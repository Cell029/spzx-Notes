package com.cell.spzx.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.entity.product.ProductDetails;
import com.cell.spzx.product.mapper.ProductDetailsMapper;
import com.cell.spzx.product.service.ProductDetailsService;
import org.springframework.stereotype.Service;

@Service
public class ProductDetailsServiceImpl extends ServiceImpl<ProductDetailsMapper, ProductDetails> implements ProductDetailsService {
}
