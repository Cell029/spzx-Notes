package com.cell.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cell.model.dto.product.CategoryBrandDto;
import com.cell.model.entity.product.CategoryBrand;
import com.cell.model.vo.h5.PageResult;

public interface CategoryBrandService extends IService<CategoryBrand> {

    PageResult<CategoryBrand> findByPage(CategoryBrandDto categoryBrandDto);

}
