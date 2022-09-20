package com.kai.waimai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kai.waimai.entity.DishFlavor;
import com.kai.waimai.mapper.DisFlavorMapper;
import com.kai.waimai.service.DishFlavorService;
import org.springframework.stereotype.Service;


@Service
public class DishFlavorServiceImpl extends ServiceImpl<DisFlavorMapper, DishFlavor> implements DishFlavorService {
}
