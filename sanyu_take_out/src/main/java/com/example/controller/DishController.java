package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.R;
import com.example.dto.DishDto;
import com.example.entity.Category;
import com.example.entity.Dish;
import com.example.entity.DishFlavor;
import com.example.entity.SetmealDish;
import com.example.service.CategoryService;
import com.example.service.DishFlavorService;
import com.example.service.DishService;
import com.example.service.SetmealDishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> savedish(@RequestBody DishDto dishDto){
        if(dishService.saveWithFlavor(dishDto)) {
            return R.success("success");
        }
        return R.error("添加失败");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Dish> pageInfo=new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage=new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name)
                .orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);
//        对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category byId = categoryService.getById(categoryId);
            String categoryname=byId.getName();
            dishDto.setCategoryName(categoryname);
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> getByid(@PathVariable Long id ){
        Dish dish=dishService.getById(id);
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("success");
    }

    @PostMapping("/status/{status}")
    public R<String> statuschange(@PathVariable Integer status,String ids){
        String[] idList=ids.split(",");
        for(String id: idList){
            Dish dish=new Dish();
            dish.setId(Long.parseLong(id));
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("");
    }

    @DeleteMapping
    public R<String> delete(String ids){
        String[] idList=ids.split(",");
        for(String id: idList){
            LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(Dish::getId,Long.parseLong(id));
            LambdaQueryWrapper<SetmealDish> queryWrapper1=new LambdaQueryWrapper<>();
            queryWrapper1.eq(SetmealDish::getDishId,Long.parseLong(id));
            int count = setmealDishService.count(queryWrapper1);
            if (count>0){
                return R.error("菜品有关联套餐，删除失败");
            }
            dishService.remove(queryWrapper);
        }
        return R.success("");
    }

    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        System.out.println(dish);
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId())
                    .like(StringUtils.isNotEmpty(dish.getName()),Dish::getName,dish.getName())
                    .orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime)
                    .eq(Dish::getStatus,1);
        List<Dish> list=dishService.list(queryWrapper);
        return R.success(list);

    }
}
