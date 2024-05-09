package org.mtf.index12306.biz.ticketservice.controller;

import lombok.RequiredArgsConstructor;
import org.mtf.index12306.biz.ticketservice.dto.req.TicketPurchaseReqDTO;
import org.mtf.index12306.biz.ticketservice.dto.req.TicketPageQueryReqDTO;
import org.mtf.index12306.biz.ticketservice.dto.resp.TicketPageQueryRespDTO;
import org.mtf.index12306.biz.ticketservice.dto.resp.TicketPurchaseRespDTO;
import org.mtf.index12306.biz.ticketservice.service.TicketService;
import org.mtf.index12306.framework.starter.convention.result.Result;
import org.mtf.index12306.framework.starter.web.Results;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/api/ticket-service/tickets")
    public Result<TicketPageQueryRespDTO> pageListTicketQuery(TicketPageQueryReqDTO requestParam) {
        return Results.success(ticketService.pageListTicketQueryV1(requestParam));
    }
    /**
     * 购买车票V1
     */
    @PostMapping("/api/ticket-service/ticket/purchase")
    public Result<TicketPurchaseRespDTO> purchaseTickets(@RequestBody TicketPurchaseReqDTO requestParam) {
        return Results.success(ticketService.purchaseTicketsV1(requestParam));
    }
}
