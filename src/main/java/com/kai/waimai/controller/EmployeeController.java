package com.kai.waimai.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kai.waimai.entity.Employee;
import com.kai.waimai.entity.R;
import com.kai.waimai.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //md5加密
        String password=employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());

        //根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> QW = new LambdaQueryWrapper<>();
        QW.eq(Employee::getUsername,employee.getUsername());
        Employee emp=employeeService.getOne(QW);
        if(emp==null){
            return  R.error("登陆失败");
        }

        //密码比对
        if(!emp.getPassword().equals(password)){
            return R.error("登陆失败");
        }

        //查看员工状态
        if(emp.getStatus()==0){
            return R.error("账号被禁用");
        }

        request.getSession().setAttribute("employee",emp.getId());

        return R.success(emp);
    }

    //管理员退出功能
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request ){
        request.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }

    //新增员工
    @PostMapping
    public R<String> save(HttpServletRequest httpServletRequest,@RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());
        //赋予初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        employee.setCreateUser((Long) httpServletRequest.getSession().getAttribute("employee"));
//        employee.setUpdateUser((Long) httpServletRequest.getSession().getAttribute("employee"));

        employeeService.save(employee);

        return R.success("新增员工成功");



    }


    //员工信息分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo=new Page(page,pageSize);



        //构造条件过滤器
        LambdaQueryWrapper<Employee> queryWrapper =new LambdaQueryWrapper();

        //添加条件过滤
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);


        //添加条件排序
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }



    @PutMapping
    public R<String> update(HttpServletRequest httpServletRequest, @RequestBody Employee employee){
        log.info(employee.toString());
        Long employee1 = (Long) httpServletRequest.getSession().getAttribute("employee");
        employee.setUpdateUser(employee1);
        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);

        long id = Thread.currentThread().getId();
        log.info("controller中执行update操作的线程id是：{}",id);
        return R.success("状态更新成功");
    }

    //数据回显功能
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){

        log.info("根据id查询员工");
        Employee employee= employeeService.getById(id);
        if(employee!=null){
            return R.success(employee);
        }

        return R.error("亲没有查到员工呢");

    }





}
