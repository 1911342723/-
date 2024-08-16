package com.java_project.reggie.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java_project.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
