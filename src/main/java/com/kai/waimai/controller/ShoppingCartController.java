package com.kai.waimai.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kai.waimai.common.BaseContext;
import com.kai.waimai.entity.R;
import com.kai.waimai.entity.ShoppingCart;
import com.kai.waimai.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {


    @Autowired
    private ShoppingCartService cartService;








    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info(shoppingCart.toString());

        Long currentId = BaseContext.getCurrentId();
        //给购物车表设置用户id
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        if(dishId!=null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart one = cartService.getOne(queryWrapper);

        if(one!=null){

            Integer integer = one.getNumber();
            one.setNumber(integer+1);
            cartService.updateById(one);

        }else{
            shoppingCart.setNumber(1);
            cartService.save(shoppingCart);
            one=shoppingCart;
        }
        //口味不同时
//        if(one!=null  &&   !(one.getDishFlavor()).equals(shoppingCart.getDishFlavor()) ){
//
//            cartService.save(shoppingCart);
//
//        }
        return R.success(one);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车");
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = cartService.list(queryWrapper);

        return R.success(list);
    }

    @DeleteMapping("/clean")
    public R<String> delete(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        cartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }






}
