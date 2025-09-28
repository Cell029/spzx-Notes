package com.cell.spzx.product.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.dto.h5.ProductQueryDto;
import com.cell.model.entity.product.Product;
import com.cell.model.vo.h5.PageResult;
import com.cell.spzx.product.mapper.ProductMapper;
import com.cell.spzx.product.service.ProductService;
import org.springframework.stereotype.Service;

@Service("productService")
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public PageResult<Product> listByPage(ProductQueryDto productQueryDto) {
        String name = productQueryDto.getName();
        Long brandId = productQueryDto.getBrandId();
        Long category1Id = productQueryDto.getCategory1Id();
        Long category2Id = productQueryDto.getCategory2Id();
        Long category3Id = productQueryDto.getCategory3Id();
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(name)) {
            wrapper.like(Product::getName, name);
        }
        if (brandId != null && brandId > 0) {
            wrapper.eq(Product::getBrandId, brandId);
        }
        if (category1Id != null && category1Id > 0) {
            wrapper.eq(Product::getCategory1Id, category1Id);
        }
        if (category2Id != null && category2Id > 0) {
            wrapper.eq(Product::getCategory2Id, category2Id);
        }
        if (category3Id != null && category3Id > 0) {
            wrapper.eq(Product::getCategory3Id, category3Id);
        }
        Page<Product> page = new Page<>(productQueryDto.getPage(), productQueryDto.getSize());
        Page<Product> pageResult = page(page, wrapper);
        return new PageResult<>(pageResult.getTotal(), pageResult.getPages(), pageResult.getRecords());
    }

}
