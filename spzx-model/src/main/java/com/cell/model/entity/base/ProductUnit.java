package com.cell.model.entity.base;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("product_unit")
@Schema(description = "产品单元实体类")
public class ProductUnit extends BaseEntity {

	@Schema(description = "名称")
	private String name;

}