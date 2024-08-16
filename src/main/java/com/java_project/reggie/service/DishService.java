package com.java_project.reggie.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.java_project.reggie.dto.DishDto;
import com.java_project.reggie.entity.Dish;
import com.java_project.reggie.mapper.DishMapper;
import org.springframework.stereotype.Service;

import java.util.List;


public interface DishService extends IService<Dish> {
    //新增菜品，拓展方法同时操作两张表
    public void saveWithFlavor(DishDto dishDto);

    //新增
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息和口味信息
    public void updateWithFlavor(DishDto dishDto);

    //删除菜品
    public void deleteDish(List<Long> ids);

}
