package com.cell.spzx.product.controller;

import com.cell.model.dto.product.CategoryBrandDto;
import com.cell.model.entity.product.Brand;
import com.cell.model.entity.product.CategoryBrand;
import com.cell.model.vo.common.Result;
import com.cell.model.vo.common.ResultCodeEnum;
import com.cell.model.vo.h5.PageResult;
import com.cell.spzx.product.service.CategoryBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categoryBrand")
@Tag(name = "CategoryBrandController", description = "商品分类和品牌关联相关接口")
public class CategoryBrandController {

    @Autowired
    private CategoryBrandService categoryBrandService;

    @PostMapping("/listPage")
    @Operation(summary = "分页查询商品分类和品牌关联信息", description = "分为四种情况：(1)同时指定商品分类和品牌；(2)什么都不指定；(3)只指定商品分类；(4)只指定品牌")
    public Result findByPage(@RequestBody CategoryBrandDto CategoryBrandDto) {
        PageResult<CategoryBrand> categoryBrandPageResult = categoryBrandService.findByPage(CategoryBrandDto);
        return Result.build(categoryBrandPageResult, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/findBrandByCategoryId/{categoryId}")
    @Operation(summary = "根据商品分类 id 加载关联的品牌数据")
    public Result findBrandByCategoryId(@PathVariable Long categoryId) {
        List<Brand> brandList = categoryBrandService.findBrandByCategoryId(categoryId);
        return Result.build(brandList, ResultCodeEnum.SUCCESS);
    }

    @PostMapping("/add")
    @Operation(summary = "新增商品分类和品牌关联信息")
    public Result add(@RequestBody CategoryBrand categoryBrand) {
        categoryBrandService.add(categoryBrand);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @PutMapping("/updateById")
    @Operation(summary = "修改商品分类和品牌关联信息")
    public Result updateById(@RequestBody CategoryBrand categoryBrand) {
        categoryBrandService.updateById(categoryBrand);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除商品分类和品牌关联信息")
    public Result delete(@RequestBody List<Long> ids) {
        categoryBrandService.delete(ids);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

}
