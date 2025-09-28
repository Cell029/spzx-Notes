package com.cell.spzx.product.controller;

import com.cell.model.dto.BrandDto;
import com.cell.model.dto.h5.BrandQueryDto;
import com.cell.model.entity.product.Brand;
import com.cell.model.vo.common.Result;
import com.cell.model.vo.common.ResultCodeEnum;
import com.cell.model.vo.h5.PageResult;
import com.cell.spzx.product.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/brand")
@Tag(name = "BrandController", description = "品牌管理相关接口")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @PostMapping("/listBrandPage")
    @Operation(summary = "分页查询品牌数据")
    public Result listBrandPage(@RequestBody BrandQueryDto brandQueryDto) {
        PageResult<Brand> brandPageResult = brandService.listBrandPage(brandQueryDto);
        return Result.build(brandPageResult, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/getAllBrand")
    @Operation(summary = "查询所有品牌数据")
    public Result getAllBrand() {
        List<Brand> brandList = brandService.getAllBrand();
        return Result.build(brandList, ResultCodeEnum.SUCCESS);
    }

    @PostMapping("/addBrand")
    @Operation(summary = "新增品牌数据")
    public Result addBrand(@RequestBody BrandDto brandDto) {
        brandService.addBrand(brandDto);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @PostMapping("/uploadLogo")
    @Operation(summary = "上传品牌 Logo 图片")
    public Result uploadLogo(@RequestParam("file") MultipartFile file) {
        String tempLogoUrl = brandService.uploadLogo(file);
        return Result.build(tempLogoUrl, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/getBrandById/{id}")
    @Operation(summary = "分页查询品牌数据")
    public Result getBrandById(@PathVariable Long id) {
        Brand brand = brandService.getBrandById(id);
        return Result.build(brand, ResultCodeEnum.SUCCESS);
    }

    @PutMapping("updateBrand")
    @Operation(summary = "修改品牌信息")
    public Result updateBrand(@RequestBody Brand brand) {
        brandService.updateBrand(brand);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @DeleteMapping("/deleteBrand")
    @Operation(summary = "删除品牌信息", description = "可以批量删除，也可以单个删除")
    public Result deleteBrand(@RequestBody List<Long> ids) {
        brandService.deleteBrand(ids);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

}
