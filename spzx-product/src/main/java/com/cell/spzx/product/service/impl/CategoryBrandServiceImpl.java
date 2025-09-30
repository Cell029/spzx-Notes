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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryBrandServiceImpl extends ServiceImpl<CategoryBrandMapper, CategoryBrand> implements CategoryBrandService {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;

    // 优化
    /*@Override
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
    }*/

    // 优化
    @Override
    public PageResult<CategoryBrand> findByPage(CategoryBrandDto categoryBrandDto) {
        Long brandId = categoryBrandDto.getBrandId();
        Long categoryId = categoryBrandDto.getCategoryId();
        Page<CategoryBrand> page = new Page<>(categoryBrandDto.getPage(), categoryBrandDto.getSize());
        LambdaQueryWrapper<CategoryBrand> cbWrapper = new LambdaQueryWrapper<>();
        // 前端传递了品牌和商品分类两个条件，那么就可以确定唯一的数据
        // 当前端只选择了品牌，那么就可能查询出多个相同品牌的不同商品分类
        if (brandId != null && brandId != 0L) {
            cbWrapper.eq(CategoryBrand::getBrandId, brandId);
        }
        // 当前端只选择了商品分类，那么就可能查询出多个不同品牌下的同一个商品分类
        if (categoryId != null && categoryId != 0L) {
            cbWrapper.eq(CategoryBrand::getCategoryId, categoryId);
        }
        // 获取分页查询结果
        Page<CategoryBrand> pageResult = page(page, cbWrapper);
        List<CategoryBrand> records = pageResult.getRecords();
        // 通过结果集获取所有 CategoryBrand 中拥有的 brandId 和 categoryId 集合，因为可能存在重复，所以把它们封装成 Set 集合
        Set<Long> brandIdSet = records.stream().map(CategoryBrand::getBrandId).collect(Collectors.toSet());
        Set<Long> categoryIdSet = records.stream().map(CategoryBrand::getCategoryId).collect(Collectors.toSet());
        // 获取到所有相关的 Brand 和 Category
        List<Brand> brandList = brandService.listByIds(brandIdSet);
        List<Category> categoryList = categoryService.listByIds(categoryIdSet);
        // 把 Brand 和 Category 封装成 Map 集合，以各自的 Id 为 key，实体类为 value
        // 这样后面给 CategoryBrand 对象赋值时就可以直接通过它们的 id 获取到对应的实体类
        Map<Long, Brand> brandMap = brandList.stream().collect(Collectors.toMap(Brand::getId, brand -> brand));
        Map<Long, Category> categoryMap = categoryList.stream().collect(Collectors.toMap(Category::getId, category -> category));
        // 处理分页查询的结果集，给每个 CategoryBrand 对象的新增字段赋值
        List<CategoryBrand> categoryBrandList = records.stream().map(categoryBrand -> {
            // 根据 brandId 从 Map 集合中获取到对应的 Brand 实体类，避免多次循环查找数据库
            Brand brand = brandMap.get(categoryBrand.getBrandId());
            Category category = categoryMap.get(categoryBrand.getCategoryId());
            if (brand != null) {
                categoryBrand.setBrandName(brand.getName());
                categoryBrand.setLogo(brand.getLogo());
            }
            if (category != null) {
                categoryBrand.setCategoryName(category.getName());
            }
            return categoryBrand;
        }).collect(Collectors.toList());
        return new PageResult<>(pageResult.getTotal(), pageResult.getSize(), categoryBrandList);
    }

    @Override
    public void add(CategoryBrand categoryBrand) {
        save(categoryBrand);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        removeBatchByIds(ids);
    }

    @Override
    public List<Brand> findBrandByCategoryId(Long categoryId) {
        LambdaQueryWrapper<CategoryBrand> wrapper = new LambdaQueryWrapper<CategoryBrand>().eq(CategoryBrand::getCategoryId, categoryId);
        Set<Long> brandIdSet = list(wrapper).stream().map(CategoryBrand::getBrandId).collect(Collectors.toSet());
        return brandService.listByIds(brandIdSet);
    }

}
