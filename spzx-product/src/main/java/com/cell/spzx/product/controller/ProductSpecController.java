package com.cell.spzx.product.controller;

import com.cell.model.dto.h5.ProductSpecQueryDto;
import com.cell.model.vo.common.Result;
import com.cell.model.vo.common.ResultCodeEnum;
import com.cell.model.vo.h5.PageResult;
import com.cell.model.vo.product.ProductSpecVo;
import com.cell.spzx.product.service.ProductSpecService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/productSpec")
@Tag(name = "ProductSpecController", description = "商品规格参数管理相关接口")
public class ProductSpecController {

    @Autowired
    private ProductSpecService productSpecService;

    @PostMapping("/list")
    @Operation(summary = "分页查询商品规格参数", description = "可以模糊查询规格名称")
    public Result listByPage(@RequestBody ProductSpecQueryDto productSpecQueryDto) {
        PageResult<ProductSpecVo> productSpecPageResult = productSpecService.listByPage(productSpecQueryDto);
        return Result.build(productSpecPageResult, ResultCodeEnum.SUCCESS);
    }

    @PostMapping("/add")
    @Operation(summary = "新增商品规格参数")
    public Result add(@RequestBody ProductSpecVo productSpecvo) {
        productSpecService.add(productSpecvo);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

}
