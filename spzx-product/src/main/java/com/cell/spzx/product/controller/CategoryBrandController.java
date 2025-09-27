package com.cell.spzx.product.controller;

import com.cell.model.dto.product.CategoryBrandDto;
import com.cell.model.entity.product.CategoryBrand;
import com.cell.model.vo.common.Result;
import com.cell.model.vo.common.ResultCodeEnum;
import com.cell.model.vo.h5.PageResult;
import com.cell.spzx.product.service.CategoryBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categoryBrand")
@Tag(name = "CategoryBrandController", description = "商品分类和品牌关联相关接口")
public class CategoryBrandController {

    @Autowired
    private CategoryBrandService categoryBrandService;

    @PostMapping("/listPage")
    @Operation(summary = "分页查询商品分类和品牌关联信息", description = "分为三种情况：(1)同时指定商品分类和品牌；(2)只指定商品分类；(3)只指定品牌")
    public Result findByPage(@RequestBody CategoryBrandDto CategoryBrandDto) {
        PageResult<CategoryBrand> categoryBrandPageResult = categoryBrandService.findByPage(CategoryBrandDto);
        return Result.build(categoryBrandPageResult, ResultCodeEnum.SUCCESS);
    }

}
