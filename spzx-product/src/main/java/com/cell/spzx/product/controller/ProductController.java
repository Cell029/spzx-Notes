package com.cell.spzx.product.controller;

import com.cell.model.dto.h5.ProductQueryDto;
import com.cell.model.entity.product.Product;
import com.cell.model.vo.common.Result;
import com.cell.model.vo.common.ResultCodeEnum;
import com.cell.model.vo.h5.PageResult;
import com.cell.spzx.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@Tag(name = "ProductController", description = "SPU　商品信息管理相关接口")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/listByPage")
    @Operation(summary = "分页查询 SPU 商品信息")
    public Result listByPage(@RequestBody ProductQueryDto productQueryDto) {
        PageResult<Product> productPageResult = productService.listByPage(productQueryDto);
        return Result.build(productPageResult, ResultCodeEnum.SUCCESS);
    }

}
