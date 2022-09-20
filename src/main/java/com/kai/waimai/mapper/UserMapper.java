package com.kai.waimai.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kai.waimai.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
