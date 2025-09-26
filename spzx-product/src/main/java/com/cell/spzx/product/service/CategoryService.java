package com.cell.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cell.model.entity.product.Category;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService extends IService<Category> {

    List<Category> getCategory();

    void exportData(HttpServletResponse response);

    void importData(MultipartFile file);

}
