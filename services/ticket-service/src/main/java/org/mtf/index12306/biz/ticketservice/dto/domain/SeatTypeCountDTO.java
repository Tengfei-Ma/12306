
package org.mtf.index12306.biz.ticketservice.dto.domain;

import lombok.Data;

/**
 * 座位类型和座位数量实体
 */
@Data
public class SeatTypeCountDTO {

    /**
     * 座位类型
     */
    private Integer seatType;

    /**
     * 座位类型 - 对应数量
     */
    private Integer seatCount;
}
