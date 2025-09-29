package com.cell.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cell.model.dto.h5.ProductDto;
import com.cell.model.dto.h5.ProductQueryDto;
import com.cell.model.entity.base.ProductUnit;
import com.cell.model.entity.product.Product;
import com.cell.model.vo.h5.PageResult;

import java.util.List;

public interface ProductService extends IService<Product> {

    PageResult<Product> listByPage(ProductQueryDto productQueryDto);

    void addProduct(ProductDto productDto);

    ProductDto getProductById(Long id);

    void updateProductById(ProductDto productDto);

    void deleteProductById(List<Long> ids);

    void updateAuditStatus(Long id, Integer auditStatus);

    void updateStatus(List<Long> ids, Integer status);

}
