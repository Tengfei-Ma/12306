package org.mtf.index12306.biz.orderservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mtf.index12306.biz.orderservice.dao.entity.OrderItemPassengerDO;
import org.mtf.index12306.biz.orderservice.dao.mapper.OrderItemPassengerMapper;
import org.mtf.index12306.biz.orderservice.service.OrderItemPassengerService;
import org.springframework.stereotype.Service;

/**
 * 乘车人订单关系接口层实现
 */
@Service
public class OrderItemPassengerServiceImpl extends ServiceImpl<OrderItemPassengerMapper, OrderItemPassengerDO> implements OrderItemPassengerService {
}
