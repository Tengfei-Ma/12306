package org.mtf.index12306.biz.payservice.dto.resp;

import lombok.Data;

import java.util.Date;

/**
 * 根据订单号查询支付单响应参数
 */
@Data
public class PayInfoRespDTO {
    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 支付总金额
     */
    private Integer totalAmount;

    /**
     * 支付状态
     */
    private Integer status;

    /**
     * 支付时间
     */
    private Date gmtPayment;
}
