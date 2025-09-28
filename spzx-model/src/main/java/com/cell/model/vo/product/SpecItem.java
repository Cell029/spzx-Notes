package com.cell.model.vo.product;

import lombok.Data;

import java.util.List;

@Data
public class SpecItem {
    private String key;
    private List<String> valueList;
}

