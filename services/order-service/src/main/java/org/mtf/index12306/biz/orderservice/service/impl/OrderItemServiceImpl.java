package org.mtf.index12306.biz.orderservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.mtf.index12306.biz.orderservice.dao.entity.OrderItemDO;
import org.mtf.index12306.biz.orderservice.dao.mapper.OrderItemMapper;
import org.mtf.index12306.biz.orderservice.dto.req.OrderItemQueryReqDTO;
import org.mtf.index12306.biz.orderservice.dto.resp.OrderItemRespDTO;
import org.mtf.index12306.biz.orderservice.service.OrderItemService;
import org.mtf.index12306.framework.starter.common.toolkit.BeanUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单明细接口实现层
 */
@Service
@Slf4j
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItemDO> implements OrderItemService {
    @Override
    public List<OrderItemRespDTO> queryOrderItemByIds(OrderItemQueryReqDTO requestParam) {
        LambdaQueryWrapper<OrderItemDO> queryWrapper = Wrappers.lambdaQuery(OrderItemDO.class)
                .eq(OrderItemDO::getOrderSn, requestParam.getOrderSn())
                .in(OrderItemDO::getId, requestParam.getOrderItemIds());
        List<OrderItemDO> orderItemDOList = baseMapper.selectList(queryWrapper);
        return BeanUtil.convert(orderItemDOList, OrderItemRespDTO.class);
    }
}
