package com.cell.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cell.model.dto.order.OrderStatisticsDto;
import com.cell.model.entity.order.OrderStatistics;
import com.cell.model.vo.order.OrderStatisticsVo;

public interface OrderStatisticsService extends IService<OrderStatistics> {

    void calculateTurnover();

    OrderStatisticsVo getOrderStatistics(OrderStatisticsDto orderStatisticsDto);

}
