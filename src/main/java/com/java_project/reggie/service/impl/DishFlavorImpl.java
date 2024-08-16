package com.java_project.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java_project.reggie.entity.DishFlavor;
import com.java_project.reggie.mapper.DishFlavorMapper;
import com.java_project.reggie.mapper.DishMapper;
import com.java_project.reggie.service.DishFlavorService;
import com.java_project.reggie.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
