package com.java_project.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java_project.reggie.entity.OrderDetail;
import com.java_project.reggie.mapper.OrderDatilMapper;
import com.java_project.reggie.service.OrderDtailService;
import com.java_project.reggie.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderDtailServiceImpl extends ServiceImpl<OrderDatilMapper, OrderDetail> implements OrderDtailService {
}
