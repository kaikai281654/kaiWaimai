package com.kai.waimai.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kai.waimai.dto.OrdersDto;
import com.kai.waimai.entity.Category;
import com.kai.waimai.entity.Orders;
import com.kai.waimai.entity.R;
import com.kai.waimai.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info(orders.toString());

        orderService.submit(orders);

        return R.success("下单成功");
    }


    @GetMapping("/userPage")
    public R getOrders(Integer page, Integer pageSize){
        Page<OrdersDto> ordersDtoPage = orderService.userPage(page,pageSize);
        return R.success(ordersDtoPage);
    }




}
