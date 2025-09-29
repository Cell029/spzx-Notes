package com.cell.spzx.product.service.impl;

import com.cell.model.entity.product.ProductSpec;
import com.cell.model.vo.product.ProductSpecVo;
import com.cell.model.vo.product.SpecItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.dto.h5.ProductSpecQueryDto;
import com.cell.model.vo.h5.PageResult;
import com.cell.spzx.product.mapper.ProductSpecMapper;
import com.cell.spzx.product.service.ProductSpecService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service("productSpecService")
public class ProductSpecServiceImpl extends ServiceImpl<ProductSpecMapper, ProductSpec> implements ProductSpecService {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PageResult<ProductSpecVo> listByPage(ProductSpecQueryDto productSpecQueryDto) {
        String specName = productSpecQueryDto.getSpecName();
        LambdaQueryWrapper<ProductSpec> wrapper = new LambdaQueryWrapper<>();
        if (specName != null && !specName.isEmpty()) {
            wrapper.like(ProductSpec::getSpecName, specName);
        }
        Page<ProductSpec> page = new Page<>(productSpecQueryDto.getPage(), productSpecQueryDto.getSize());
        Page<ProductSpec> pageResult = page(page, wrapper);
        List<ProductSpecVo> productSpecVoList = pageResult.getRecords().stream().map(productSpec -> {
            ProductSpecVo productSpecVo = new ProductSpecVo();
            BeanUtils.copyProperties(productSpec, productSpecVo);
            List<SpecItem> specValueList;
            try {
                specValueList = objectMapper.readValue(productSpec.getSpecValue(), new TypeReference<List<SpecItem>>() {
                });
                if (specValueList != null && !specValueList.isEmpty()) {
                    productSpecVo.setSpecValue(specValueList);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return productSpecVo;
        }).collect(Collectors.toList());
        return new PageResult<>(pageResult.getTotal(), pageResult.getPages(), productSpecVoList);
    }

    @Override
    public void add(ProductSpecVo productSpecVo) {
        ProductSpec productSpec = new ProductSpec();
        BeanUtils.copyProperties(productSpecVo, productSpec);
        List<SpecItem> specValue = productSpecVo.getSpecValue();
        if (specValue != null && !specValue.isEmpty()) {
            String json;
            try {
                json = objectMapper.writeValueAsString(specValue);
                productSpec.setSpecValue(json);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        save(productSpec);
    }

    @Override
    public List<ProductSpec> findAll() {
        return list();
    }

}
