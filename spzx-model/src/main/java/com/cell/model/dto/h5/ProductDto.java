package com.cell.model.dto.h5;

import com.baomidou.mybatisplus.annotation.TableField;
import com.cell.model.entity.base.BaseEntity;
import com.cell.model.entity.product.Product;
import com.cell.model.entity.product.ProductSku;
import com.cell.model.vo.product.SpecItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "商品实体类")
public class ProductDto extends Product {

    // 扩展的属性，用来封装响应的数据
    @TableField(exist = false)
    @Schema(description = "品牌名称")
    private String brandName;				// 品牌

    @TableField(exist = false)
    @Schema(description = "一级分类名称")
    private String category1Name;			// 一级分类

    @TableField(exist = false)
    @Schema(description = "二级分类名称")
    private String category2Name;			// 二级分类

    @TableField(exist = false)
    @Schema(description = "三级分类名称")
    private String category3Name;			// 三级分类

    @TableField(exist = false)
	@Schema(description = "sku列表集合")
	private List<ProductSku> productSkuList; // sku列表集合

    @TableField(exist = false)
	@Schema(description = "图片详情列表")
	private String detailsImageUrls;		 // 图片详情列表

}