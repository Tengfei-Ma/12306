package org.mtf.index12306.biz.payservice.dto.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付返回
 */
@Data
@AllArgsConstructor
public final class PayResponse {

    /**
     * 调用支付返回信息
     */
    private String body;
}
