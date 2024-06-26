package org.mtf.index12306.biz.ticketservice.service.handler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mtf.index12306.biz.ticketservice.dto.domain.PurchaseTicketPassengerDetailDTO;
import org.mtf.index12306.biz.ticketservice.dto.req.TicketPurchaseReqDTO;

import java.util.List;

/**
 * 选择座位实体
 */
@Data
@Builder
public final class SelectSeatDTO {

    /**
     * 座位类型
     */
    private Integer seatType;

    /**
     * 座位对应的乘车人集合
     */
    private List<PurchaseTicketPassengerDetailDTO> passengerSeatDetails;

    /**
     * 购票原始入参
     */
    private TicketPurchaseReqDTO requestParam;
}
