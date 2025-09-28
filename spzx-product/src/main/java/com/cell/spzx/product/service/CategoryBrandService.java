package com.cell.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cell.model.dto.product.CategoryBrandDto;
import com.cell.model.entity.product.Brand;
import com.cell.model.entity.product.CategoryBrand;
import com.cell.model.vo.h5.PageResult;

import java.util.List;

public interface CategoryBrandService extends IService<CategoryBrand> {

    PageResult<CategoryBrand> findByPage(CategoryBrandDto categoryBrandDto);

    void add(CategoryBrand categoryBrand);

    void delete(List<Long> ids);

    List<Brand> findBrandByCategoryId(Long categoryId);

}
