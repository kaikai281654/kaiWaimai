package com.kai.waimai.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kai.waimai.common.CustomException;
import com.kai.waimai.entity.Category;
import com.kai.waimai.entity.Dish;
import com.kai.waimai.entity.Setmeal;
import com.kai.waimai.mapper.CategoryMapper;
import com.kai.waimai.service.CategoryService;
import com.kai.waimai.service.DishService;
import com.kai.waimai.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Autowired
    private DishService dishService;


    @Autowired
    private SetmealService setmealService;

    //根据id删除分类
    @Override
    public void remove(Long id) {

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(dishLambdaQueryWrapper);



        //查询当前分类是否关联了菜品
           if(count>0){
               throw  new CustomException("当前分类已关联菜品");
           }
        //查询当前分类是否关联了套餐，若关联则抛出异常
            LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
           setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
           int count1=setmealService.count(setmealLambdaQueryWrapper);
           if(count1>0){
               throw  new CustomException("当前分类已关联套餐");
           }



        //正常删除分类
        super.removeById(id);

    }
}
