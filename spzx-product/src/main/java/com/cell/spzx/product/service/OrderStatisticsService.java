package com.cell.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cell.model.entity.order.OrderStatistics;

public interface OrderStatisticsService extends IService<OrderStatistics> {

    void calculateTurnover();

}
