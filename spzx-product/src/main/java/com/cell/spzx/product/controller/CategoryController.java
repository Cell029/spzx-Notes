package com.cell.spzx.product.controller;

import com.cell.model.entity.product.Category;
import com.cell.model.vo.common.Result;
import com.cell.model.vo.common.ResultCodeEnum;
import com.cell.spzx.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/category")
@Tag(name = "CategoryController", description = "商品分类管理相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/getCategory")
    @Operation(summary = "获取商品三级分类")
    public Result getCategory() {
        List<Category> categoryList = categoryService.getCategory();
        return Result.build(categoryList, ResultCodeEnum.SUCCESS);
    }

    @GetMapping(value = "/exportData")
    @Operation(summary = "导出商品三级分类 Excel")
    public void exportData(HttpServletResponse response) {
        // 1. 设置响应头信息和其它信息
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里 URLEncoder.encode 可以防止中文乱码 当然和 EasyExcel 没有关系
        String fileName = URLEncoder.encode("分类数据", StandardCharsets.UTF_8);
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        categoryService.exportData(response);
    }

    @PostMapping("importData")
    @Operation(summary = "导入商品三级分类 Excel")
    public Result importData(MultipartFile file) {
        categoryService.importData(file);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

}
