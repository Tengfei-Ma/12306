package org.mtf.index12306.biz.orderservice.controller;

import cn.crane4j.annotation.AutoOperate;
import lombok.RequiredArgsConstructor;
import org.mtf.index12306.biz.orderservice.dto.req.*;
import org.mtf.index12306.biz.orderservice.dto.resp.OrderInfoRespDTO;
import org.mtf.index12306.biz.orderservice.dto.resp.OrderItemRespDTO;
import org.mtf.index12306.biz.orderservice.dto.resp.SelfOrderInfoRespDTO;
import org.mtf.index12306.biz.orderservice.service.OrderItemService;
import org.mtf.index12306.biz.orderservice.service.OrderService;
import org.mtf.index12306.framework.starter.convention.page.PageResponse;
import org.mtf.index12306.framework.starter.convention.result.Result;
import org.mtf.index12306.framework.starter.web.Results;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 订单控制层
 */
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderItemService orderItemService;

    /**
     * 根据订单号查询订单
     */
    @GetMapping("/api/order-service/order")
    public Result<OrderInfoRespDTO> queryOrderByOrderSn(@RequestParam(value = "orderSn") String orderSn) {
        return Results.success(orderService.queryOrderByOrderSn(orderSn));
    }

    /**
     * 根据订单号和订单明细id集合查询订单明细
     */
    @GetMapping("/api/order-service/order-items")
    public Result<List<OrderItemRespDTO>> queryOrderItemByIds(OrderItemQueryReqDTO requestParam) {
        return Results.success(orderItemService.queryOrderItemByIds(requestParam));
    }

    /**
     * 分页查询订单
     */
    @AutoOperate(type = OrderInfoRespDTO.class, on = "data.records")
    @GetMapping("/api/order-service/order/page")
    public Result<PageResponse<OrderInfoRespDTO>> pageOrder(OrderPageQueryReqDTO requestParam) {
        return Results.success(orderService.pageOrder(requestParam));
    }

    /**
     * 分页查询本人订单
     */
    @GetMapping("/api/order-service/order-items/page")
    public Result<PageResponse<SelfOrderInfoRespDTO>> pageSelfOrder(SelfOrderPageQueryReqDTO requestParam) {
        return Results.success(orderService.pageSelfOrder(requestParam));
    }

    /**
     * 创建订单
     */
    @PostMapping("/api/order-service/order/create")
    public Result<String> createOrder(@RequestBody OrderCreateReqDTO requestParam) {
        return Results.success(orderService.createOrder(requestParam));
    }

    /**
     * 关闭订单
     */
    @PostMapping("/api/order-service/order/close")
    public Result<Boolean> closeOrder(@RequestBody OrderCancelReqDTO requestParam) {
        return Results.success(orderService.closeOrder(requestParam));
    }

    /**
     * 取消订单
     */
    @PostMapping("/api/order-service/order/cancel")
    public Result<Boolean> cancelOrder(@RequestBody OrderCancelReqDTO requestParam) {
        return Results.success(orderService.cancelOrder(requestParam));
    }
}
