package com.java_project.reggie.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java_project.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
