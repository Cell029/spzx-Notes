package com.cell.spzx.product.controller;

import com.cell.model.dto.order.OrderStatisticsDto;
import com.cell.model.entity.order.OrderStatistics;
import com.cell.model.vo.common.Result;
import com.cell.model.vo.common.ResultCodeEnum;
import com.cell.model.vo.order.OrderStatisticsVo;
import com.cell.spzx.product.service.OrderStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/orderStatistics")
@Tag(name = "OrderStatisticsController", description = "订单统计管理相关接口")
public class OrderStatisticsController {

    @Autowired
    private OrderStatisticsService orderStatisticsService;

    @PostMapping("/calculateTurnover")
    @Operation(summary = "统计某天的营业数据")
    public Result calculateTurnover() {
        orderStatisticsService.calculateTurnover();
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @PostMapping("/getOrderStatistics")
    @Operation(summary = "查询营业额")
    public Result getOrderStatistics(@RequestBody OrderStatisticsDto orderStatisticsDto) {
        OrderStatisticsVo orderStatisticsVo = orderStatisticsService.getOrderStatistics(orderStatisticsDto);
        return Result.build(orderStatisticsVo, ResultCodeEnum.SUCCESS);
    }

}
