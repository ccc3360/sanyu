package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dto.DishDto;
import com.example.entity.Dish;
import com.example.entity.DishFlavor;
import com.example.mapper.DishMapper;
import com.example.service.DishFlavorService;
import com.example.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService  {
    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品，同时插入菜品对应的口味
     */
    @Override
    @Transactional
    public boolean saveWithFlavor(DishDto dishDto) {
        //保存菜品消息
        this.save(dishDto);
        //保存菜品口味
        Long id = dishDto.getId();
        List<DishFlavor> flavors=dishDto.getFlavors();
        flavors.stream().map((item)->{
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        boolean b = dishFlavorService.saveBatch(flavors);
        return b;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        List<DishFlavor> flavors=dishDto.getFlavors();
        flavors=flavors.stream().map((item)->{
           item.setDishId(dishDto.getId());
           return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
