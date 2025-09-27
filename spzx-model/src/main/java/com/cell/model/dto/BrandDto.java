package com.cell.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "新增品牌请求参数实体类")
public class BrandDto {

    @Schema(description = "品牌名")
    private String name;

    @Schema(description = "品牌 logo")
    private String logo;

}
