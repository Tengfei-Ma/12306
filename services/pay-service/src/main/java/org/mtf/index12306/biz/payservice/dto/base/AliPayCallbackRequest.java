package org.mtf.index12306.biz.payservice.dto.base;

import lombok.Data;
import org.mtf.index12306.biz.payservice.common.enums.PayChannelEnum;

import java.util.Date;

/**
 * 支付宝回调请求入参
 * 公众号：马丁玩编程，回复：加群，添加马哥微信（备注：12306）获取项目资料
 */
@Data
public final class AliPayCallbackRequest extends AbstractPayCallbackRequest {

    /**
     * 支付渠道
     */
    private String channel;

    /**
     * 支付状态
     */
    private String tradeStatus;

    /**
     * 支付凭证号
     */
    private String tradeNo;

    /**
     * 买家付款时间
     */
    private Date gmtPayment;

    /**
     * 买家付款金额
     */
    private Integer buyerPayAmount;

    @Override
    public AliPayCallbackRequest getAliPayCallBackRequest() {
        return this;
    }

    @Override
    public String buildMark() {
        return PayChannelEnum.ALI_PAY.getName();
    }
}
