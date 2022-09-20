package com.kai.waimai.dto;


import com.kai.waimai.entity.Setmeal;
import com.kai.waimai.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
