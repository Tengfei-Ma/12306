package org.mtf.index12306.biz.orderservice.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import org.mtf.index12306.framework.starter.database.base.BaseDO;

/**
 * 乘车人订单关系实体
 */
@Data
@Builder
@TableName("t_order_item_passenger")
public class OrderItemPassengerDO extends BaseDO {
    /**
     * id
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 证件类型
     */
    private Integer idType;

    /**
     * 证件号
     */
    private String idCard;
}
