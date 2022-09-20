package com.kai.waimai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kai.waimai.dto.OrdersDto;
import com.kai.waimai.entity.Orders;

public interface OrderService extends IService<Orders> {
    public void submit(Orders orders);


    Page<OrdersDto> userPage(Integer page, Integer pageSize);


}
