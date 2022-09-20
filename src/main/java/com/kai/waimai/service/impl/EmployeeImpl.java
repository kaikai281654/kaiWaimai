package com.kai.waimai.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kai.waimai.entity.Employee;
import com.kai.waimai.mapper.EmployeeMapper;
import com.kai.waimai.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {


}
