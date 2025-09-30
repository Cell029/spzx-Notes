package com.cell.model.dto.h5;

import com.cell.model.dto.h5.base.QueryPageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "")
public class SearchParamDto extends QueryPageDto {

    @Schema(description = "全局查询关键字")
    private String keyword;

    @Schema(description = "品牌列表")
    private List<Long> brandIds;

    @Schema(description = "商品三级分类")
    private Long category3Id;

    @Schema(description = "价格区间")
    private String price;

    @Schema(description = "排序字段")
    private List<String> sort;

}
