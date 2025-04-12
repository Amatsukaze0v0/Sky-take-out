package com.skytakeout.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skytakeout.constant.MessageConstant;
import com.skytakeout.context.BaseContext;
import com.skytakeout.dto.*;
import com.skytakeout.entity.OrderDetail;
import com.skytakeout.entity.Orders;
import com.skytakeout.entity.ShoppingCart;
import com.skytakeout.exception.AddressBookBusinessException;
import com.skytakeout.exception.ShoppingCartBusinessException;
import com.skytakeout.repository.AddressBookRepository;
import com.skytakeout.repository.OrderDetailRepository;
import com.skytakeout.repository.OrderRepository;
import com.skytakeout.repository.ShoppingCartRepository;
import com.skytakeout.result.PageResult;
import com.skytakeout.service.OrderService;
import com.skytakeout.vo.OrderPaymentVO;
import com.skytakeout.vo.OrderStatisticsVO;
import com.skytakeout.vo.OrderSubmitVO;
import com.skytakeout.vo.OrderVO;
import com.skytakeout.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private AddressBookRepository addressBookRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private WebSocketServer webSocketServer;

    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        //处理业务异常
        //1. 地址簿为空
        if (!addressBookRepository.existsById(ordersSubmitDTO.getAddressBookId())) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //2. 购物车为空
        Long userID = BaseContext.getCurrentID();

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userID);
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.list(shoppingCart);

        if (shoppingCartList == null || shoppingCartList.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_EMPTY);
        }

        //向订单表插入1条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        //默认为待支付状态
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        //时间戳作为订单号
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBookRepository.getReferenceById(ordersSubmitDTO.getAddressBookId()).getPhone());
        orders.setUserId(userID);

        orderRepository.save(orders);

        //向订单明细表插入n条数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart sc : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(sc, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailRepository.saveAll(orderDetailList);

        //清空购物车表
        shoppingCartRepository.deleteByUserId(userID);

        //封装VO返回值
        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        orderSubmitVO.setId(orders.getId());
        orderSubmitVO.setOrderTime(orders.getOrderTime());
        orderSubmitVO.setOrderNumber(orders.getNumber());
        orderSubmitVO.setOrderAmount(orders.getAmount());

        return orderSubmitVO;
    }

    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 1. 查询订单
        Orders orders = orderRepository.findByNumber(ordersPaymentDTO.getOrderNumber());
        if (orders == null) {
            throw new Exception("订单不存在");
        }

        // 2. 判断订单状态，只有待支付状态才能支付
        if (!orders.getStatus().equals(Orders.PENDING_PAYMENT)) {
            throw new Exception("该订单状态不正确，无法支付");
        }

        // 3. 生成模拟的预支付交易会话标识
        String prePayId = "wx" + System.currentTimeMillis();

        // 4. 保存预支付交易单号到订单的 outTradeNo 字段
        orders.setNumber(prePayId);
        orderRepository.save(orders);

        // 5. 封装并返回支付信息
        OrderPaymentVO vo = new OrderPaymentVO();
        vo.setNonceStr(String.valueOf(System.nanoTime()));
        vo.setPaySign(generateMockSign(orders.getNumber(), orders.getAmount()));
        vo.setPackageStr("prepay_id=" + prePayId);
        vo.setSignType("MD5");
        vo.setTimeStamp(String.valueOf(System.currentTimeMillis() / 1000));

        // 6. 添加模拟支付跳转链接到响应对象的附加数据中
        // 注意：这里假设 OrderPaymentVO 有一个 additionalData 字段用于存储额外信息
        // 如果没有，需要修改 OrderPaymentVO 类添加此字段
        Map<String, String> additionalData = new HashMap<>();
        additionalData.put("mockPaymentUrl", "/mock-payment?orderId=" + orders.getId() +
                "&orderNumber=" + orders.getNumber() +
                "&outTradeNo=" + prePayId +
                "&amount=" + orders.getAmount());
        vo.setAdditionalData(additionalData);

        return vo;
    }

    @Override
    public void paySuccess(String outTradeNo) {
        // 1. 根据预支付交易单号和用户ID查询订单
        Long userId = BaseContext.getCurrentID();
        Orders orders = orderRepository.findByNumberAndUserId(outTradeNo, userId);
        if (orders == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 更新订单状态
        orders.setStatus(Orders.TO_BE_CONFIRMED);  // 待接单状态
        orders.setPayStatus(Orders.PAID);  // 已支付
        orders.setCheckoutTime(LocalDateTime.now());  // 支付时间

        // 3. 保存订单状态
        orderRepository.save(orders);

        //通过websocket向客户端推送消息 type orderID content
        Map<String, Object> map = new HashMap();
        map.put("type", 1);   //1表示来单提醒，2为客户催单
        map.put("orderId", orders.getId());
        map.put("content", "订单号：" + outTradeNo);

        //转为json格式
        String jsonString = null;
        try {
            jsonString = new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // 发送WebSocket消息
        webSocketServer.sendToAllClient(jsonString);
    }

    @Override
    public PageResult page(int page, int pageSize, Integer status) {
        return null;
    }

    @Override
    public OrderVO showOrderDetail(Long id) {
        return null;
    }

    @Override
    public void cancel(Long id) throws Exception {

    }

    @Override
    public void repetition(Long id) {

    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        return null;
    }

    @Override
    public OrderStatisticsVO getStatistics() {
        return null;
    }

    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {

    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception {

    }

    @Override
    public void adminCancel(OrdersCancelDTO ordersCancelDTO) throws Exception {

    }

    @Override
    public void delivery(Long id) {

    }

    @Override
    public void complete(Long id) {

    }
    /**
     * 客户催单功能
     * */
    @Override
    public void reminder(Long id) {
        //根据ID查询订单
        Orders order = orderRepository.findById(id).orElseThrow();

        //构造json字符串
        Map<String, Object> hashMap = new HashMap();
        hashMap.put("type", 2);
        hashMap.put("orderId", id);
        hashMap.put("content", "订单号:" + order.getNumber());

        //转为json格式
        String jsonString = null;
        try {
            jsonString = new ObjectMapper().writeValueAsString(hashMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //websocket向客户端推送催单消息
        webSocketServer.sendToAllClient(jsonString);

    }

    /**
     * 生成模拟的签名
     * @param orderNumber 订单号
     * @param amount 金额
     * @return 签名字符串
     */
    private String generateMockSign(String orderNumber, BigDecimal amount) {
        // 简单模拟签名生成过程
        String plainText = orderNumber + amount + "mock_key";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(plainText.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }
}
