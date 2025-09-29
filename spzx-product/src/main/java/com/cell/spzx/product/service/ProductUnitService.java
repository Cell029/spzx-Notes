package com.cell.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cell.model.entity.base.ProductUnit;

import java.util.List;

public interface ProductUnitService extends IService<ProductUnit> {

    List<ProductUnit> getProductUnit();

}
