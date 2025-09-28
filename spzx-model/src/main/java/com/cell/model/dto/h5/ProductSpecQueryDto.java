package com.cell.model.dto.h5;

import com.cell.model.dto.h5.base.QueryPageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分页查询商品规格参数请求参数实体类")
public class ProductSpecQueryDto extends QueryPageDto {

    @Schema(description = "规格名称")
    private String specName;   // 规格名称

}
