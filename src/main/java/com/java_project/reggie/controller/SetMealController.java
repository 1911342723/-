package com.java_project.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java_project.reggie.common.R;
import com.java_project.reggie.dto.SetmealDto;
import com.java_project.reggie.entity.DishFlavor;
import com.java_project.reggie.entity.Employee;
import com.java_project.reggie.entity.Setmeal;
import com.java_project.reggie.service.SetMealDishService;
import com.java_project.reggie.service.SetmealService;
import com.java_project.reggie.service.impl.SetmealServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetMealController {
    @Autowired
    private SetMealDishService setMealDishService;

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息：{}" ,setmealDto);
        setmealService.saveWitgDish(setmealDto);
        return R.success("添加成功！");
    }


    /*分页查询
    * */
    @GetMapping("/page")
    public R<Page> page (Integer page, Integer pagesize,String name){
        log.info("page:{} , pagesize:{}.name:{}",page,pagesize);
        if (pagesize == null) {
            pagesize = 10; // 提供默认值
        }
        //构建分页构造器，基于Mybatis-plus的插件
        Page pageInfo = new Page(page,pagesize);

        //构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper =new LambdaQueryWrapper();
        //添加过滤条件,这里用like，相似度查询比较好，会查找相似的名称
        queryWrapper.like(!StringUtils.isEmpty(name),Setmeal::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行查询
        setmealService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /*
    * 删除套餐
    * */
    @DeleteMapping
    public R<String> deleteSetMead(@RequestParam List<Long> ids){
        //删除套餐，要把和菜品的关系也删除
        setmealService.deleteWithDish(ids);
        return R.success("删除成功！");
    }

    /*
     * 状态更改
     * */
    @PostMapping("/status/{status}")
    public R<String> SetMealChange(@PathVariable int status,@RequestParam List<Long> ids){
        //获取用户id
//        Long EmpId = (Long) request.getSession().getAttribute("employee");
//        //设置更新信息
//        employee.setUpdateUser(EmpId);
//        employee.setUpdateTime(LocalDateTime.now());
        // 更新数据库
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids); // 根据传入的ids筛选出需要更新的Setmeal

        // 假设你有一个setmealService来操作数据库
        List<Setmeal> setmealsToUpdate = setmealService.list(queryWrapper);

        for (Setmeal setmeal : setmealsToUpdate) {
            setmeal.setStatus(status); // 设置新的状态码
        }
        setmealService.updateBatchById(setmealsToUpdate);
        return R.success("更新成功！");
    }

    /*作一个套餐修改的页面回显*/
    @GetMapping("/{id}")
    public R<Setmeal> SetMealChangeBcak(@PathVariable Long id){
        SetmealDto setmealServiceWithDish = setmealService.getWithDish(id);
        return R.success(setmealServiceWithDish);
    }


    /*
    * 修改并保存套餐
    * */
    @PutMapping
    public R<String> SetMealChange(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("更新成功");
    }


    /*根据分类id查询套餐信息*/
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }
}
