package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.R;
import com.example.entity.Employee;
import com.example.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request 登陆成功将用户信息存入session
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
//        1、页面提交的密码md5加密处理
        String password =employee.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());
//        2、根据用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<Employee>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
//        3、没有查询到则返回登录失败
        if (emp==null){
            return R.error("登陆失败，用户名不存在");
        }
//        4、密码比对，不一致则返回失败
        if(!emp.getPassword().equals(password)){
            return R.error("登陆失败，密码错误");
        }
//        5、查看员工状态，若为已禁用则返回已禁用
        if(emp.getStatus()==0){
            return R.error("登陆失败，账号已禁用");
        }
//        6、登陆成功，员工id存入session
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理session终保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("登出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee,HttpServletRequest request){
//        设置初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        Long empid=(Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empid);
//        employee.setUpdateUser(empid);

        employeeService.save(employee);
        return R.success(null);
    }

    /**
     * 新增员工时检查用户名是否已存在
     * @param username
     * @return
     */
    @GetMapping("/check/{username}")
    public R<Employee> queryEmployeeByUsername(@PathVariable String username){
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,username);
        Employee emp = employeeService.getOne(queryWrapper);
        if(emp==null){
            return R.success(null);
        }
        System.out.println("yicunzazi");
        return R.error("用户名已存在");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> queryMemberList(Integer page,Integer pageSize,String name){
//        分页构造器
        Page pageInfo=new Page(page,pageSize);
//        条件构造器
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name)
        .orderByDesc(Employee::getUpdateTime)
        .ne(Employee::getName,"管理员");
//        执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);

    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        long id=Thread.currentThread().getId();
        log.info("线程id为：{}",id);
//        Long empid=(Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(empid);
//        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);
        return R.success("修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if(employee!=null){
        return R.success(employee);
        }
        return R.error("没查到");
    }
}
