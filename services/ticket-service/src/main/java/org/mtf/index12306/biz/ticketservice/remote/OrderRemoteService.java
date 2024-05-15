package org.mtf.index12306.biz.ticketservice.remote;

import org.mtf.index12306.biz.ticketservice.dto.req.OrderCancelReqDTO;
import org.mtf.index12306.biz.ticketservice.dto.req.OrderItemQueryReqDTO;
import org.mtf.index12306.biz.ticketservice.remote.dto.OrderCreateReqDTO;
import org.mtf.index12306.biz.ticketservice.remote.dto.OrderInfoRespDTO;
import org.mtf.index12306.biz.ticketservice.remote.dto.OrderItemRespDTO;
import org.mtf.index12306.framework.starter.convention.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 车票订单远程服务调用
 */
@FeignClient(value = "index12306-order-service", url = "${remote.order-service.url}")
public interface OrderRemoteService {

    /**
     * 跟据订单号查询车票订单
     *
     * @param orderSn 列车订单号
     * @return 列车订单记录
     */
    @GetMapping("/api/order-service/order")
    Result<OrderInfoRespDTO> queryTicketOrderByOrderSn(@RequestParam(value = "orderSn") String orderSn);


    /**
     * 跟据子订单记录id查询车票子订单详情
     */
    @GetMapping("/api/order-service/order-items")
    Result<List<OrderItemRespDTO>> queryTicketItemOrderById(@SpringQueryMap OrderItemQueryReqDTO requestParam);

    /**
     * 创建车票订单
     *
     * @param requestParam 创建车票订单请求参数
     * @return 订单号
     */
    @PostMapping("/api/order-service/order/create")
    Result<String> createTicketOrder(@RequestBody OrderCreateReqDTO requestParam);

    /**
     * 车票订单关闭
     *
     * @param requestParam 车票订单关闭入参
     * @return 关闭订单返回结果
     */
    @PostMapping("/api/order-service/order/close")
    Result<Boolean> closeTickOrder(@RequestBody OrderCancelReqDTO requestParam);

    /**
     * 车票订单取消
     *
     * @param requestParam 车票订单取消入参
     * @return 订单取消返回结果
     */
    @PostMapping("/api/order-service/order/cancel")
    Result<Boolean> cancelTicketOrder(@RequestBody OrderCancelReqDTO requestParam);
}
