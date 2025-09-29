package com.cell.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cell.model.dto.h5.ProductSpecQueryDto;
import com.cell.model.entity.product.ProductSpec;
import com.cell.model.vo.h5.PageResult;
import com.cell.model.vo.product.ProductSpecVo;

import java.util.List;

public interface ProductSpecService extends IService<ProductSpec> {

    PageResult<ProductSpecVo> listByPage(ProductSpecQueryDto productSpecQueryDto);

    void add(ProductSpecVo productSpecvo);

    List<ProductSpec> findAll();

}
