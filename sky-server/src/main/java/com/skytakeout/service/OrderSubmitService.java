package com.skytakeout.service;

import com.skytakeout.dto.*;
import com.skytakeout.result.PageResult;
import com.skytakeout.vo.OrderPaymentVO;
import com.skytakeout.vo.OrderStatisticsVO;
import com.skytakeout.vo.OrderSubmitVO;
import com.skytakeout.vo.OrderVO;

/**
 * 订单提交
 */
public interface OrderSubmitService {
    /**
     * 订单提交
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 分页查询历史订单
     * @param page,pageSize,status
     * @return
     */
    PageResult page(int page, int pageSize, Integer status);

    /**
     * 查看订单详情
     * @param id
     * @return
     */
    OrderVO showOrderDetail(Long id);

    /**
     * 取消订单
     * @param id
     */
    void cancel(Long id) throws Exception;


//    /**
//     * 再来一单
//     * @param id
//     * @return
//     */
//    List<ShoppingCartDTO> repetition(Long id);


    /**
     * 再来一单
     * @param id
     */
    void repetition(Long id);

    /**
     * 订单条件搜索
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 统计订单数据
     * @return
     */
    OrderStatisticsVO getStatistics();

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    /**
     * 管理端取消订单
     * @param ordersCancelDTO
     */
    void adminCancel(OrdersCancelDTO ordersCancelDTO) throws Exception;

    /**
     * 派送订单
     * @param id
     */
    void delivery(Long id);

    /**
     * 完成订单
     * @param id
     */
    void complete(Long id);
}