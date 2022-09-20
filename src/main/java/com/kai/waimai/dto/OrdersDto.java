package com.kai.waimai.dto;


import com.kai.waimai.entity.OrderDetail;
import com.kai.waimai.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;

}
