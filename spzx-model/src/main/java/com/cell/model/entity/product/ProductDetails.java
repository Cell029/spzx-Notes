package com.cell.model.entity.product;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cell.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("product_details")
@Schema(description = "商品详情实体类（图片）")
public class ProductDetails extends BaseEntity {

    @Schema(description = "商品 ID")
	private Long productId;

    @Schema(description = "商品详情图")
	private String imageUrls;

}