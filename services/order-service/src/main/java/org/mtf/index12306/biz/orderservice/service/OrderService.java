package org.mtf.index12306.biz.orderservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mtf.index12306.biz.orderservice.dao.entity.OrderDO;
import org.mtf.index12306.biz.orderservice.dto.req.OrderCancelReqDTO;
import org.mtf.index12306.biz.orderservice.dto.req.OrderCreateReqDTO;
import org.mtf.index12306.biz.orderservice.dto.req.OrderPageQueryReqDTO;
import org.mtf.index12306.biz.orderservice.dto.req.SelfOrderPageQueryReqDTO;
import org.mtf.index12306.biz.orderservice.dto.resp.OrderInfoRespDTO;
import org.mtf.index12306.biz.orderservice.dto.resp.SelfOrderInfoRespDTO;
import org.mtf.index12306.framework.starter.convention.page.PageResponse;

/**
 * 订单接口层
 */
public interface OrderService extends IService<OrderDO> {
    /**
     * 根据订单号查询订单
     * @param orderSn 订单号
     * @return 整体订单详情响应参数
     */
    OrderInfoRespDTO queryOrderByOrderSn(String orderSn);
    /**
     * 跟据用户名分页查询车票订单
     * @param requestParam 分页查询请求参数
     * @return 订单分页详情
     */
    PageResponse<OrderInfoRespDTO> pageOrder(OrderPageQueryReqDTO requestParam);
    /**
     * 分页查询本人订单
     * @param requestParam 分页查询请求参数
     * @return 本人订单集合
     */
    PageResponse<SelfOrderInfoRespDTO> pageSelfOrder(SelfOrderPageQueryReqDTO requestParam);
    /**
     * 创建订单
     * @param requestParam 创建订单请求参数
     * @return 订单号
     */
    String createOrder(OrderCreateReqDTO requestParam);
    /**
     * 关闭订单
     *
     * @param requestParam 关闭订单请求参数
     */
    boolean closeOrder(OrderCancelReqDTO requestParam);
    /**
     * 取消订单
     *
     * @param requestParam 取消订单请求参数
     */
    boolean cancelOrder(OrderCancelReqDTO requestParam);
}
