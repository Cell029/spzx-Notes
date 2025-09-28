package com.cell.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cell.model.dto.h5.ProductQueryDto;
import com.cell.model.entity.product.Product;
import com.cell.model.vo.h5.PageResult;

public interface ProductService extends IService<Product> {

    PageResult<Product> listByPage(ProductQueryDto productQueryDto);

}
