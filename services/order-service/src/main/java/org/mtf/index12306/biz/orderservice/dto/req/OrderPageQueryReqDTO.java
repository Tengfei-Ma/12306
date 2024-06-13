package org.mtf.index12306.biz.orderservice.dto.req;

import lombok.Data;
import org.mtf.index12306.framework.starter.convention.page.PageRequest;

/**
 * 订单分页查询
 */
@Data
public class OrderPageQueryReqDTO extends PageRequest {
    /**
     * 用户唯一标识
     */
    private String userId;

    /**
     * 状态类型
     * 0：未完成   订单待支付
     * 1：未出行   订单已支付，订单部分退款，订单全部退款
     * 2：历史订单 订单已完成
     */
    private Integer statusType;
}
