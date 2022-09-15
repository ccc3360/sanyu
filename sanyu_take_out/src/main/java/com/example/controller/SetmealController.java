package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.R;
import com.example.dto.SetmealDto;
import com.example.entity.Category;
import com.example.entity.Setmeal;
import com.example.entity.SetmealDish;
import com.example.service.CategoryService;
import com.example.service.SetmealDishService;
import com.example.service.SetmealService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @PostMapping
    public R<String> insert(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("");
    }

    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize,String name){
        Page<Setmeal> pageInfo=new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage=new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name)
                .orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> dtolist=records.stream().map((i)->{
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(i,setmealDto);
            Category category=categoryService.getById(i.getCategoryId());
            if(category!=null){
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(dtolist);
        return R.success(dtoPage);
    }


    @GetMapping("/{id}")
    public R<SetmealDto> getByid(@PathVariable Long id){
        SetmealDto setmealDto=new SetmealDto();
        Setmeal setmeal=setmealService.getById(id);
        BeanUtils.copyProperties(setmeal,setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list=setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> edit(@RequestBody SetmealDto setmealDto){
        setmealService.eidiWithDish(setmealDto);
        return R.success("");
    }

    @PostMapping("/status/{status}")
    public R<String> statuschange(@PathVariable Integer status,String ids){
        String[] idList=ids.split(",");
        for(String id: idList){
            Setmeal setmeal=new Setmeal();
            setmeal.setId(Long.parseLong(id));
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        }
        return R.success("");
    }

    @DeleteMapping
    public R<String> delete(String ids){
        String[] idList=ids.split(",");
        for(String id: idList){
            LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId,Long.parseLong(id));
            setmealDishService.remove(queryWrapper);

            setmealService.removeById(Long.parseLong(id));
        }
        return R.success("");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId,setmeal.getCategoryId())
        .eq(Setmeal::getStatus,setmeal.getStatus());
        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }



}
