package com.cell.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.dto.product.CategoryBrandDto;
import com.cell.model.entity.product.Brand;
import com.cell.model.entity.product.Category;
import com.cell.model.entity.product.CategoryBrand;
import com.cell.model.vo.h5.PageResult;
import com.cell.spzx.product.mapper.CategoryBrandMapper;
import com.cell.spzx.product.service.BrandService;
import com.cell.spzx.product.service.CategoryBrandService;
import com.cell.spzx.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("categoryBrandService")
public class CategoryBrandServiceImpl extends ServiceImpl<CategoryBrandMapper, CategoryBrand> implements CategoryBrandService {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;

    // TODO 优化
    @Override
    public PageResult<CategoryBrand> findByPage(CategoryBrandDto categoryBrandDto) {
        Long brandId = categoryBrandDto.getBrandId();
        Long categoryId = categoryBrandDto.getCategoryId();
        if (categoryBrandDto.getBrandId() != null && categoryBrandDto.getCategoryId() != null) {
            LambdaQueryWrapper<CategoryBrand> cbWrapper = new LambdaQueryWrapper<>();
            cbWrapper.eq(CategoryBrand::getBrandId, categoryBrandDto.getBrandId());
            cbWrapper.eq(CategoryBrand::getCategoryId, categoryBrandDto.getCategoryId());
            List<CategoryBrand> categoryBrandList = new ArrayList<>();
            CategoryBrand categoryBrand = new CategoryBrand();
            if (count(cbWrapper) > 0) {
                String categoryName = categoryService.getById(categoryBrandDto.getCategoryId()).getName();
                Brand brand = brandService.getById(categoryBrandDto.getBrandId());
                String brandName = brand.getName();
                String brandLogo = brand.getLogo();
                categoryBrand.setCategoryId(categoryBrandDto.getCategoryId());
                categoryBrand.setBrandId(categoryBrandDto.getBrandId());
                categoryBrand.setCategoryName(categoryName);
                categoryBrand.setBrandName(brandName);
                categoryBrand.setLogo(brandLogo);
                categoryBrandList.add(categoryBrand);
                return new PageResult<CategoryBrand>(1, 1, categoryBrandList);
            }
        }

        if (categoryBrandDto.getBrandId() == null && categoryBrandDto.getCategoryId() == null) {
            LambdaQueryWrapper<CategoryBrand> cbWrapper = new LambdaQueryWrapper<>();
            Page<CategoryBrand> page = new Page<>(categoryBrandDto.getPage(), categoryBrandDto.getSize());
            Page<CategoryBrand> pageResult = page(page, cbWrapper);
            List<CategoryBrand> list = pageResult.getRecords();
            return new PageResult<>(pageResult.getTotal(), pageResult.getPages(), pageResult.getRecords());
        }

        if (categoryBrandDto.getBrandId() != null && categoryBrandDto.getCategoryId() == null) {
            LambdaQueryWrapper<CategoryBrand> cbWrapper = new LambdaQueryWrapper<CategoryBrand>().eq(CategoryBrand::getBrandId, categoryBrandDto.getBrandId());
            Page<CategoryBrand> page = new Page<>(categoryBrandDto.getPage(), categoryBrandDto.getSize());
            Page<CategoryBrand> pageResult = page(page, cbWrapper);
            List<CategoryBrand> list = pageResult.getRecords();
            Brand brand = brandService.getById(categoryBrandDto.getBrandId());
            if (!list.isEmpty()) {
                List<Long> categoryIds = list.stream().map(CategoryBrand::getCategoryId).collect(Collectors.toList());
                List<CategoryBrand> categoryBrandList = categoryService.list(new LambdaQueryWrapper<Category>().in(Category::getId, categoryIds))
                        .stream()
                        .map(category -> {
                            CategoryBrand categoryBrand = new CategoryBrand();
                            categoryBrand.setBrandId(categoryBrandDto.getBrandId());
                            categoryBrand.setBrandName(brand.getName());
                            categoryBrand.setLogo(brand.getLogo());
                            categoryBrand.setCategoryId(category.getId());
                            categoryBrand.setCategoryName(category.getName());
                            return categoryBrand;
                        }).collect(Collectors.toList());
                return new PageResult<>(pageResult.getTotal(), pageResult.getPages(), categoryBrandList);
            }
        }
        if (categoryBrandDto.getCategoryId() != null && categoryBrandDto.getBrandId() == null) {
            LambdaQueryWrapper<CategoryBrand> cbWrapper = new LambdaQueryWrapper<CategoryBrand>().eq(CategoryBrand::getCategoryId, categoryBrandDto.getCategoryId());
            Page<CategoryBrand> page = new Page<>(categoryBrandDto.getPage(), categoryBrandDto.getSize());
            Page<CategoryBrand> pageResult = page(page, cbWrapper);
            List<CategoryBrand> list = pageResult.getRecords();
            Category category = categoryService.getById(categoryBrandDto.getCategoryId());
            if (!list.isEmpty()) {
                List<Long> brandIds = list.stream().map(CategoryBrand::getBrandId).collect(Collectors.toList());
                List<CategoryBrand> categoryBrandList = brandService.list(new LambdaQueryWrapper<Brand>().in(Brand::getId, brandIds))
                        .stream()
                        .map(brand -> {
                            CategoryBrand categoryBrand = new CategoryBrand();
                            categoryBrand.setBrandId(categoryBrandDto.getBrandId());
                            categoryBrand.setBrandName(brand.getName());
                            categoryBrand.setLogo(brand.getLogo());
                            categoryBrand.setCategoryId(categoryBrandDto.getCategoryId());
                            categoryBrand.setCategoryName(category.getName());
                            return categoryBrand;
                        }).collect(Collectors.toList());
                return new PageResult<>(pageResult.getTotal(), pageResult.getPages(), categoryBrandList);
            }
        }
        return new PageResult<>();
    }



}
