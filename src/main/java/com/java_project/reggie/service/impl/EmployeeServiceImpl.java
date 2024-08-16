package com.java_project.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java_project.reggie.entity.Employee;
import com.java_project.reggie.mapper.EmployeeMapper;
import com.java_project.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

//按照Mybatis-plus规范继承
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
