package com.kai.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kai.waimai.dto.DishDto;
import com.kai.waimai.entity.Dish;

public interface DishService extends IService<Dish> {
    //新增菜品同时插入口味数据 对两张表进行操作 dish.dish_flavor

    public void saveWithFlavor(DishDto dishDto);


    //根据id查询菜品信息和对应的口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息和口味信息
    public void updateWithFlavor(DishDto dishDto);
}
