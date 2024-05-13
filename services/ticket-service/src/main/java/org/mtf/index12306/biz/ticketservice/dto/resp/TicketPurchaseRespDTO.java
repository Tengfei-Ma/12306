package org.mtf.index12306.biz.ticketservice.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 车票购买返回参数
 */
@Data
@AllArgsConstructor
public class TicketPurchaseRespDTO {

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 乘车人订单详情
     */
    private List<TicketDetailRespDTO> ticketOrderDetails;
}
