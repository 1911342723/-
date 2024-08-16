package com.java_project.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java_project.reggie.common.CustomException;
import com.java_project.reggie.dto.DishDto;
import com.java_project.reggie.dto.SetmealDto;
import com.java_project.reggie.entity.Dish;
import com.java_project.reggie.entity.DishFlavor;
import com.java_project.reggie.entity.Setmeal;
import com.java_project.reggie.entity.SetmealDish;
import com.java_project.reggie.mapper.SetMealMapper;
import com.java_project.reggie.service.SetMealDishService;
import com.java_project.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetMealDishService setMealDishService;
    /*
  新增套餐，并维持套餐与菜品的关系
  * */
    @Override
    public void saveWitgDish(SetmealDto setmealDto){
        //保存套餐的基本信息，保存到套餐表，操作setmeal
        this.save(setmealDto);

        //获取dto里面的菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //使用Stream流循环保存id
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品的关联信息
        setMealDishService.saveBatch(setmealDishes);
    }

    @Override
    public void deleteWithDish(List<Long> ids) {
        //先查询套餐的销售状态，在售不可删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
            //第一个条件，查找id与ids匹配的，用in关键字
        queryWrapper.in(Setmeal::getId,ids);
            //第二个条件,看是否为在售状态
        queryWrapper.eq(Setmeal::getStatus,1);
            //计数
        int count = this.count(queryWrapper);

        //如果不能删除，抛出异常
        if (count>0){
            throw new  CustomException("套餐有菜品在售，不可删除!");
        }
        //如果可以删除，就删除数据即可，一个是SetMeal表，
        this.removeByIds(ids);

        //另一个是SetMealDish表，由于在该表内,ids并非主键，所以不可以直接调用removeByids
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);//获取套餐的id
        setMealDishService.remove(dishLambdaQueryWrapper);
    }


    /*页面回显*/
    @Override
    public SetmealDto getWithDish(Long id) {
        //查询套餐的基本信息
        Setmeal setmeal =  this.getById(id);
        //创建一个dto对象
        SetmealDto setmealDto = new SetmealDto();
        //拷贝setmeal信息到新的dto对象，这样套餐的普通基本信息就有了，接下来是回显包含的菜品和套餐分类
        BeanUtils.copyProperties(setmeal,setmealDto);

        //查询分类名称和包含菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件,根据id匹配菜品
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setMealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);//设置包含的菜品

        return setmealDto;
    }


    /*修改套餐*/
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，保存到套餐表，操作setmeal
        this.updateById(setmealDto);

        //获取dto里面的菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //使用Stream流循环保存id
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品的关联信息
        setMealDishService.saveBatch(setmealDishes);
    }


}
