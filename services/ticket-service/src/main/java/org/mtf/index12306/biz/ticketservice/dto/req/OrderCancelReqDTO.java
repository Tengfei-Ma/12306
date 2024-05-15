package org.mtf.index12306.biz.ticketservice.dto.req;

import lombok.Data;

/**
 * 取消车票订单请求入参
 */
@Data
public class OrderCancelReqDTO {

    /**
     * 订单号
     */
    private String orderSn;
}
