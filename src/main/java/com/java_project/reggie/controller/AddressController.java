package com.java_project.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.java_project.reggie.common.BaseContext;
import com.java_project.reggie.common.R;
import com.java_project.reggie.entity.AddressBook;
import com.java_project.reggie.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressController {

    @Autowired
    private AddressService addressService;



    /*新增地址*/
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook ,HttpSession session){
        addressBook.setUserId(BaseContext.getThreadLocal());
        addressService.save(addressBook);
        return R.success(addressBook);
    }

    /*
     * 设置默认地址
     * */

    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        //匹配
        wrapper.eq(AddressBook::getUserId,BaseContext.getThreadLocal());
        //设置为默认地址
        wrapper.set(AddressBook::getIsDefault,0);
        addressService.update(wrapper);

        addressBook.setIsDefault(1);

        addressService.updateById(addressBook);

        return R.success(addressBook);

    }

    /*根据id查地址*/
    @GetMapping("/{id}")
    public R get(@PathVariable Long id){
        AddressBook addressBook = addressService.getById(id);

        if(addressBook!=null){
            return R.success(addressBook);
        }
        return R.error("没有找到该地址");
    }

    /*查默认地址*/
    @GetMapping("default")
    public R<AddressBook> getDefult(){
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(AddressBook::getUserId,BaseContext.getThreadLocal());
        lambdaQueryWrapper.eq(AddressBook::getIsDefault,1);

        AddressBook addressBook = addressService.getOne(lambdaQueryWrapper);

        if(addressBook!=null){
            return R.success(addressBook);
        }
        return R.error("没有找到该地址！");
    }


    /*查询指定用户的全部地址*/
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook){
        addressBook.setUserId(BaseContext.getThreadLocal());

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(addressBook.getUserId()!=null,AddressBook::getUserId,addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        //返回传结果
        return R.success(addressService.list(queryWrapper));
    }
}


