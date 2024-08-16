package com.java_project.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java_project.reggie.common.R;
import com.java_project.reggie.entity.Employee;
import com.java_project.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(
            HttpServletRequest request,@RequestBody Employee employee
    ){
        //1：对密码进行加密处理
        String password = employee.getPassword();//获取密码
        password = DigestUtils.md5DigestAsHex(password.getBytes());//对密码进行加密处理

        //2：根据页面提交的用户名来查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
            //进行等值查询
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
            //创建当前用户登录的类，这里使用getOne是因为在表里，username是唯一约束
        Employee emp = employeeService.getOne(queryWrapper);

        //3：查询结果判断
        if(emp == null){
            return R.error("用户名不存在");
        }

        //4:如果存在就比对密码
        if(!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }

        //5：如果密码也正确，就开始判断员工的状态
        if(emp.getStatus() == 0){
            return R.error("账号已被禁用");
        }
        //6:登陆成功了,将员工id存入session并返回成功登陆结果，这里有个细节，这里存储了，后续的过滤器就能查看是否存在这个属性判断用户是否登陆了
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }



    /*
    * 员工退出
    * */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /*
    * 新增员工
    * */
    @PostMapping
    //通用更新方法
    public R<String> UpdateEmployee(HttpServletRequest request,@RequestBody Employee employee){
        //设置初始的密码，并通过MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        //设置创建时间和更新时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

//        //设置创建者和更新者
//        Long emp = (Long) request.getSession().getAttribute("employee");//这里获取登陆时记录在Session里的创建者信息
//        employee.setCreateUser(emp);
//        employee.setUpdateUser(emp);

        //这里直接使用Mybatis-plus的接口，即可保存，就不需要操作复杂的JDBC
        employeeService.save(employee);
        return R.success("创建成功!");
    }

    /*
    * 员工分页查询的请求处理器
    * */
    @GetMapping("/page")
    public R<Page> page (Integer page,Integer pagesize,String name){
        log.info("page:{} , pagesize:{}.name:{}",page,pagesize,name);
        if (pagesize == null) {
            pagesize = 10; // 提供默认值
        }
        //构建分页构造器，基于Mybatis-plus的插件
        Page pageInfo = new Page(page,pagesize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper =new LambdaQueryWrapper();
        //添加过滤条件,这里用like，相似度查询比较好，会查找相似的名称
        queryWrapper.like(!StringUtils.isEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /*
    * 员工账号状态更改
    * */
    @PutMapping
    public R<String> EmployeeChange(HttpServletRequest request,@RequestBody Employee employee){
        //获取用户id
//        Long EmpId = (Long) request.getSession().getAttribute("employee");
//        //设置更新信息
//        employee.setUpdateUser(EmpId);
//        employee.setUpdateTime(LocalDateTime.now());
        //更新数据库
        log.info("staus:{}",employee.getStatus());
        employeeService.updateById(employee);
        return R.success("更新成功！");
    }

    /*
    * 员工信息编辑
    * */
    @GetMapping("/{id}")
    public R<Employee> EditEmployee(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if(employee!=null){
            return R.success(employee);
        }
        return R.error("员工信息不存在！");
    }
}
