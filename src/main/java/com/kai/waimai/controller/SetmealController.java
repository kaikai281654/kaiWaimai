package com.kai.waimai.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kai.waimai.dto.SetmealDto;
import com.kai.waimai.entity.Category;
import com.kai.waimai.entity.R;
import com.kai.waimai.entity.Setmeal;
import com.kai.waimai.service.CategoryService;
import com.kai.waimai.service.SetmealDishService;
import com.kai.waimai.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealService setmealService;



    @PostMapping
    public R<String> save(@RequestBody  SetmealDto setmealDto){

        setmealService.saveWithDish(setmealDto);

        return R.success("操作成功");
    }


    @GetMapping("/page")
    @CacheEvict(value = "setmealCache" , allEntries = true)
    public R<Page> page(int page, int pageSize){

        Page<Setmeal> pageInfo=new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage=new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.orderByAsc(Setmeal::getCreateTime);

        setmealService.page(pageInfo,queryWrapper);


        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");

        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> collect = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            Long categoryId = item.getCategoryId();
            Category byId = categoryService.getById(categoryId);
            String name = byId.getName();

            BeanUtils.copyProperties(item, setmealDto);
            setmealDto.setCategoryName(name);
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(collect);

        return R.success(setmealDtoPage);

    }



    @DeleteMapping
    @CacheEvict(value = "setmealCache" , allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
        System.out.println(ids);

        setmealService.removeWithDish(ids);

        return R.success("删除成功");
    }


    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId + '_'+#setmeal.status")

    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper();

        queryWrapper.eq(setmeal.getCategoryId()!=null, Setmeal::getCategoryId, setmeal.getCategoryId());

        queryWrapper.eq(Setmeal::getStatus,setmeal.getStatus());

        queryWrapper.orderByAsc(Setmeal::getCreateTime).orderByAsc(Setmeal::getCreateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }



}
