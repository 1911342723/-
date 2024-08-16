package com.java_project.reggie.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.java_project.reggie.dto.SetmealDto;
import com.java_project.reggie.entity.Setmeal;
import org.springframework.stereotype.Service;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {
    /*
  新增套餐，并维持套餐与菜品的关系
  * */
    public void saveWitgDish(SetmealDto setmealDto);

    /*删除套餐，并且删除菜品与套餐的关系（SetMealDish表）*/
    public void deleteWithDish(List<Long> ids);

    /*
    * 页面回显
    * */
    public SetmealDto getWithDish(Long id);

    public void updateWithDish(SetmealDto setmealDto);
}
