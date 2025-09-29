package com.cell.spzx.product.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.dto.h5.ProductDto;
import com.cell.model.dto.h5.ProductQueryDto;
import com.cell.model.entity.product.*;
import com.cell.model.vo.h5.PageResult;
import com.cell.model.vo.product.SpecItem;
import com.cell.spzx.product.mapper.ProductDetailsMapper;
import com.cell.spzx.product.mapper.ProductMapper;
import com.cell.spzx.product.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service("productService")
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProductSkuService productSkuService;
    @Autowired
    private ProductDetailsService productDetailsService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProduct(ProductDto productDto) {
        // 1. 将 SPU 信息保存进 product 表
        Product product = new Product();
        BeanUtils.copyProperties(productDto, product);
        product.setStatus(0); // 设置上架状态为 0
        product.setAuditStatus(0); // 设置审核状态为 0
        // 保存商品规格参数
        String specValue = productDto.getSpecValue();
        if (specValue != null) {
            product.setSpecValue(specValue);
        }
        save(product);
        // 获取插入数据时的回显 productId
        Long productId = product.getId();
        // 2. 将 SKU 信息保存进 product_sku 表
        List<ProductSku> productSkuList = productDto.getProductSkuList();
        if (specValue != null) {
            AtomicInteger count = new AtomicInteger();
            List<ProductSku> collect = productSkuList.stream().map(productSku -> {
                count.getAndIncrement();
                productSku.setProductId(productId);
                productSku.setSkuCode(productId + "_" + count.get());
                productSku.setSaleNum(0); // 设置销量
                productSku.setStatus(0);
                String skuSpec = productSku.getSkuSpec();
                try {
                    Map<String, String> specMap = objectMapper.readValue(skuSpec, Map.class);
                    StringBuilder stringBuilder = new StringBuilder(product.getName());
                    for (String value : specMap.values()) {
                        stringBuilder.append(" ").append(value); // 设置 SKU 名称
                    }
                } catch (Exception e) {
                    // 解析异常就退回原逻辑
                    productSku.setSkuName(product.getName() + skuSpec);
                }
                return productSku;
            }).collect(Collectors.toList());
            productSkuService.saveBatch(collect);
        }
        // 3. 将 DETAILS 信息保存进 product_details 表
        ProductDetails productDetails = new ProductDetails();
        productDetails.setProductId(productId);
        String detailsImageUrls = productDto.getDetailsImageUrls();
        if (StringUtils.isNotEmpty(detailsImageUrls)) {
            productDetails.setImageUrls(detailsImageUrls);
        }
        productDetailsService.save(productDetails);
    }

    @Override
    public ProductDto getProductById(Long id) {
        ProductDto productDto = new ProductDto();
        // 1. 封装 SPU 信息
        Product product = getById(id);
        BeanUtils.copyProperties(product, productDto);
        // 2. 封装 SKU 信息
        List<ProductSku> productSkuList = productSkuService.list(new LambdaQueryWrapper<ProductSku>().eq(ProductSku::getProductId, id));
        productDto.setProductSkuList(productSkuList);
        // 3. 封装 DETAILS 信息
        ProductDetails productDetails = productDetailsService.getOne(new LambdaQueryWrapper<ProductDetails>().eq(ProductDetails::getProductId, id));
        productDto.setDetailsImageUrls(productDetails.getImageUrls());
        Long brandId = product.getBrandId();
        Brand brand = brandService.getById(brandId);
        if (brand != null) {
            productDto.setBrandName(brand.getName());
        }
        Long category1Id = product.getCategory1Id();
        Long category2Id = product.getCategory2Id();
        Long category3Id = product.getCategory3Id();
        List<Long> categoryIdList = new ArrayList<>();
        categoryIdList.add(category1Id);
        categoryIdList.add(category2Id);
        categoryIdList.add(category3Id);
        List<Category> categoryList = categoryService.listByIds(categoryIdList);
        if (CollectionUtils.isNotEmpty(categoryList)) {
            Map<Long, Category> categoryMap = categoryList.stream().collect(Collectors.toMap(Category::getId, category -> category));
            productDto.setCategory1Name(categoryMap.get(category1Id).getName());
            productDto.setCategory2Name(categoryMap.get(category2Id).getName());
            productDto.setCategory3Name(categoryMap.get(category3Id).getName());
        }
        return productDto;
    }

    @Override
    @Transactional
    public void updateProductById(ProductDto productDto) {
        // 1. 修改 SPU 信息
        updateById(productDto);
        // 2. 修改 SKU 信息
        List<ProductSku> productSkuList = productDto.getProductSkuList();
        if (CollectionUtils.isNotEmpty(productSkuList)) {
            List<ProductSku> collect = productSkuList.stream().map(productSku -> {
                StringBuilder stringBuilder = new StringBuilder(productDto.getName());
                String skuSpec = productSku.getSkuSpec();
                Map<String, String> specMap = null;
                try {
                    specMap = objectMapper.readValue(skuSpec, Map.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                for (String value : specMap.values()) {
                    stringBuilder.append(" ").append(value);
                }
                productSku.setSkuName(stringBuilder.toString()); // 设置 SKU 名称
                return productSku;
            }).collect(Collectors.toList());
            productSkuService.updateBatchById(collect);
        }
        // 3. 修改 DETAILS 信息
        String detailsImageUrls = productDto.getDetailsImageUrls();
        if (StringUtils.isNotEmpty(detailsImageUrls)) {
            ProductDetails productDetails = new ProductDetails();
            productDetails.setProductId(productDto.getId());
            productDetails.setImageUrls(detailsImageUrls);
            productDetailsService.update(productDetails, new LambdaQueryWrapper<ProductDetails>().eq(ProductDetails::getProductId, productDto.getId()));
        }
    }

    @Override
    @Transactional
    public void deleteProductById(List<Long> ids) {
        // 2. 删除 SKU 信息
        productSkuService.remove(new LambdaQueryWrapper<ProductSku>().in(ProductSku::getProductId, ids));
        // 3. 删除 DETAILS 信息
        productDetailsService.remove(new LambdaQueryWrapper<ProductDetails>().in(ProductDetails::getProductId, ids));
        // 1. 删除 SPU　信息
        removeBatchByIds(ids);
    }

    @Override
    public void updateAuditStatus(Long id, Integer auditStatus) {
        Product product = new Product();
        product.setId(id);
        if(auditStatus == 1) {
            product.setAuditStatus(1);
            product.setAuditMessage("审批通过");
        } else {
            product.setAuditStatus(-1);
            product.setAuditMessage("审批不通过");
        }
        updateById(product);
    }

    @Override
    @Transactional
    public void updateStatus(List<Long> ids, Integer status) {
        List<Product> productList = ids.stream().map(id -> {
            Product product = new Product();
            product.setId(id);
            product.setStatus(status);
            return product;
        }).collect(Collectors.toList());
        updateBatchById(productList);
    }

}
