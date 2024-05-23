package org.mtf.index12306.biz.orderservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mtf.index12306.biz.orderservice.dao.entity.OrderItemDO;
import org.mtf.index12306.biz.orderservice.dto.domain.OrderItemStatusReversalDTO;
import org.mtf.index12306.biz.orderservice.dto.req.OrderItemQueryReqDTO;
import org.mtf.index12306.biz.orderservice.dto.resp.OrderItemRespDTO;

import java.util.List;

/**
 * 订单明细接口层
 */
public interface OrderItemService extends IService<OrderItemDO> {
    /**
     * 根据订单号和订单明细id集合查询订单明细
     * @param requestParam 订单明细查询请求参数
     * @return 订单明细响应参数
     */
    List<OrderItemRespDTO> queryOrderItemByIds(OrderItemQueryReqDTO requestParam);
    /**
     * 子订单状态反转
     *
     * @param requestParam 请求参数
     */
    void orderItemStatusReversal(OrderItemStatusReversalDTO requestParam);
}
