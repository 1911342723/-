package com.java_project.reggie.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.java_project.reggie.entity.Employee;

//这个接口继承了Mybatis-plus里的方法，用于实现数据库的查询，删除，增加等等
public interface EmployeeService extends IService<Employee> {
}
