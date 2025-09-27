package com.cell.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cell.model.dto.BrandDto;
import com.cell.model.dto.h5.BrandQueryDto;
import com.cell.model.entity.product.Brand;
import com.cell.model.vo.h5.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BrandService extends IService<Brand> {

    PageResult<Brand> listBrandPage(BrandQueryDto brandQueryDto);

    void addBrand(BrandDto brandDto);

    String uploadLogo(MultipartFile file);

    void updateBrand(Brand brand);

    Brand getBrandById(Long id);

    void deleteBrand(List<Long> ids);

    List<Brand> getAllBrand();

}
