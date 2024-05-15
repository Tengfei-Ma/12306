package org.mtf.index12306.biz.ticketservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mtf.index12306.biz.ticketservice.dao.entity.TicketDO;
import org.mtf.index12306.biz.ticketservice.dto.req.OrderCancelReqDTO;
import org.mtf.index12306.biz.ticketservice.dto.req.RefundTicketReqDTO;
import org.mtf.index12306.biz.ticketservice.dto.req.TicketPageQueryReqDTO;
import org.mtf.index12306.biz.ticketservice.dto.req.TicketPurchaseReqDTO;
import org.mtf.index12306.biz.ticketservice.dto.resp.RefundTicketRespDTO;
import org.mtf.index12306.biz.ticketservice.dto.resp.TicketPageQueryRespDTO;
import org.mtf.index12306.biz.ticketservice.dto.resp.TicketPurchaseRespDTO;
import org.mtf.index12306.biz.ticketservice.remote.dto.PayInfoRespDTO;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 车票接口层
 */
public interface TicketService extends IService<TicketDO> {
    /**
     * 根据条件分页查询车票
     *
     * @param requestParam 分页查询车票请求参数
     * @return 查询车票返回结果
     */
    TicketPageQueryRespDTO pageListTicketQueryV1(TicketPageQueryReqDTO requestParam);

    /**
     * 购买车票V1
     *
     * @param requestParam 车票购买请求参数
     * @return 订单号
     */
    TicketPurchaseRespDTO purchaseTicketsV1(TicketPurchaseReqDTO requestParam);

    /**
     * 购买车票V2高性能版本
     *
     * @param requestParam 车票购买请求参数
     * @return 订单号
     */
    TicketPurchaseRespDTO purchaseTicketsV2(TicketPurchaseReqDTO requestParam);
    /**
     * 取消车票订单
     *
     * @param requestParam 取消车票订单入参
     */
    void cancelTicketOrder(OrderCancelReqDTO requestParam);

    /**
     * 支付单详情查询
     *
     * @param orderSn 订单号
     * @return 支付单详情
     */
    PayInfoRespDTO getPayInfo(String orderSn);

    /**
     * 公共退款接口
     *
     * @param requestParam 退款请求参数
     * @return 退款返回详情
     */
    RefundTicketRespDTO commonTicketRefund(RefundTicketReqDTO requestParam);



    /**
     * 执行购买车票
     *
     * @param requestParam 车票购买请求参数
     * @return 订单号
     */
    TicketPurchaseRespDTO executePurchaseTickets(@RequestBody TicketPurchaseReqDTO requestParam);
}
