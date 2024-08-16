package com.java_project.reggie.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java_project.reggie.common.CustomException;
import com.java_project.reggie.dto.DishDto;
import com.java_project.reggie.entity.Dish;
import com.java_project.reggie.entity.DishFlavor;
import com.java_project.reggie.entity.Setmeal;
import com.java_project.reggie.entity.SetmealDish;
import com.java_project.reggie.mapper.DishMapper;
import com.java_project.reggie.service.DishFlavorService;
import com.java_project.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    //新增菜品，保存数据的重写方法
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品基本信息到Dish表
        this.save(dishDto);

        //获取菜品id
        Long dishId = dishDto.getId();

        //菜品口味
        List<DishFlavor> flavors =dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
           item.setDishId(dishId);
           return item;
        }).collect(Collectors.toList());

        //保存到DishFlavor,批量保存集合
        dishFlavorService.saveBatch(flavors);
    }
//    ？数据回显
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品的基本信息
        Dish dish = this.getById(id);
        //创建一个dto对象
        DishDto dishDto = new DishDto();
        //拷贝dish信息到新的dto对象
        BeanUtils.copyProperties(dish,dishDto);

        //查询对应口味,从dish_flavor查,先创建一个构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件,根据id匹配
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(list);

        return dishDto;
    }


    /*更新菜品信息*/
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表
        this.updateById(dishDto);

        //清理当前菜品的口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加菜品信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        //设置id字段
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存到DishFlavor,批量保存集合
        dishFlavorService.saveBatch(flavors);

    }


    /*重写删除的方法*/
    @Override
    public void deleteDish(List<Long> ids) {
        //先查询套餐的销售状态，在售不可删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //第一个条件，查找id与ids匹配的，用in关键字
        queryWrapper.in(Dish::getId,ids);
        //第二个条件,看是否为在售状态
        queryWrapper.eq(Dish::getStatus,1);
        //计数
        int count = this.count(queryWrapper);

        //如果不能删除，抛出异常
        if (count>0){
            throw new CustomException("套餐有菜品在售，不可删除!");
        }
        //如果可以删除，就删除数据即可，一个是SetMeal表，
        this.removeByIds(ids);
    }
}
