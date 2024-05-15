package org.mtf.index12306.biz.ticketservice.remote;

import org.mtf.index12306.biz.ticketservice.remote.dto.PayInfoRespDTO;
import org.mtf.index12306.biz.ticketservice.remote.dto.RefundReqDTO;
import org.mtf.index12306.biz.ticketservice.remote.dto.RefundRespDTO;
import org.mtf.index12306.framework.starter.convention.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 支付单远程调用服务
 */
@FeignClient(value = "index12306-pay-service", url = "${remote.pay-service.url}")
public interface PayRemoteService {

    /**
     * 支付单详情查询
     */
    @GetMapping("/api/pay-service/pay/order-sn")
    Result<PayInfoRespDTO> getPayInfo(@RequestParam(value = "orderSn") String orderSn);

    /**
     * 公共退款接口
     */
    @PostMapping("/api/pay-service/refund")
    Result<RefundRespDTO> commonRefund(@RequestBody RefundReqDTO requestParam);
}
