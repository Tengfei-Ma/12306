package org.mtf.index12306.biz.payservice.controller;

import lombok.RequiredArgsConstructor;
import org.mtf.index12306.biz.payservice.dto.req.RefundReqDTO;
import org.mtf.index12306.biz.payservice.dto.resp.RefundRespDTO;
import org.mtf.index12306.biz.payservice.service.RefundService;
import org.mtf.index12306.framework.starter.convention.result.Result;
import org.mtf.index12306.framework.starter.web.Results;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 退款控制层
 */
@RestController
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    /**
     * 公共退款接口
     */
    @PostMapping("/api/pay-service/refund")
    public Result<RefundRespDTO> commonRefund(@RequestBody RefundReqDTO requestParam) {
        return Results.success(refundService.commonRefund(requestParam));
    }
}
