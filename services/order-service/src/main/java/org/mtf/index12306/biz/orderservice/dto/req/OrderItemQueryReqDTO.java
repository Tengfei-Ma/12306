package org.mtf.index12306.biz.orderservice.dto.req;

import lombok.Data;

import java.util.List;

/**
 * 订单明细查询请求参数
 */
@Data
public class OrderItemQueryReqDTO {
    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 子订单记录id
     */
    private List<Long> orderItemIds;
}
