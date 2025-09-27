package com.cell.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.dto.BrandDto;
import com.cell.model.dto.h5.BrandQueryDto;
import com.cell.model.entity.product.Brand;
import com.cell.model.vo.h5.PageResult;
import com.cell.spzx.common.utils.MinioUtil;
import com.cell.spzx.product.mapper.BrandMapper;
import com.cell.spzx.product.service.BrandService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

    // 配置 log
    private static final Logger log = Logger.getLogger(BrandServiceImpl.class.getName());

    @Autowired
    private MinioUtil minioUtil;

    @Override
    public PageResult<Brand> listBrandPage(BrandQueryDto brandQueryDto) {
        LambdaQueryWrapper<Brand> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (brandQueryDto.getName() != null && !brandQueryDto.getName().isEmpty()) {
            lambdaQueryWrapper.eq(Brand::getName, brandQueryDto.getName());
        }
        Page<Brand> page = new Page<>(brandQueryDto.getPage(), brandQueryDto.getSize());
        Page<Brand> pageResult = page(page, lambdaQueryWrapper);
        return new PageResult<Brand>(pageResult.getTotal(), pageResult.getPages(), pageResult.getRecords());
    }

    @Override
    public void addBrand(BrandDto brandDto) {
        // 检查传递来的 BrandDto 中是否包含 Logo URL
        String currentLogoUrl = brandDto.getLogo();
        // 将临时目录下的 logo 拷贝到 use 目录，如果返回为空，证明没有上传图片
        String newLogoUrl = minioUtil.copyMinioTempToCurrent(currentLogoUrl);
        if (currentLogoUrl != null && newLogoUrl != null) {
            brandDto.setLogo(newLogoUrl);
        }
        // 当 currentLogoUrl 为空时，说明没有进行上传 Logo 的操作，那么存入数据库时使用空的 URL　即可（前端未点击上传时即为空）
        Brand brand = new Brand();
        BeanUtils.copyProperties(brandDto, brand);
        save(brand);
    }

    @Override
    public String uploadLogo(MultipartFile file) {
        return minioUtil.upload(file);
    }

    @Override
    @Transactional
    public void updateBrand(Brand brand) {
        String oldLogoUrl = getById(brand.getId()).getLogo();
        String currentLogoUrl = brand.getLogo();
        // 当传入的 Logo 不为空时，则需要判断是新增的头像，还是原始的头像
        if (currentLogoUrl != null) {
            // 只有 url 中包含临时路径 /temp 才会通过该方法返回修改为使用中路径 /use
            String newLogoUrl = minioUtil.copyMinioTempToCurrent(currentLogoUrl);
            // 如果不为空，证明更新了 Logo，那么就要查询数据库删除以前的存放在 /use 中的旧 Logo
            if (newLogoUrl != null && oldLogoUrl != null) {
                minioUtil.deleteMinioOldFile(oldLogoUrl);
                brand.setLogo(newLogoUrl);
            }
        }
        updateById(brand);
    }

    @Override
    public Brand getBrandById(Long id) {
        return getById(id);
    }

    @Override
    @Transactional
    public void deleteBrand(List<Long> ids) {
        List<Brand> deleteBrandList = list(new LambdaQueryWrapper<Brand>().in(Brand::getId, ids));
        List<String> logoList = deleteBrandList.stream().map(Brand::getLogo).collect(Collectors.toList());
        removeBatchByIds(ids);
        minioUtil.deleteBatchMinioFile(logoList);
    }

    @Override
    @Cacheable(value = "allBrand", key = "'getAllBrand'")
    public List<Brand> getAllBrand() {
        return list();
    }


}
