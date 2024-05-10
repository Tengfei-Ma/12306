package org.mtf.index12306.biz.ticketservice.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.mtf.index12306.framework.starter.database.base.BaseDO;

/**
 * 座位实体
 */
@Data
@TableName("t_seat")
public class SeatDO extends BaseDO {

    /**
     * id
     */
    private Long id;

    /**
     * 列车id
     */
    private Long trainId;

    /**
     * 车厢号
     */
    private String carriageNumber;

    /**
     * 座位号
     */
    private String seatNumber;

    /**
     * 座位类型 0:商务座  1:一等座  2:二等座
     */
    private Integer seatType;

    /**
     * 起始站
     */
    private String startStation;

    /**
     * 终点站
     */
    private String endStation;

    /**
     * 座位状态
     */
    private Integer seatStatus;

    /**
     * 车票价格
     */
    private Integer price;
}
