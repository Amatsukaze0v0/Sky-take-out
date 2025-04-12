package com.skytakeout.repository;

import com.skytakeout.dto.GoodsSalesDTO;
import com.skytakeout.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {


    /**
     * 根据订单状态和下单时间查询订单
     * @param orderTime 下单时间
     * @param status 订单状态
     * @return 订单状态列表
     * */
    @Query("select 1 from Orders o where o.status = :status and o.orderTime < :orderTime")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    Orders findByNumber(String orderNumber);

    Orders findByNumberAndUserId(String outTradeNo, Long userId);

    /**
     * 根据动态条件统计营业额
     * */
    @Query("select sum(o.amount) from Orders o where o.orderTime > :begin and o.orderTime < :end and o.status = :status")
    Double sumByTimeRangeAndStatus(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end, @Param("status") Integer status);

    @Query("select count(o.id) from Orders o where o.orderTime > :begin and o.orderTime < :end")
    Integer countByTimeRange(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);

    @Query("select count(o.id) from Orders o where o.orderTime > :begin and o.orderTime < :end and o.status = :status")
    Integer countByTimeRangeAndStatus(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end, @Param("status") Integer status);

    @Query("select od.name, count(od.amount) as number from OrderDetail od, Orders o where od.orderId = o.id and o.status = 5 and o.orderTime > :begin and o.orderTime < :end " +
            "group by od.name order by number desc limit 10")
    List<GoodsSalesDTO> getSalesTop10(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);
}
