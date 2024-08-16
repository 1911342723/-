package com.java_project.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java_project.reggie.common.R;
import com.java_project.reggie.entity.Orders;
import com.java_project.reggie.service.OrderDtailService;
import com.java_project.reggie.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDtailService orderDtailService;

    /*
    * 用户下单
    * */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("下单成功！");
    }

    /*订单详细查询*/
    @GetMapping("/userPage")
    public R<Page> page(Integer page,Integer pagesize){
        if (pagesize == null) {
            pagesize = 5; // 提供默认值
        }
        //构建分页构造器，基于Mybatis-plus的插件
        Page pageInfo = new Page(page,pagesize);

        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper =new LambdaQueryWrapper();
        //添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //执行查询
        //queryWrapper.eq(Orders::getUserId, BaseContext.getThreadLocal());
        //List<Orders> list =null;


        //list.set(1,orderService.getOne(queryWrapper));
        //pageInfo.setOrders(list);
        orderService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /*管理端的订单明细*/
    @GetMapping("/page")
    public R<Page> page2(Integer page,Integer pagesize){
        if (pagesize == null) {
            pagesize = 10; // 提供默认值
        }
        //构建分页构造器，基于Mybatis-plus的插件
        Page pageInfo = new Page(page,pagesize);

        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper =new LambdaQueryWrapper();
        //添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //执行查询
        //queryWrapper.eq(Orders::getUserId, BaseContext.getThreadLocal());
        //List<Orders> list =null;


        //list.set(1,orderService.getOne(queryWrapper));
        //pageInfo.setOrders(list);
        orderService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /*管理端点击派送*/
    @PutMapping
    public R<String> updateOrderStatus(@RequestBody Orders request) {

        boolean updated = orderService.updateOrderStatus(request.getId(), request.getStatus());

        if (updated) {
            return R.success("Order status updated successfully.");
        } else {
            return R.error("修改失败");
        }
    }
}
