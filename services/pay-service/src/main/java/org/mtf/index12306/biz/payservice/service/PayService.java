package org.mtf.index12306.biz.payservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mtf.index12306.biz.payservice.dao.entity.PayDO;
import org.mtf.index12306.biz.payservice.dto.base.PayRequest;
import org.mtf.index12306.biz.payservice.dto.req.PayCallbackReqDTO;
import org.mtf.index12306.biz.payservice.dto.resp.PayInfoRespDTO;
import org.mtf.index12306.biz.payservice.dto.resp.PayRespDTO;

/**
 * 支付接口层
 */
public interface PayService extends IService<PayDO> {
    /**
     * 根据订单号查询支付单
     *
     * @param orderSn 订单号
     * @return 支付单响应参数
     */
    PayInfoRespDTO getPayInfoByOrderSn(String orderSn);

    /**
     * 根据支付流水号查询支付单
     *
     * @param paySn 支付流水号
     * @return 支付单响应参数
     */
    PayInfoRespDTO getPayInfoByPaySn(String paySn);

    /**
     * 创建支付单
     *
     * @param requestParam 创建支付单实体
     * @return 支付返回详情
     */
    PayRespDTO commonPay(PayRequest requestParam);

    /**
     * 支付单回调
     *
     * @param requestParam 回调支付单实体
     */
    void callbackPay(PayCallbackReqDTO requestParam);
}
