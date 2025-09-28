package com.cell.model.vo.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "商品规格视图实体类")
public class ProductSpecVo {

    @Schema(description = "规格名称")
    private String specName;   // 规格名称

    @Schema(description = "规格值")
    private List<SpecItem> specValue;  // 规格值

}
