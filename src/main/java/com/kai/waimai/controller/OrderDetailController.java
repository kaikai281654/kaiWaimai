package com.kai.waimai.controller;


import com.kai.waimai.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/")
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;


}
