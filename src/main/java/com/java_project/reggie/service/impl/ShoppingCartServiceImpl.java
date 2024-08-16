package com.java_project.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java_project.reggie.entity.ShoppingCart;
import com.java_project.reggie.mapper.ShoppingCartmapper;
import com.java_project.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartmapper, ShoppingCart> implements ShoppingCartService {
}
