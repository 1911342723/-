package com.java_project.reggie.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.java_project.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submit(Orders orders);

    public boolean updateOrderStatus(Long id, Integer status);
}
