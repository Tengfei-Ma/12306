package org.mtf.index12306.biz.payservice.controller;

import lombok.RequiredArgsConstructor;
import org.mtf.index12306.biz.payservice.convert.PayRequestConvert;
import org.mtf.index12306.biz.payservice.dto.base.PayRequest;
import org.mtf.index12306.biz.payservice.dto.req.PayCommand;
import org.mtf.index12306.biz.payservice.dto.resp.PayInfoRespDTO;
import org.mtf.index12306.biz.payservice.dto.resp.PayRespDTO;
import org.mtf.index12306.biz.payservice.service.PayService;
import org.mtf.index12306.framework.starter.convention.result.Result;
import org.mtf.index12306.framework.starter.web.Results;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制层
 */
@RestController
@RequiredArgsConstructor
public class PayController {
    private final PayService payService;

    /**
     * 公共支付接口
     * 对接常用支付方式，比如：支付宝、微信以及银行卡等
     */
    @PostMapping("/api/pay-service/pay")
    public Result<PayRespDTO> pay(@RequestBody PayCommand requestParam){
        PayRequest payRequest = PayRequestConvert.command2PayRequest(requestParam);
        PayRespDTO result =payService.commonPay(payRequest);
        return Results.success(result);
    }
    /**
     * 跟据订单号查询支付单详情
     */
    @GetMapping("/api/pay-service/pay/order-sn")
    public Result<PayInfoRespDTO> getPayInfoByOrderSn(@RequestParam(value = "orderSn") String orderSn) {
        return Results.success(payService.getPayInfoByOrderSn(orderSn));
    }

    /**
     * 跟据支付流水号查询支付单详情
     */
    @GetMapping("/api/pay-service/pay/pay-sn")
    public Result<PayInfoRespDTO> getPayInfoByPaySn(@RequestParam(value = "paySn") String paySn) {
        return Results.success(payService.getPayInfoByPaySn(paySn));
    }
}
