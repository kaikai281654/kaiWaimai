package com.kai.waimai.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kai.waimai.entity.Category;
import com.kai.waimai.entity.R;
import com.kai.waimai.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        log.info("category{}",category);
        return R.success("新增分类成功");
    }

    //分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //分页构造器
        Page<Category> pageInfo=new Page<>(page,pageSize);

        //条件过滤器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        //添加排序条件 根据sort进行排序
        queryWrapper.orderByAsc(Category::getSort);


        //进行分页查询
        categoryService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

//删除分类
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除分类，id为{}",ids);


        categoryService.remove(ids);
        return R.success("删除成功");
    }


    //修改
    @PutMapping
    public R<String> update(@RequestBody Category category){

        log.info("修改分类信息{}",category);
        categoryService.updateById(category);
        return R.success("修改成功");
    }


    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(category.getType()!=null, Category::getType, category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getCreateTime);

        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }




}
