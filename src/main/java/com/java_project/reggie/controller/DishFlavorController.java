package com.java_project.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java_project.reggie.common.R;
import com.java_project.reggie.dto.DishDto;
import com.java_project.reggie.entity.*;
import com.java_project.reggie.service.CategoryService;
import com.java_project.reggie.service.DishFlavorService;
import com.java_project.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequestMapping("/dish")
@Slf4j
@RestController
public class DishFlavorController {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /*分页查询*/
    @GetMapping("/page")
    public R<Page> page(int page, Integer pagesize, String name){
        log.info("page:{} , pagesize:{}", page, pagesize);
        if (pagesize == null) {
            pagesize = 10; // 提供默认值
        }
        // 构建分页构造器，基于Mybatis-plus的插件
        Page<Dish> pageInfo = new Page<>(page, pagesize);
        Page<DishDto> dishDtoPage = new Page<>();
        // 构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        // 添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);

        // 添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        // 执行分页查询

        dishService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();
            if (categoryId != null) {
                if (categoryService == null) {
                    throw new NullPointerException("CategoryService is null");
                }
                Category category = categoryService.getById(categoryId);
                if (category != null) {
                    String categoryName = category.getName();
                    if (categoryName != null) {
                        dishDto.setCategoryName(categoryName);
                    } else {
                        log.warn("Category name is null for categoryId: {}", categoryId);
                    }
                } else {
                    log.warn("Category is null for categoryId: {}", categoryId);
                }
            } else {
                log.warn("CategoryId is null for dish: {}", item.getId());
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }


    //处理新增菜品上传的JSON数据
    @PostMapping
    public R<String> upload(@RequestBody DishDto dishDto){
        //操作数据库，保存数据
        /*因为这里要同时操作两张表，Dish和DishFlavor
        * 所以这里要拓展一下*/
        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功！");
    }

    @GetMapping("/{id}")
    public R<DishDto> change (@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public  R<DishDto> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        //更新数据后，需要清理缓存，否则会造成数据不一致
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);

        return R.success(dishDto);
    }

    public DishFlavorService getDishFlavorService() {
        return dishFlavorService;
    }
    /*
    * 添加菜品，返回一个List集合
    * */
    @GetMapping("list")
    public R<List<DishDto>> list(Dish dish ){
        List<DishDto> dishDtoList = null;
        //动态构造key
        String key = "dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        //先从redis中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        //判断查询
        if(dishDtoList!=null){
            //说明有缓存数据
            return R.success(dishDtoList);
        }


        //没有数据就是没缓存，查询数据库
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
            //查询状态是1的，为在售状态的
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //开始查询
        List<Dish> list = dishService.list(queryWrapper);

        //bean拷贝，通过流的形式
        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();
            if (categoryId != null) {
                if (categoryService == null) {
                    throw new NullPointerException("CategoryService is null");
                }
                Category category = categoryService.getById(categoryId);
                if (category != null) {
                    String categoryName = category.getName();
                    if (categoryName != null) {
                        dishDto.setCategoryName(categoryName);
                    } else {
                        log.warn("Category name is null for categoryId: {}", categoryId);
                    }
                } else {
                    log.warn("Category is null for categoryId: {}", categoryId);
                }
            } else {
                log.warn("CategoryId is null for dish: {}", item.getId());
            }
            //当前菜品的id
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId,id);
            List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(dishFlavors);
            return dishDto;
        }).collect(Collectors.toList());

        //数据缓存到redis
        redisTemplate.opsForValue().set(key,dishDtoList,60L, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }

    /*
     * 菜品状态更改
     * */
    @PostMapping("/status/{status}")
    public R<String> DishStatusChange(@PathVariable int status,@RequestParam List<Long> ids){
        //获取用户id
//        Long EmpId = (Long) request.getSession().getAttribute("employee");
//        //设置更新信息
//        employee.setUpdateUser(EmpId);
//        employee.setUpdateTime(LocalDateTime.now());
        // 更新数据库
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids); // 根据传入的ids筛选出需要更新的Setmeal

        // 假设你有一个setmealService来操作数据库
        List<Dish> DishToUpdate = dishService.list(queryWrapper);

        for (Dish dish : DishToUpdate) {
            dish.setStatus(status); // 设置新的状态码
        }
        dishService.updateBatchById(DishToUpdate);
        return R.success("更新成功！");
    }


    /*
    * 菜品的删除
    * */
    @DeleteMapping
    public R<String> deleteDish(@RequestParam List<Long> ids){
        dishService.deleteDish(ids);
        return R.success("删除成功！");
    }

}
