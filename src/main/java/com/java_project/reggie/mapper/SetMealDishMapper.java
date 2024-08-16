package com.java_project.reggie.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java_project.reggie.dto.SetmealDto;
import com.java_project.reggie.entity.Setmeal;
import com.java_project.reggie.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetMealDishMapper extends BaseMapper<SetmealDish>{


}
