package com.cell.model.dto.h5;

import com.cell.model.dto.h5.base.QueryPageDto;
import com.cell.model.dto.product.ProductDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分页请求 spu 商品信息请求参数实体类")
public class ProductQueryDto extends QueryPageDto {

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "品牌id")
    private Long brandId;

    @Schema(description = "一级分类id")
    private Long category1Id;

    @Schema(description = "二级分类id")
    private Long category2Id;

    @Schema(description = "三级分类id")
    private Long category3Id;

}
