package org.mtf.index12306.biz.orderservice.dto.resp;

import cn.crane4j.annotation.AssembleEnum;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.mtf.index12306.biz.orderservice.common.enums.OrderItemStatusEnum;
import org.mtf.index12306.biz.orderservice.serialize.IdCardDesensitizationSerializer;

/**
 * 乘车人订单详情返回参数
 */
@Data
public class OrderItemRespDTO {
    /**
     * ID
     */
    private String id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 车厢号
     */
    private String carriageNumber;

    /**
     * 席别类型
     */
    private Integer seatType;

    /**
     * 座位号
     */
    private String seatNumber;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 证件类型
     */
    private Integer idType;

    /**
     * 证件号
     */
    @JsonSerialize(using = IdCardDesensitizationSerializer.class)
    private String idCard;

    /**
     * 车票类型 0：成人 1：儿童 2：学生 3：残疾军人
     */
    private Integer ticketType;

    /**
     * 车票状态
     */
    @AssembleEnum(type = OrderItemStatusEnum.class, ref = "statusName")
    private Integer status;

    /**
     * 车票状态名称
     */
    private String statusName;

    /**
     * 订单金额
     */
    private Integer amount;
}
