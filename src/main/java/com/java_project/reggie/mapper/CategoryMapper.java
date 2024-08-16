package com.java_project.reggie.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java_project.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;


//实现Mybatis的接口，用于操作数据库
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
