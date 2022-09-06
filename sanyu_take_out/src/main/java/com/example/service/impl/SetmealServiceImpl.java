package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dto.DishDto;
import com.example.dto.SetmealDto;
import com.example.entity.DishFlavor;
import com.example.entity.Setmeal;
import com.example.entity.SetmealDish;
import com.example.mapper.SetmealMapper;
import com.example.service.SetmealDishService;
import com.example.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService{
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((iteam)->{
            iteam.setSetmealId(setmealDto.getId());
            return iteam;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }
//    @Override
//    public void updateWithFlavor(DishDto dishDto) {
//        this.updateById(dishDto);
//
//        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
//        dishFlavorService.remove(queryWrapper);
//
//        List<DishFlavor> flavors=dishDto.getFlavors();
//        flavors=flavors.stream().map((item)->{
//            item.setDishId(dishDto.getId());
//            return item;
//        }).collect(Collectors.toList());
//        dishFlavorService.saveBatch(flavors);
//    }
    @Override
    public void eidiWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        List<SetmealDish> dishes=setmealDto.getSetmealDishes();
        dishes=dishes.stream().map((i)->{
           i.setSetmealId(setmealDto.getId());
           return i;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(dishes);
    }
}
