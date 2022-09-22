package com.kai.waimai.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kai.waimai.dto.DishDto;
import com.kai.waimai.entity.*;
import com.kai.waimai.service.CategoryService;
import com.kai.waimai.service.DishFlavorService;
import com.kai.waimai.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

//菜品管理


@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
        @Autowired
        private DishFlavorService dishFlavorService;

        @Autowired
        private DishService dishService;

        @Autowired
        private CategoryService categoryService;


        @Autowired
        private RedisTemplate redisTemplate;




        @PostMapping
        public R<String> save(@RequestBody  DishDto dishDto){
                log.info(dishDto.toString());
                dishService.saveWithFlavor(dishDto);
               return R.success("新增菜品成功");
        }


        @GetMapping("/page")
        public R<Page> page(int page,int pageSize,String name){
                log.info("{} {}",page,pageSize);

                Page<Dish> pageInfo=new Page(page,pageSize);

                Page<DishDto> dtoPage=new Page<>();



                LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper();

                queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);

                queryWrapper.orderByDesc(Dish::getUpdateTime);

                dishService.page(pageInfo,queryWrapper);





                BeanUtils.copyProperties(pageInfo,dtoPage,"records");

                List<Dish> records = pageInfo.getRecords();

                List<DishDto> list= records.stream().map((item) -> {

                        DishDto dishDto = new DishDto();
                        BeanUtils.copyProperties(item, dishDto);

                        Long categoryId = item.getCategoryId();

                        Category category = categoryService.getById(categoryId);
                        if(category!=null){
                                String name1 = category.getName();
                                dishDto.setCategoryName(name1);
                        }

                        return dishDto;
                }).collect(Collectors.toList());


                dtoPage.setRecords(list);

                return R.success(dtoPage);
        }



        @GetMapping("/{id}")
        public R<DishDto> get(@PathVariable  Long id){
                DishDto dishDto = dishService.getByIdWithFlavor(id);
                return  R.success(dishDto);
        }


        @PutMapping
        public R<String> update(@RequestBody DishDto dishDto){

                dishService.updateWithFlavor(dishDto);


                //清理所有的菜品缓存并从数据库重新提交备份
//                Set keys = redisTemplate.keys("dish_*");
//                redisTemplate.delete(keys);


                //清理某个分类的缓存
                String key="dish_"+dishDto.getCategoryId()+"_1";
                redisTemplate.delete(key);

                return R.success("修改菜品成功");
        }



//
//        @GetMapping("/list")
//        public R<List<Dish>> getAll(Dish dish){
//
//
//                LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
//                queryWrapper.eq(Dish::getCategoryId,dish.getCategoryId());
//                queryWrapper.eq(Dish::getStatus,1);
//                queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getCreateTime);
//
//                List<Dish> list = dishService.list(queryWrapper);
//
//                return R.success(list);
//        }



        @GetMapping("/list")
        public R<List<DishDto>> getAll(DishDto dishDto){

                List<DishDto> dishDtoList=null;

                //动态构造key
                String key="dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();

                //先从redis中获取数据
                dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

                //如果不为空直接返回
                if(dishDtoList!=null){
                        return R.success(dishDtoList);
                }

                LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
                queryWrapper.eq(Dish::getCategoryId,dishDto.getCategoryId());
                queryWrapper.eq(Dish::getStatus,1);
                queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getCreateTime);

                List<Dish> dishList= dishService.list(queryWrapper);

                        dishDtoList= dishList.stream().map((item) -> {

                        DishDto dishDto1 = new DishDto();

                        BeanUtils.copyProperties(item, dishDto1);

                        Long categoryId = item.getCategoryId();

                        Category category = categoryService.getById(categoryId);
                        if(category!=null){
                                String name1 = category.getName();
                                dishDto1.setCategoryName(name1);
                        }

                        Long id = item.getId();

                        LambdaQueryWrapper<DishFlavor> queryWrapper1=new LambdaQueryWrapper<>();
                        queryWrapper1.eq(DishFlavor::getDishId,id);


                        List<DishFlavor> list = dishFlavorService.list(queryWrapper1);
                        dishDto1.setFlavors(list);
                        return dishDto1;
                }).collect(Collectors.toList());


                //如果数据为空，则从数据库中获取数据,缓存到redis中
                redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);


                return R.success(dishDtoList);
        }









}
