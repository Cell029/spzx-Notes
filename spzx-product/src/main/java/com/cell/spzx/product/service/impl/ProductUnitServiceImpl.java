package com.cell.spzx.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.entity.base.ProductUnit;
import com.cell.spzx.product.mapper.ProductUnitMapper;
import com.cell.spzx.product.service.ProductUnitService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductUnitServiceImpl extends ServiceImpl<ProductUnitMapper, ProductUnit> implements ProductUnitService {
    @Override
    public List<ProductUnit> getProductUnit() {
        return list();
    }
}
