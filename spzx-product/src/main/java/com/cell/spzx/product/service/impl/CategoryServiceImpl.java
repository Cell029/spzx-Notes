package com.cell.spzx.product.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.entity.product.Category;
import com.cell.model.vo.product.CategoryExcelVo;
import com.cell.spzx.product.listener.ExcelListener;
import com.cell.spzx.product.mapper.CategoryMapper;
import com.cell.spzx.product.service.CategoryService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    @Cacheable(value = "category", key = "'Level1Categories'")
    public List<Category> getCategory() {
        Map<Long, List<Category>> allCategories = list().stream().collect(Collectors.groupingBy(Category::getParentId));
        return buildTreeCategory(0L, allCategories);
    }

    @Override
    public void exportData(HttpServletResponse response) {
        try {
            // 2. 查询数据库中的数据
            List<Category> categoryList = list();
            List<CategoryExcelVo> categoryExcelVoList = new ArrayList<>(categoryList.size());
            // 将从数据库中查询到的 Category 对象转换成 CategoryExcelVo 对象
            for (Category category : categoryList) {
                CategoryExcelVo categoryExcelVo = new CategoryExcelVo();
                BeanUtils.copyProperties(category, categoryExcelVo);
                categoryExcelVoList.add(categoryExcelVo);
            }
            // 3. 写出数据到浏览器端
            EasyExcel.write(response.getOutputStream(), CategoryExcelVo.class).sheet("分类数据").doWrite(categoryExcelVoList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void importData(MultipartFile file) {
        try {
            // 传入 CategoryService 到监听器
            ExcelListener<CategoryExcelVo> excelListener = new ExcelListener<>(this);
            // 调用 EasyExcel 读取
            EasyExcel.read(file.getInputStream(),
                    CategoryExcelVo.class,
                    excelListener).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Category> buildTreeCategory(Long parentId, Map<Long, List<Category>> allCategories) {
        List<Category> categoryList = new ArrayList<>();
        // 获取 parentId 下的子节点
        List<Category> childCategoryList = allCategories.get(parentId);
        if (childCategoryList != null && !childCategoryList.isEmpty()) {
            for (Category childCategory : childCategoryList) {
                // 查找是否有以本节点的 id 作为 parentId 的节点
                List<Category> nextChildCategoryList = buildTreeCategory(childCategory.getId(), allCategories);
                childCategory.setChildren(nextChildCategoryList);
                categoryList.add(childCategory);
            }
        }
        return categoryList;
    }

}
