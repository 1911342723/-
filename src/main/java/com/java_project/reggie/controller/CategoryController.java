package com.java_project.reggie.controller;


import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java_project.reggie.common.R;
import com.java_project.reggie.entity.Category;
import com.java_project.reggie.entity.Dish;
import com.java_project.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/category")
@RestController
public class CategoryController {
    @Autowired//自动装配注释
    private CategoryService categoryService;


    /*
    * 新增菜品
    * */

    @PostMapping
    public R<String> addDish(@RequestBody Category category){
        log.info("新增菜品：{}",category);
        //调用数据库，保存下来
        categoryService.save(category);
        return R.success("新增成功！");
    }

    /*
    * 分页查询
    * */
    @GetMapping("/page")
    public R<Page> page (int page, Integer pagesize){
        log.info("page:{} , pagesize:{}",page,pagesize);
        if (pagesize == null) {
            pagesize = 10; // 提供默认值
        }
        //构建分页构造器，基于Mybatis-plus的插件
        Page<Category> pageInfo = new Page<>(page,pagesize);
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort);

        //执行查询
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    //删除接口
    @DeleteMapping
    public R<String> deleteDish(Long id){
        log.info("删除菜品{}",id);

        categoryService.removeById(id);

        return R.success("删除成功!");
    }

    @PutMapping
    public R<String> changeCategory(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功！");
    }

    //获取菜品分类
    @GetMapping("list")
    public R<List<Category>> list(Category category) {
        // 添加条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        // 添加条件
        if (category.getType() != null) {
            queryWrapper.eq(Category::getType, category.getType());
        }

        // 添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }


}
