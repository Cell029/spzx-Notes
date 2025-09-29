package com.cell.spzx.common.feign;

import com.cell.model.vo.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@Service
@FeignClient(name = "spzx-product", path = "/orderStatistics")
public interface OrderStatisticsFeignClient {
    @PostMapping("/calculateTurnover")
    @Operation(summary = "统计某天的营业数据")
    Result calculateTurnover();
}
