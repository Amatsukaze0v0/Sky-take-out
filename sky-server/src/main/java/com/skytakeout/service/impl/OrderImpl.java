package com.skytakeout.service.impl;

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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private AddressBookRepository addressBookRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

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
        return null;
    }

    @Override
    public void paySuccess(String outTradeNo) {

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
}
