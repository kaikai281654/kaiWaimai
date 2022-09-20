package com.kai.waimai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kai.waimai.common.BaseContext;
import com.kai.waimai.common.CustomException;
import com.kai.waimai.dto.OrdersDto;
import com.kai.waimai.entity.*;
import com.kai.waimai.mapper.OrderMapper;
import com.kai.waimai.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService cartService;

    @Autowired
    private OrderService orderService;


    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private UserService service;



    @Autowired
    private OrderDetailService orderDetailService;


    @Transactional
    @Override
    public void submit(Orders orders) {
        //获得当前用户的id
        Long currentId = BaseContext.getCurrentId();


        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        List<ShoppingCart> shoppingCartslist = cartService.list(queryWrapper);

        if(shoppingCartslist==null||shoppingCartslist.size()==0){
            throw  new CustomException("购物车没有数据");
        }

        //查询用户数据
        User user = service.getById(currentId);

        //查询地址
        AddressBook Address = addressBookService.getById(orders.getAddressBookId());
        if(Address==null){
            throw new CustomException("该用户没有设置地址");
        }




        long orderId = IdWorker.getId();//订单号

        AtomicInteger amount=new AtomicInteger(0);

        List<OrderDetail> orderDetails=shoppingCartslist.stream().map((item)->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());

            return orderDetail;
        }).collect(Collectors.toList());



        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(Address.getConsignee());
        orders.setPhone(Address.getPhone());
        orders.setAddress((Address.getProvinceName() == null ? "" : Address.getProvinceName())
                + (Address.getCityName() == null ? "" : Address.getCityName())
                + (Address.getDistrictName() == null ? "" : Address.getDistrictName())
                + (Address.getDetail() == null ? "" : Address.getDetail()));

        //向订单表插入数据,一条数据
        this.save(orders);


        //向订单详情表插入多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        cartService.remove(queryWrapper);


    }

    @Override
    public Page<OrdersDto> userPage(Integer page, Integer pageSize) {
        Page<Orders> orders = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        Long userId = BaseContext.getCurrentId();
        // 获取用户信息
        User user = service.getById(userId);
        AddressBook address = addressBookService.getOne(
                new LambdaQueryWrapper<AddressBook>()
                        .eq(AddressBook::getUserId, userId).
                        eq(AddressBook::getIsDefault, true));
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId,userId);
        page(orders, wrapper);
        List<OrdersDto> records = orders.getRecords().stream().map(item->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            ordersDto.setUserName(user.getName());
            BeanUtils.copyProperties(address, ordersDto);
            List<OrderDetail> list = orderDetailService.list(
                    new LambdaQueryWrapper<OrderDetail>()
                            .eq(OrderDetail::getOrderId, item.getId()));
            ordersDto.setOrderDetails(list);
            return ordersDto;
        }).collect(Collectors.toList());

        BeanUtils.copyProperties(orders, ordersDtoPage);
        ordersDtoPage.setRecords(records);
        return ordersDtoPage;
    }



    
}
