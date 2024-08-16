package com.java_project.reggie.dto;

import com.java_project.reggie.entity.Setmeal;
import com.java_project.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    //包含的菜品列表
    private List<SetmealDish> setmealDishes;
    //分类的名称
    private String categoryName;
}
