package com.cell.spzx.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.entity.order.OrderInfo;
import com.cell.spzx.product.mapper.OrderInfoMapper;
import com.cell.spzx.product.service.OrderInfoService;
import org.springframework.stereotype.Service;

@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {
}
