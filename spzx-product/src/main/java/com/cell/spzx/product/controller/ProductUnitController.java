package com.cell.spzx.product.controller;

import com.cell.model.dto.h5.ProductQueryDto;
import com.cell.model.entity.base.ProductUnit;
import com.cell.model.entity.product.Product;
import com.cell.model.vo.common.Result;
import com.cell.model.vo.common.ResultCodeEnum;
import com.cell.model.vo.h5.PageResult;
import com.cell.spzx.product.service.ProductService;
import com.cell.spzx.product.service.ProductUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productUnit")
@Tag(name = "ProductUnitController", description = "商品售卖单元管理相关接口")
public class ProductUnitController {

    @Autowired
    private ProductUnitService productUnitService;

    @GetMapping("getProductUnit")
    @Operation(summary = "获取所有商品售卖单元")
    public Result getProductUnit() {
        List<ProductUnit> productUnitList = productUnitService.getProductUnit();
        return Result.build(productUnitList , ResultCodeEnum.SUCCESS) ;
    }

}
