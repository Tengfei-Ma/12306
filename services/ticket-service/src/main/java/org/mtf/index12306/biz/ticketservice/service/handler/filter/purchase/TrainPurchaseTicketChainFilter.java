package org.mtf.index12306.biz.ticketservice.service.handler.filter.purchase;

import org.mtf.index12306.biz.ticketservice.dto.req.TicketPurchaseReqDTO;
import org.mtf.index12306.framework.starter.designpattern.chain.AbstractChainHandler;
import static org.mtf.index12306.biz.ticketservice.common.enums.TicketChainMarkEnum.TRAIN_PURCHASE_TICKET_FILTER;

/**
 * 列车购买车票过滤器
 */
public interface TrainPurchaseTicketChainFilter<T extends TicketPurchaseReqDTO> extends AbstractChainHandler<TicketPurchaseReqDTO> {
    @Override
    default String mark(){
        return TRAIN_PURCHASE_TICKET_FILTER.name();
    }
}
