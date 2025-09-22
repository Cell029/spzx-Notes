package com.cell.model.dto.h5.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class QueryPageDto {

    @Schema(description = "查询页码")
    private Integer page;

    @Schema(description = "每页数据条数")
    private Integer size;

    public Integer getPage() {
        return (page == null || page <= 0) ? 1 : page;
    }

    public Integer getSize() {
        return (size == null || size <= 0) ? 10 : size;
    }

}
