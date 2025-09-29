package com.cell.spzx.product.controller;

import com.cell.model.dto.h5.ProductDto;
import com.cell.model.dto.h5.ProductQueryDto;
import com.cell.model.entity.base.ProductUnit;
import com.cell.model.entity.product.Product;
import com.cell.model.vo.common.Result;
import com.cell.model.vo.common.ResultCodeEnum;
import com.cell.model.vo.h5.PageResult;
import com.cell.spzx.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/addProduct")
    @Operation(summary = "新增商品信息", description = "添加商品 SPU、SKU、SPC、DETAILS 信息")
    public Result addProduct(@RequestBody ProductDto productDto) {
        productService.addProduct(productDto);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/getProductById/{id}")
    @Operation(summary = "根据 ID 查询商品信息")
    public Result getProductById(@PathVariable Long id) {
        ProductDto productDto = productService.getProductById(id);
        return Result.build(productDto, ResultCodeEnum.SUCCESS);
    }

    @PutMapping("/updateProductById")
    @Operation(summary = "根据 ID 修改商品信息")
    public Result updateById(@RequestBody ProductDto productDto) {
        productService.updateProductById(productDto);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @DeleteMapping("/deleteProductById")
    @Operation(summary = "根据 ID 删除商品信息")
    public Result deleteProductById(@RequestBody List<Long> ids) {
        productService.deleteProductById(ids);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @PutMapping("/updateAuditStatus/{id}/{auditStatus}")
    @Operation(summary = "审核商品")
    public Result updateAuditStatus(@PathVariable Long id, @PathVariable Integer auditStatus) {
        productService.updateAuditStatus(id, auditStatus);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @PutMapping("/updateStatus/{status}")
    @Operation(summary = "上下架商品")
    public Result updateStatus(@RequestBody List<Long> ids, @PathVariable Integer status) {
        productService.updateStatus(ids, status);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

}
