package com.skytakeout.service.impl;

import com.skytakeout.constant.MessageConstant;
import com.skytakeout.context.BaseContext;
import com.skytakeout.dto.OrdersPaymentDTO;
import com.skytakeout.dto.OrdersSubmitDTO;
import com.skytakeout.entity.AddressBook;
import com.skytakeout.entity.Orders;
import com.skytakeout.entity.ShoppingCart;
import com.skytakeout.exception.AddressBookBusinessException;
import com.skytakeout.exception.ShoppingCartBusinessException;
import com.skytakeout.repository.AddressBookRepository;
import com.skytakeout.repository.OrderDetailRepository;
import com.skytakeout.repository.OrderRepository;
import com.skytakeout.repository.ShoppingCartRepository;
import com.skytakeout.vo.OrderPaymentVO;
import com.skytakeout.vo.OrderSubmitVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private AddressBookRepository addressBookRepository;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private final Long USER_ID = 1L;
    private final Long ADDRESS_ID = 1L;
    private final String ORDER_NUMBER = "1234567890";

    @BeforeEach
    void setUp() {
        // 设置一些测试数据
    }

    @Test
    void testSubmit_Success() {
        // 准备测试数据
        OrdersSubmitDTO submitDTO = new OrdersSubmitDTO();
        submitDTO.setAddressBookId(ADDRESS_ID);
        submitDTO.setAmount(new BigDecimal("100.00"));
        submitDTO.setRemark("测试订单");
        submitDTO.setPayMethod(1);
        submitDTO.setEstimatedDeliveryTime(LocalDateTime.now().plusHours(1));

        // 添加缺失的属性
        submitDTO.setDeliveryStatus(1); // 立即送出
        submitDTO.setTablewareNumber(2); // 餐具数量
        submitDTO.setTablewareStatus(1); // 按餐量提供
        submitDTO.setPackAmount(5); // 打包费

        AddressBook addressBook = new AddressBook();
        addressBook.setId(ADDRESS_ID);
        addressBook.setPhone("13800138000");

        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        ShoppingCart cart1 = new ShoppingCart();
        cart1.setId(1L);
        cart1.setName("测试菜品1");
        cart1.setAmount(new BigDecimal("50.00"));
        cart1.setNumber(2);
        shoppingCartList.add(cart1);

        // 模拟方法调用
        try (MockedStatic<BaseContext> baseContext = mockStatic(BaseContext.class)) {
            baseContext.when(BaseContext::getCurrentID).thenReturn(USER_ID);

            when(addressBookRepository.existsById(ADDRESS_ID)).thenReturn(true);
            when(addressBookRepository.getReferenceById(ADDRESS_ID)).thenReturn(addressBook);

            ShoppingCart queryCart = new ShoppingCart();
            queryCart.setUserId(USER_ID);
            when(shoppingCartRepository.list(any(ShoppingCart.class))).thenReturn(shoppingCartList);

            when(orderRepository.save(any(Orders.class))).thenAnswer(invocation -> {
                Orders order = invocation.getArgument(0);
                order.setId(1L);
                return order;
            });

            // 执行测试
            OrderSubmitVO result = orderService.submit(submitDTO);

            // 验证结果
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertNotNull(result.getOrderNumber());
            assertEquals(new BigDecimal("100.00"), result.getOrderAmount());

            // 验证方法调用
            verify(orderRepository, times(1)).save(any(Orders.class));
            verify(orderDetailRepository, times(1)).saveAll(anyList());
            verify(shoppingCartRepository, times(1)).deleteByUserId(USER_ID);
        }
    }
    @Test
    void testSubmit_AddressBookNotExist() {
        // 准备测试数据
        OrdersSubmitDTO submitDTO = new OrdersSubmitDTO();
        submitDTO.setAddressBookId(ADDRESS_ID);
        // 添加其他必要属性
        submitDTO.setAmount(new BigDecimal("100.00"));
        submitDTO.setRemark("测试订单");
        submitDTO.setPayMethod(1);
        submitDTO.setEstimatedDeliveryTime(LocalDateTime.now().plusHours(1));
        submitDTO.setDeliveryStatus(1);
        submitDTO.setTablewareNumber(2);
        submitDTO.setTablewareStatus(1);
        submitDTO.setPackAmount(5);

        // 模拟方法调用
        when(addressBookRepository.existsById(ADDRESS_ID)).thenReturn(false);

        // 执行测试并验证异常
        try (MockedStatic<BaseContext> baseContext = mockStatic(BaseContext.class)) {
            baseContext.when(BaseContext::getCurrentID).thenReturn(USER_ID);

            assertThrows(AddressBookBusinessException.class, () -> orderService.submit(submitDTO));
        }
    }

    @Test
    void testSubmit_EmptyShoppingCart() {
        // 准备测试数据
        OrdersSubmitDTO submitDTO = new OrdersSubmitDTO();
        submitDTO.setAddressBookId(ADDRESS_ID);
        // 添加其他必要属性
        submitDTO.setAmount(new BigDecimal("100.00"));
        submitDTO.setRemark("测试订单");
        submitDTO.setPayMethod(1);
        submitDTO.setEstimatedDeliveryTime(LocalDateTime.now().plusHours(1));
        submitDTO.setDeliveryStatus(1);
        submitDTO.setTablewareNumber(2);
        submitDTO.setTablewareStatus(1);
        submitDTO.setPackAmount(5);

        // 模拟方法调用
        try (MockedStatic<BaseContext> baseContext = mockStatic(BaseContext.class)) {
            baseContext.when(BaseContext::getCurrentID).thenReturn(USER_ID);

            when(addressBookRepository.existsById(ADDRESS_ID)).thenReturn(true);
            when(shoppingCartRepository.list(any(ShoppingCart.class))).thenReturn(new ArrayList<>());

            // 执行测试并验证异常
            assertThrows(ShoppingCartBusinessException.class, () -> orderService.submit(submitDTO));
        }
    }
    @Test
    void testPayment_Success() throws Exception {
        // 准备测试数据
        OrdersPaymentDTO paymentDTO = new OrdersPaymentDTO();
        paymentDTO.setOrderNumber(ORDER_NUMBER);

        Orders order = new Orders();
        order.setId(1L);
        order.setNumber(ORDER_NUMBER);
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setAmount(new BigDecimal("100.00"));

        // 模拟方法调用
        when(orderRepository.findByNumber(ORDER_NUMBER)).thenReturn(order);
        when(orderRepository.save(any(Orders.class))).thenReturn(order);

        // 执行测试
        OrderPaymentVO result = orderService.payment(paymentDTO);

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getNonceStr());
        assertNotNull(result.getPaySign());
        assertNotNull(result.getPackageStr());
        assertEquals("MD5", result.getSignType());
        assertNotNull(result.getTimeStamp());

        // 验证附加数据
        Map<String, String> additionalData = result.getAdditionalData();
        assertNotNull(additionalData);
        assertTrue(additionalData.containsKey("mockPaymentUrl"));
        String mockPaymentUrl = additionalData.get("mockPaymentUrl");
        assertTrue(mockPaymentUrl.contains("orderId=1"));
        assertTrue(mockPaymentUrl.contains("orderNumber="));
        assertTrue(mockPaymentUrl.contains("outTradeNo="));
        assertTrue(mockPaymentUrl.contains("amount=100.00"));
    }

    @Test
    void testPayment_OrderNotExist() {
        // 准备测试数据
        OrdersPaymentDTO paymentDTO = new OrdersPaymentDTO();
        paymentDTO.setOrderNumber(ORDER_NUMBER);

        // 模拟方法调用
        when(orderRepository.findByNumber(ORDER_NUMBER)).thenReturn(null);

        // 执行测试并验证异常
        assertThrows(Exception.class, () -> orderService.payment(paymentDTO));
    }

    @Test
    void testPayment_InvalidOrderStatus() {
        // 准备测试数据
        OrdersPaymentDTO paymentDTO = new OrdersPaymentDTO();
        paymentDTO.setOrderNumber(ORDER_NUMBER);

        Orders order = new Orders();
        order.setId(1L);
        order.setNumber(ORDER_NUMBER);
        order.setStatus(Orders.CONFIRMED); // 不是待支付状态

        // 模拟方法调用
        when(orderRepository.findByNumber(ORDER_NUMBER)).thenReturn(order);

        // 执行测试并验证异常
        assertThrows(Exception.class, () -> orderService.payment(paymentDTO));
    }

    @Test
    void testPaySuccess() {
        // 准备测试数据
        String outTradeNo = "wx" + System.currentTimeMillis();

        Orders order = new Orders();
        order.setId(1L);
        order.setNumber(outTradeNo);
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setPayStatus(Orders.UN_PAID);

        // 模拟方法调用
        try (MockedStatic<BaseContext> baseContext = mockStatic(BaseContext.class)) {
            baseContext.when(BaseContext::getCurrentID).thenReturn(USER_ID);

            when(orderRepository.findByNumberAndUserId(outTradeNo, USER_ID)).thenReturn(order);
            when(orderRepository.save(any(Orders.class))).thenReturn(order);

            // 执行测试
            orderService.paySuccess(outTradeNo);

            // 验证结果
            assertEquals(Orders.TO_BE_CONFIRMED, order.getStatus());
            assertEquals(Orders.PAID, order.getPayStatus());
            assertNotNull(order.getCheckoutTime());

            // 验证方法调用
            verify(orderRepository, times(1)).save(order);
        }
    }

    @Test
    void testPaySuccess_OrderNotExist() {
        // 准备测试数据
        String outTradeNo = "wx" + System.currentTimeMillis();

        // 模拟方法调用
        try (MockedStatic<BaseContext> baseContext = mockStatic(BaseContext.class)) {
            baseContext.when(BaseContext::getCurrentID).thenReturn(USER_ID);

            when(orderRepository.findByNumberAndUserId(outTradeNo, USER_ID)).thenReturn(null);

            // 执行测试并验证异常
            assertThrows(RuntimeException.class, () -> orderService.paySuccess(outTradeNo));
        }
    }

    @Test
    void testGenerateMockSign() throws Exception {
        // 使用反射调用私有方法
        String result = (String) ReflectionTestUtils.invokeMethod(
                orderService,
                "generateMockSign",
                ORDER_NUMBER,
                new BigDecimal("100.00"));

        // 验证结果
        assertNotNull(result);
        assertEquals(32, result.length()); // MD5 哈希值长度为32
    }
}