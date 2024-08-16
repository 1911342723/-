package com.java_project.reggie.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java_project.reggie.common.CustomException;
import com.java_project.reggie.entity.Category;
import com.java_project.reggie.entity.Dish;
import com.java_project.reggie.entity.Employee;
import com.java_project.reggie.entity.Setmeal;
import com.java_project.reggie.mapper.CategoryMapper;
import com.java_project.reggie.mapper.EmployeeMapper;
import com.java_project.reggie.mapper.SetMealMapper;
import com.java_project.reggie.service.CategoryService;
import com.java_project.reggie.service.DishService;
import com.java_project.reggie.service.EmployeeService;
import com.java_project.reggie.service.SetmealService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;


    @Override
    public void remove(Long id) {
        //查询当前菜品是否关联，如果关联，抛出异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper =new LambdaQueryWrapper<>();

        //添加查询条件
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);

        int count = dishService.count();
        if(count>0){
            throw new CustomException("当前分类已关联菜品，不可删除！");
        }

        //是否关联套餐，如果关联，抛出异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper =new LambdaQueryWrapper<>();

        //添加查询条件
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count();
        if(count2>0){
            //关联套餐了，抛出异常
            throw new CustomException("当前分类已关联套餐，不可删除！");
        }

        super.removeById(id);


        //如果都没关联，可以删除


    }
}
