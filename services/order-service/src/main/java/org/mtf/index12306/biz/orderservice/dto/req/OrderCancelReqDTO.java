package org.mtf.index12306.biz.orderservice.dto.req;

import lombok.Data;

/**
 * 取消订单请求参数
 */
@Data
public class OrderCancelReqDTO {
    /**
     * 订单号
     */
    private String orderSn;
}
