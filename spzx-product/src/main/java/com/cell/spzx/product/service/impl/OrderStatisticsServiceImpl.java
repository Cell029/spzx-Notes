package com.cell.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.dto.order.OrderStatisticsDto;
import com.cell.model.entity.order.OrderInfo;
import com.cell.model.entity.order.OrderStatistics;
import com.cell.model.vo.order.OrderStatisticsVo;
import com.cell.spzx.product.mapper.OrderStatisticsMapper;
import com.cell.spzx.product.service.OrderInfoService;
import com.cell.spzx.product.service.OrderStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderStatisticsServiceImpl extends ServiceImpl<OrderStatisticsMapper, OrderStatistics> implements OrderStatisticsService {

    @Autowired
    private OrderInfoService orderInfoService;

    /**
     * 每天 2:00 会统计前一天的营业数据，定时任务会调用该方法
     */
    @Override
    public void calculateTurnover() {
        // 获取前一天的日期
        LocalDateTime startTime = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime endTime = LocalDate.now().minusDays(1).atTime(23, 59, 59);
        LambdaQueryWrapper<OrderInfo> orderInfoWrapper = new LambdaQueryWrapper<>();
        // 查询订单创建日期为昨日的
        orderInfoWrapper.between(OrderInfo::getCreateTime, startTime, endTime);
        List<OrderInfo> orderInfoList = orderInfoService.list(orderInfoWrapper);
        // 计算昨日的所有订单的总金额
        BigDecimal totalAmount = new BigDecimal("0");
        for (OrderInfo orderInfo : orderInfoList) {
            totalAmount = totalAmount.add(orderInfo.getTotalAmount());
        }
        OrderStatistics orderStatistics = new OrderStatistics();
        // 将 LocalDateTime 转换为 Date
        Date orderDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
        orderStatistics.setOrderDate(orderDate);
        orderStatistics.setTotalAmount(totalAmount);
        // 计算昨日订单的总数，即查询出的 orderInfoList 的长度
        orderStatistics.setTotalNum(orderInfoList.size());
        // 存入数据到 order_statistics 表
        save(orderStatistics);
    }

    @Override
    public OrderStatisticsVo getOrderStatistics(OrderStatisticsDto orderStatisticsDto) {
        String createTimeBegin = orderStatisticsDto.getCreateTimeBegin();
        String createTimeEnd = orderStatisticsDto.getCreateTimeEnd();
        // 将字符串转换为 LocalDate 类型
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startTime = null;
        LocalDate endTime = null;
        LambdaQueryWrapper<OrderStatistics> wrapper = new LambdaQueryWrapper<>();
        if (createTimeBegin != null && !createTimeBegin.isEmpty()) {
            startTime = LocalDate.parse(createTimeBegin, formatter);
            // 设置大于等于该时间的条件
            wrapper.ge(OrderStatistics::getOrderDate, startTime);
        }
        if (createTimeEnd != null && !createTimeEnd.isEmpty()) {
            endTime = LocalDate.parse(createTimeEnd, formatter);
            // 设置小于该时间的条件
            wrapper.lt(OrderStatistics::getOrderDate, endTime);
        }
        List<OrderStatistics> orderStatisticsList = list(wrapper);
        // 获取订单日期
        List<String> dateList = orderStatisticsList.stream().map(orderStatistics -> orderStatistics.getOrderDate().toString()).collect(Collectors.toList());
        // 获取订单营业额
        List<BigDecimal> totalAmountList = orderStatisticsList.stream().map(OrderStatistics::getTotalAmount).collect(Collectors.toList());
        OrderStatisticsVo orderStatisticsVo = new OrderStatisticsVo();
        orderStatisticsVo.setDateList(dateList);
        orderStatisticsVo.setAmountList(totalAmountList);
        return orderStatisticsVo;
    }
}
