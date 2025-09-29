package com.cell.model.entity.order;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cell.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("order_statistics")
@Schema(description = "统计数据结果实体类")
public class OrderStatistics extends BaseEntity {

    @Schema(description = "订单统计日期")
    private Date orderDate;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "总订单")
    private Integer totalNum;
    
}