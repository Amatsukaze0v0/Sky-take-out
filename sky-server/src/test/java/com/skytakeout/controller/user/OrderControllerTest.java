package com.skytakeout.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skytakeout.dto.OrdersPaymentDTO;
import com.skytakeout.dto.OrdersSubmitDTO;
import com.skytakeout.service.OrderService;
import com.skytakeout.vo.OrderPaymentVO;
import com.skytakeout.vo.OrderSubmitVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrdersSubmitDTO submitDTO;
    private OrderSubmitVO submitVO;
    private OrdersPaymentDTO paymentDTO;
    private OrderPaymentVO paymentVO;

    @BeforeEach
    void setUp() {
        // 设置提交订单的测试数据
        submitDTO = new OrdersSubmitDTO();
        submitDTO.setAddressBookId(1L);
        submitDTO.setAmount(new BigDecimal("100.00"));
        submitDTO.setRemark("测试订单");
        submitDTO.setPayMethod(1);
        submitDTO.setEstimatedDeliveryTime(LocalDateTime.now().plusHours(1));
        // 添加缺失的属性
        submitDTO.setDeliveryStatus(1); // 立即送出
        submitDTO.setTablewareNumber(2); // 餐具数量
        submitDTO.setTablewareStatus(1); // 按餐量提供
        submitDTO.setPackAmount(5); // 打包费

        submitVO = new OrderSubmitVO();
        submitVO.setId(1L);
        submitVO.setOrderNumber("1234567890");
        submitVO.setOrderTime(LocalDateTime.now());
        submitVO.setOrderAmount(new BigDecimal("100.00"));

        // 设置支付订单的测试数据
        paymentDTO = new OrdersPaymentDTO();
        paymentDTO.setOrderNumber("1234567890");

        paymentVO = new OrderPaymentVO();
        paymentVO.setNonceStr("nonceStr");
        paymentVO.setPaySign("paySign");
        paymentVO.setPackageStr("prepay_id=wx123456789");
        paymentVO.setSignType("MD5");
        paymentVO.setTimeStamp("1234567890");

        Map<String, String> additionalData = new HashMap<>();
        additionalData.put("mockPaymentUrl", "/mock-payment?orderId=1&orderNumber=1234567890&outTradeNo=wx123456789&amount=100.00");
        paymentVO.setAdditionalData(additionalData);
    }

    @Test
    void testSubmit() throws Exception {
        // 模拟服务方法
        when(orderService.submit(any(OrdersSubmitDTO.class))).thenReturn(submitVO);

        // 执行测试
        mockMvc.perform(post("/user/order/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.orderNumber").value("1234567890"))
                .andExpect(jsonPath("$.data.orderAmount").value(100.00));
    }

    @Test
    void testPayment() throws Exception {
        // 模拟服务方法
        when(orderService.payment(any(OrdersPaymentDTO.class))).thenReturn(paymentVO);

        // 执行测试
        mockMvc.perform(post("/user/order/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.nonceStr").value("nonceStr"))
                .andExpect(jsonPath("$.data.paySign").value("paySign"))
                .andExpect(jsonPath("$.data.packageStr").value("prepay_id=wx123456789"))
                .andExpect(jsonPath("$.data.signType").value("MD5"))
                .andExpect(jsonPath("$.data.timeStamp").value("1234567890"))
                .andExpect(jsonPath("$.data.additionalData.mockPaymentUrl").exists());
    }

    @Test
    void testPaySuccess() throws Exception {
        // 执行测试
        mockMvc.perform(post("/user/order/pay-success")
                        .param("outTradeNo", "wx123456789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }
}
