package org.mtf.index12306.biz.ticketservice.controller;

import lombok.RequiredArgsConstructor;
import org.mtf.index12306.biz.ticketservice.dto.req.OrderCancelReqDTO;
import org.mtf.index12306.biz.ticketservice.dto.req.RefundTicketReqDTO;
import org.mtf.index12306.biz.ticketservice.dto.req.TicketPurchaseReqDTO;
import org.mtf.index12306.biz.ticketservice.dto.req.TicketPageQueryReqDTO;
import org.mtf.index12306.biz.ticketservice.dto.resp.RefundTicketRespDTO;
import org.mtf.index12306.biz.ticketservice.dto.resp.TicketPageQueryRespDTO;
import org.mtf.index12306.biz.ticketservice.dto.resp.TicketPurchaseRespDTO;
import org.mtf.index12306.biz.ticketservice.remote.dto.PayInfoRespDTO;
import org.mtf.index12306.biz.ticketservice.service.TicketService;
import org.mtf.index12306.framework.starter.convention.result.Result;
import org.mtf.index12306.framework.starter.idempotent.annotation.Idempotent;
import org.mtf.index12306.framework.starter.idempotent.enums.IdempotentSceneEnum;
import org.mtf.index12306.framework.starter.idempotent.enums.IdempotentTypeEnum;
import org.mtf.index12306.framework.starter.log.annotation.ILog;
import org.mtf.index12306.framework.starter.web.Results;
import org.springframework.web.bind.annotation.*;

/**
 * 车票控制层
 */
@RestController
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    /**
     * 根据出发地区和目的地区分页查询车票
     */
    @GetMapping("/api/ticket-service/ticket")
    public Result<TicketPageQueryRespDTO> pageListTicketQuery(TicketPageQueryReqDTO requestParam) {
        return Results.success(ticketService.pageListTicketQueryV1(requestParam));
    }

    /**
     * 购买车票V1
     */
    @ILog
    @Idempotent(
            uniqueKeyPrefix = "index12306-ticket:lock_purchase-tickets:",
            key = "T(org.mtf.index12306.framework.starter.bases.ApplicationContextHolder).getBean('environment').getProperty('unique-name', '')"
                    + "+'_'+"
                    + "T(org.mtf.index12306.frameworks.starter.user.core.UserContext).getUsername()",
            message = "正在执行下单流程，请稍后...",
            scene = IdempotentSceneEnum.RESTAPI,
            type = IdempotentTypeEnum.SPEL
    )
    @PostMapping("/api/ticket-service/ticket/purchase/v1")
    public Result<TicketPurchaseRespDTO> purchaseTicketsV1(@RequestBody TicketPurchaseReqDTO requestParam) {
        return Results.success(ticketService.purchaseTicketsV1(requestParam));
    }

    /**
     * 购买车票v2
     */
    @ILog
    @Idempotent(
            uniqueKeyPrefix = "index12306-ticket:lock_purchase-tickets:",
            key = "T(org.mtf.index12306.framework.starter.bases.ApplicationContextHolder).getBean('environment').getProperty('unique-name', '')"
                    + "+'_'+"
                    + "T(org.mtf.index12306.frameworks.starter.user.core.UserContext).getUsername()",
            message = "正在执行下单流程，请稍后...",
            scene = IdempotentSceneEnum.RESTAPI,
            type = IdempotentTypeEnum.SPEL
    )
    @PostMapping("/api/ticket-service/ticket/purchase/v2")
    public Result<TicketPurchaseRespDTO> purchaseTicketsV2(@RequestBody TicketPurchaseReqDTO requestParam) {
        return Results.success(ticketService.purchaseTicketsV2(requestParam));
    }

    /**
     * 取消车票订单
     */
    @ILog
    @PostMapping("/api/ticket-service/ticket/cancel")
    public Result<Void> cancelTicketOrder(@RequestBody OrderCancelReqDTO requestParam) {
        ticketService.cancelTicketOrder(requestParam);
        return Results.success();
    }

    /**
     * 支付单详情查询
     */
    @GetMapping("/api/ticket-service/ticket/pay")
    public Result<PayInfoRespDTO> getPayInfo(@RequestParam(value = "orderSn") String orderSn) {
        return Results.success(ticketService.getPayInfo(orderSn));
    }

    /**
     * 公共退款接口
     */
    @PostMapping("/api/ticket-service/ticket/refund")
    public Result<RefundTicketRespDTO> commonTicketRefund(@RequestBody RefundTicketReqDTO requestParam) {
        return Results.success(ticketService.commonTicketRefund(requestParam));
    }
}
