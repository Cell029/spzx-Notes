package com.cell.spzx.product.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.cell.model.entity.product.Category;
import com.cell.spzx.product.service.CategoryService;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.stream.Collectors;

public class ExcelListener<T> extends AnalysisEventListener<T> {

    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;
    /**
     * 缓存的数据
     */
    private List<T> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    //获取 mapper 对象
    private CategoryService categoryService;

    public ExcelListener(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 每解析一行数据就会调用一次该方法
    @Override
    public void invoke(T data, AnalysisContext analysisContext) {
        cachedDataList.add(data);
        // 达到 BATCH_COUNT 了，需要去存储一次数据库，防止数据几万条数据在内存，容易 OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // excel解析完毕以后需要执行的代码
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
    }

    private void saveData() {
        // 将 CategoryExcelVo 转换为 Category 实体
        List<Category> categoryList = cachedDataList.stream()
                .map(vo -> {
                    Category category = new Category();
                    BeanUtils.copyProperties(vo, category);
                    return category;
                })
                .collect(Collectors.toList());
        categoryService.saveBatch(categoryList);
    }
}
