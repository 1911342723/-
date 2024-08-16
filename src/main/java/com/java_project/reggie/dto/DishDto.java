package com.java_project.reggie.dto;


import com.java_project.reggie.entity.Dish;
import com.java_project.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/*用于传输菜品的信息，除了Dish属性以外，还有口味等等其他的*/
@Data
public class DishDto extends Dish {

    //菜品口味
    private List<DishFlavor> flavors = new ArrayList<>();
    //分类名称
    private String categoryName;

    private Integer copies;
}
