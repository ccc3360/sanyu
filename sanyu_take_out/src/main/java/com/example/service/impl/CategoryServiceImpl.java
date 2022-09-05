package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.CustomException;
import com.example.entity.Category;
import com.example.entity.Dish;
import com.example.entity.Setmeal;
import com.example.mapper.CategoryMapper;
import com.example.service.CategoryService;
import com.example.service.DishService;
import com.example.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 删除分类前判断
     * @param id
     */
    @Override
    public void remove(Long id) {
//        查询当前分类是否关联菜品或套餐
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(queryWrapper);
        LambdaQueryWrapper<Setmeal> queryWrapper2=new LambdaQueryWrapper<>();
        queryWrapper2.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(queryWrapper2);
        if(count>0){
//            已关联菜品，抛出一个业务异常
            throw new CustomException("当前分类关联了菜品，不能删除");
        }
        if(count2>0){
//            已关联套餐，抛出一个业务异常
            throw new CustomException("当前分类关联了套餐，不能删除");

        }
//        正常删除分类
        super.removeById(id);
    }
}
