package com.kai.waimai.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kai.waimai.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}