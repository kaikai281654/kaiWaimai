package com.kai.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kai.waimai.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
