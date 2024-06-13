package org.mtf.index12306.biz.orderservice.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.StrBuilder;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.mtf.index12306.biz.orderservice.common.enums.OrderCanalErrorCodeEnum;
import org.mtf.index12306.biz.orderservice.common.enums.OrderItemStatusEnum;
import org.mtf.index12306.biz.orderservice.common.enums.OrderStatusEnum;
import org.mtf.index12306.biz.orderservice.dao.entity.OrderDO;
import org.mtf.index12306.biz.orderservice.dao.entity.OrderItemDO;
import org.mtf.index12306.biz.orderservice.dao.entity.OrderItemPassengerDO;
import org.mtf.index12306.biz.orderservice.dao.mapper.OrderItemMapper;
import org.mtf.index12306.biz.orderservice.dao.mapper.OrderMapper;
import org.mtf.index12306.biz.orderservice.dto.domain.OrderStatusReversalDTO;
import org.mtf.index12306.biz.orderservice.dto.req.*;
import org.mtf.index12306.biz.orderservice.dto.resp.OrderInfoRespDTO;
import org.mtf.index12306.biz.orderservice.dto.resp.OrderItemRespDTO;
import org.mtf.index12306.biz.orderservice.dto.resp.SelfOrderInfoRespDTO;
import org.mtf.index12306.biz.orderservice.mq.event.DelayCloseOrderEvent;
import org.mtf.index12306.biz.orderservice.mq.event.PayResultCallbackOrderEvent;
import org.mtf.index12306.biz.orderservice.mq.produce.DelayCloseOrderSendProduce;
import org.mtf.index12306.biz.orderservice.remote.UserRemoteService;
import org.mtf.index12306.biz.orderservice.remote.dto.UserQueryActualRespDTO;
import org.mtf.index12306.biz.orderservice.service.OrderItemPassengerService;
import org.mtf.index12306.biz.orderservice.service.OrderItemService;
import org.mtf.index12306.biz.orderservice.service.OrderService;
import org.mtf.index12306.biz.orderservice.service.orderid.OrderIdGeneratorManager;
import org.mtf.index12306.framework.starter.common.toolkit.BeanUtil;
import org.mtf.index12306.framework.starter.convention.exception.ClientException;
import org.mtf.index12306.framework.starter.convention.exception.ServiceException;
import org.mtf.index12306.framework.starter.convention.page.PageResponse;
import org.mtf.index12306.framework.starter.convention.result.Result;
import org.mtf.index12306.framework.starter.user.core.UserContext;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 订单接口实现层
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderDO> implements OrderService {
    private final OrderItemMapper orderItemMapper;
    private final OrderItemService orderItemService;
    private final OrderItemPassengerService orderItemPassengerService;
    private final RedissonClient redissonClient;
    private final DelayCloseOrderSendProduce delayCloseOrderSendProduce;
    private final UserRemoteService userRemoteService;

    @Override
    public OrderInfoRespDTO queryOrderByOrderSn(String orderSn) {
        LambdaQueryWrapper<OrderDO> orderQueryWrapper = Wrappers.lambdaQuery(OrderDO.class)
                .eq(OrderDO::getOrderSn, orderSn);
        OrderDO orderDO = baseMapper.selectOne(orderQueryWrapper);
        OrderInfoRespDTO orderInfoRespDTO = BeanUtil.convert(orderDO, OrderInfoRespDTO.class);
        LambdaQueryWrapper<OrderItemDO> orderItemQueryWrapper = Wrappers.lambdaQuery(OrderItemDO.class)
                .eq(OrderItemDO::getOrderSn, orderSn);
        List<OrderItemDO> orderItemDOList = orderItemMapper.selectList(orderItemQueryWrapper);
        List<OrderItemRespDTO> passengerDetails = BeanUtil.convert(orderItemDOList, OrderItemRespDTO.class);
        orderInfoRespDTO.setPassengerDetails(passengerDetails);
        return orderInfoRespDTO;
    }

    @Override
    public PageResponse<OrderInfoRespDTO> pageOrder(OrderPageQueryReqDTO requestParam) {
        List<Integer> OrderStatusTypes = switch (requestParam.getStatusType()) {
            case 0 -> ListUtil.of(OrderStatusEnum.PENDING_PAYMENT.getStatus());
            case 1 -> ListUtil.of(OrderStatusEnum.ALREADY_PAID.getStatus(),
                    OrderStatusEnum.PARTIAL_REFUND.getStatus(),
                    OrderStatusEnum.FULL_REFUND.getStatus());
            case 2 -> ListUtil.of(OrderStatusEnum.COMPLETED.getStatus());
            default -> new ArrayList<>();
        };
        LambdaQueryWrapper<OrderDO> queryWrapper = Wrappers.lambdaQuery(OrderDO.class)
                .eq(OrderDO::getUserId, requestParam.getUserId())
                .in(OrderDO::getStatus, OrderStatusTypes)
                .orderByDesc(OrderDO::getOrderTime);
        Page<OrderDO> orderPage = new Page<>(requestParam.getCurrent(), requestParam.getSize());
        baseMapper.selectPage(orderPage, queryWrapper);
        List<OrderDO> sourceRecords = orderPage.getRecords();
        List<OrderInfoRespDTO> targetRecords = sourceRecords.stream().map(orderDO -> {
            OrderInfoRespDTO orderInfoRespDTO = BeanUtil.convert(orderDO, OrderInfoRespDTO.class);
            LambdaQueryWrapper<OrderItemDO> orderItemQueryWrapper = Wrappers.lambdaQuery(OrderItemDO.class)
                    .eq(OrderItemDO::getOrderSn, orderDO.getOrderSn());
            List<OrderItemDO> orderItemDOList = orderItemMapper.selectList(orderItemQueryWrapper);
            orderInfoRespDTO.setPassengerDetails(BeanUtil.convert(orderItemDOList, OrderItemRespDTO.class));
            return orderInfoRespDTO;
        }).toList();
        PageResponse<OrderInfoRespDTO> result = new PageResponse<>();
        result.setCurrent(orderPage.getCurrent());
        result.setSize(orderPage.getSize());
        result.setRecords(targetRecords);
        result.setTotal(orderPage.getTotal());
        return result;
    }

    @Override
    public PageResponse<SelfOrderInfoRespDTO> pageSelfOrder(SelfOrderPageQueryReqDTO requestParam) {
        Result<UserQueryActualRespDTO> userActualResp = userRemoteService.queryActualUserByUsername(UserContext.getUsername());
        LambdaQueryWrapper<OrderItemPassengerDO> queryWrapper = Wrappers.lambdaQuery(OrderItemPassengerDO.class)
                .eq(OrderItemPassengerDO::getIdCard, userActualResp.getData().getIdCard())
                .orderByDesc(OrderItemPassengerDO::getCreateTime);
        Page<OrderItemPassengerDO> orderItemPassengerPage = new Page<>(requestParam.getCurrent(), requestParam.getSize());
        orderItemPassengerService.page(orderItemPassengerPage, queryWrapper);
        List<OrderItemPassengerDO> sourceRecords = orderItemPassengerPage.getRecords();
        List<SelfOrderInfoRespDTO> targetRecords = sourceRecords.stream().map(new Function<OrderItemPassengerDO, SelfOrderInfoRespDTO>() {
            @Override
            public SelfOrderInfoRespDTO apply(OrderItemPassengerDO orderItemPassengerDO) {
                LambdaQueryWrapper<OrderDO> orderQueryWrapper = Wrappers.lambdaQuery(OrderDO.class)
                        .eq(OrderDO::getOrderSn, orderItemPassengerDO.getOrderSn());
                OrderDO orderDO = baseMapper.selectOne(orderQueryWrapper);
                LambdaQueryWrapper<OrderItemDO> orderItemQueryWrapper = Wrappers.lambdaQuery(OrderItemDO.class)
                        .eq(OrderItemDO::getOrderSn, orderItemPassengerDO.getOrderSn())
                        .eq(OrderItemDO::getIdCard, orderItemPassengerDO.getIdCard());
                OrderItemDO orderItemDO = orderItemMapper.selectOne(orderItemQueryWrapper);
                SelfOrderInfoRespDTO selfOrderInfoRespDTO = BeanUtil.convert(orderDO, SelfOrderInfoRespDTO.class);
                BeanUtil.convertIgnoreNullAndBlank(orderItemDO, selfOrderInfoRespDTO);
                return selfOrderInfoRespDTO;
            }
        }).toList();
        PageResponse<SelfOrderInfoRespDTO> result = new PageResponse<>();
        result.setCurrent(orderItemPassengerPage.getCurrent());
        result.setSize(orderItemPassengerPage.getSize());
        result.setRecords(targetRecords);
        result.setTotal(orderItemPassengerPage.getTotal());
        return result;
    }

    @Override
    public String createOrder(OrderCreateReqDTO requestParam) {
        // 通过基因法将用户 ID 融入到订单号
        String orderSn = OrderIdGeneratorManager.generateId(requestParam.getUserId());
        OrderDO orderDO = OrderDO.builder().orderSn(orderSn)
                .orderTime(requestParam.getOrderTime())
                .departure(requestParam.getDeparture())
                .departureTime(requestParam.getDepartureTime())
                .ridingDate(requestParam.getRidingDate())
                .arrivalTime(requestParam.getArrivalTime())
                .trainNumber(requestParam.getTrainNumber())
                .arrival(requestParam.getArrival())
                .trainId(requestParam.getTrainId())
                .source(requestParam.getSource())
                .status(OrderStatusEnum.PENDING_PAYMENT.getStatus())
                .username(requestParam.getUsername())
                .userId(String.valueOf(requestParam.getUserId()))
                .build();
        baseMapper.insert(orderDO);
        List<OrderItemCreateReqDTO> ticketOrderItems = requestParam.getTicketOrderItems();
        List<OrderItemDO> orderItemDOList = new ArrayList<>();
        List<OrderItemPassengerDO> orderPassengerRelationDOList = new ArrayList<>();
        ticketOrderItems.forEach(each -> {
            OrderItemDO orderItemDO = OrderItemDO.builder()
                    .trainId(requestParam.getTrainId())
                    .seatNumber(each.getSeatNumber())
                    .carriageNumber(each.getCarriageNumber())
                    .realName(each.getRealName())
                    .orderSn(orderSn)
                    .phone(each.getPhone())
                    .seatType(each.getSeatType())
                    .username(requestParam.getUsername()).amount(each.getAmount()).carriageNumber(each.getCarriageNumber())
                    .idCard(each.getIdCard())
                    .ticketType(each.getTicketType())
                    .idType(each.getIdType())
                    .userId(String.valueOf(requestParam.getUserId()))
                    .status(0)
                    .build();
            orderItemDOList.add(orderItemDO);
            OrderItemPassengerDO orderPassengerRelationDO = OrderItemPassengerDO.builder()
                    .idType(each.getIdType())
                    .idCard(each.getIdCard())
                    .orderSn(orderSn)
                    .build();
            orderPassengerRelationDOList.add(orderPassengerRelationDO);
        });
        orderItemService.saveBatch(orderItemDOList);
        orderItemPassengerService.saveBatch(orderPassengerRelationDOList);
        try {
            // 发送 RocketMQ 延时消息，指定时间后取消订单
            DelayCloseOrderEvent delayCloseOrderEvent = DelayCloseOrderEvent.builder()
                    .trainId(String.valueOf(requestParam.getTrainId()))
                    .departure(requestParam.getDeparture())
                    .arrival(requestParam.getArrival())
                    .orderSn(orderSn)
                    .trainPurchaseTicketResults(requestParam.getTicketOrderItems())
                    .build();
            // 创建订单并支付后延时关闭订单消息怎么办？详情查看：https://nageoffer.com/12306/question
            SendResult sendResult = delayCloseOrderSendProduce.sendMessage(delayCloseOrderEvent);
            if (!Objects.equals(sendResult.getSendStatus(), SendStatus.SEND_OK)) {
                throw new ServiceException("投递延迟关闭订单消息队列失败");
            }
        } catch (Throwable ex) {
            log.error("延迟关闭订单消息队列发送错误，请求参数：{}", JSON.toJSONString(requestParam), ex);
            throw ex;
        }
        return orderSn;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean closeOrder(OrderCancelReqDTO requestParam) {
        String orderSn = requestParam.getOrderSn();
        LambdaQueryWrapper<OrderDO> queryWrapper = Wrappers.lambdaQuery(OrderDO.class)
                .eq(OrderDO::getOrderSn, orderSn)
                .select(OrderDO::getStatus);
        OrderDO orderDO = baseMapper.selectOne(queryWrapper);
        //订单完成
        if (Objects.isNull(orderDO) || orderDO.getStatus() != OrderStatusEnum.PENDING_PAYMENT.getStatus()) {
            return false;
        }
        // 原则上订单关闭和订单取消这两个方法可以复用，为了区分未来考虑到的场景，这里对方法进行拆分但复用逻辑
        return cancelOrder(requestParam);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean cancelOrder(OrderCancelReqDTO requestParam) {
        String orderSn = requestParam.getOrderSn();
        LambdaQueryWrapper<OrderDO> queryWrapper = Wrappers.lambdaQuery(OrderDO.class)
                .eq(OrderDO::getOrderSn, orderSn);
        OrderDO orderDO = baseMapper.selectOne(queryWrapper);
        if (orderDO == null) {
            throw new ServiceException(OrderCanalErrorCodeEnum.ORDER_CANAL_UNKNOWN_ERROR);
        } else if (orderDO.getStatus() != OrderStatusEnum.PENDING_PAYMENT.getStatus()) {
            throw new ServiceException(OrderCanalErrorCodeEnum.ORDER_CANAL_STATUS_ERROR);
        }
        RLock lock = redissonClient.getLock(StrBuilder.create("order:canal:order_sn_").append(orderSn).toString());
        if (!lock.tryLock()) {
            throw new ClientException(OrderCanalErrorCodeEnum.ORDER_CANAL_REPETITION_ERROR);
        }
        try {
            OrderDO updateOrderDO = new OrderDO();
            updateOrderDO.setStatus(OrderStatusEnum.CLOSED.getStatus());
            LambdaUpdateWrapper<OrderDO> updateWrapper = Wrappers.lambdaUpdate(OrderDO.class)
                    .eq(OrderDO::getOrderSn, orderSn);
            int updateResult = baseMapper.update(updateOrderDO, updateWrapper);
            if (updateResult <= 0) {
                throw new ServiceException(OrderCanalErrorCodeEnum.ORDER_CANAL_ERROR);
            }
            OrderItemDO updateOrderItemDO=new OrderItemDO();
            updateOrderItemDO.setStatus(OrderItemStatusEnum.CLOSED.getStatus());
            LambdaUpdateWrapper<OrderItemDO> updateItemWrapper = Wrappers.lambdaUpdate(OrderItemDO.class)
                    .eq(OrderItemDO::getOrderSn, orderSn);
            int updateItemResult = orderItemMapper.update(updateOrderItemDO, updateItemWrapper);
            if (updateItemResult <= 0) {
                throw new ServiceException(OrderCanalErrorCodeEnum.ORDER_CANAL_ERROR);
            }
        } finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public void statusReversal(OrderStatusReversalDTO requestParam) {
        LambdaQueryWrapper<OrderDO> queryWrapper = Wrappers.lambdaQuery(OrderDO.class)
                .eq(OrderDO::getOrderSn, requestParam.getOrderSn());
        OrderDO orderDO = baseMapper.selectOne(queryWrapper);
        if (orderDO == null) {
            throw new ServiceException(OrderCanalErrorCodeEnum.ORDER_CANAL_UNKNOWN_ERROR);
        } else if (orderDO.getStatus() != OrderStatusEnum.PENDING_PAYMENT.getStatus()) {
            throw new ServiceException(OrderCanalErrorCodeEnum.ORDER_CANAL_STATUS_ERROR);
        }
        RLock lock = redissonClient.getLock(StrBuilder.create("order:status-reversal:order_sn_").append(requestParam.getOrderSn()).toString());
        if (!lock.tryLock()) {
            log.warn("订单重复修改状态，状态反转请求参数：{}", JSON.toJSONString(requestParam));
        }
        try {
            OrderDO updateOrderDO = new OrderDO();
            updateOrderDO.setStatus(requestParam.getOrderStatus());
            LambdaUpdateWrapper<OrderDO> updateWrapper = Wrappers.lambdaUpdate(OrderDO.class)
                    .eq(OrderDO::getOrderSn, requestParam.getOrderSn());
            int updateResult = baseMapper.update(updateOrderDO, updateWrapper);
            if (updateResult <= 0) {
                throw new ServiceException(OrderCanalErrorCodeEnum.ORDER_STATUS_REVERSAL_ERROR);
            }
            OrderItemDO orderItemDO = new OrderItemDO();
            orderItemDO.setStatus(requestParam.getOrderItemStatus());
            LambdaUpdateWrapper<OrderItemDO> orderItemUpdateWrapper = Wrappers.lambdaUpdate(OrderItemDO.class)
                    .eq(OrderItemDO::getOrderSn, requestParam.getOrderSn());
            int orderItemUpdateResult = orderItemMapper.update(orderItemDO, orderItemUpdateWrapper);
            if (orderItemUpdateResult <= 0) {
                throw new ServiceException(OrderCanalErrorCodeEnum.ORDER_STATUS_REVERSAL_ERROR);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void payCallbackOrder(PayResultCallbackOrderEvent requestParam) {
        OrderDO updateOrderDO = new OrderDO();
        updateOrderDO.setPayTime(requestParam.getGmtPayment());
        updateOrderDO.setPayType(requestParam.getChannel());
        LambdaUpdateWrapper<OrderDO> updateWrapper = Wrappers.lambdaUpdate(OrderDO.class)
                .eq(OrderDO::getOrderSn, requestParam.getOrderSn());
        int updateResult = baseMapper.update(updateOrderDO, updateWrapper);
        if (updateResult <= 0) {
            throw new ServiceException(OrderCanalErrorCodeEnum.ORDER_STATUS_REVERSAL_ERROR);
        }
    }
}
