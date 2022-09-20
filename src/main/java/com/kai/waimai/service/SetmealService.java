package com.kai.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kai.waimai.dto.SetmealDto;
import com.kai.waimai.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public void saveWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);


}
