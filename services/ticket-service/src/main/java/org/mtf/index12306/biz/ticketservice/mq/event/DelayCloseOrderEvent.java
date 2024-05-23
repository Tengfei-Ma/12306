package org.mtf.index12306.biz.ticketservice.mq.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mtf.index12306.biz.ticketservice.service.handler.dto.TrainPurchaseTicketRespDTO;

import java.util.List;

/**
 * 延迟关闭订单事件
 * 公众号：马丁玩编程，回复：加群，添加马哥微信（备注：12306）获取项目资料
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DelayCloseOrderEvent {

    /**
     * 车次 ID
     */
    private String trainId;

    /**
     * 出发站点
     */
    private String departure;

    /**
     * 到达站点
     */
    private String arrival;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 乘车人购票信息
     */
    private List<TrainPurchaseTicketRespDTO> trainPurchaseTicketResults;
}
