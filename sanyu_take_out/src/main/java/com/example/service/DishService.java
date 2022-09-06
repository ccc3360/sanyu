package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dto.DishDto;
import com.example.entity.Dish;

public interface DishService extends IService<Dish> {

    /**
     * 新增菜品，同时插入菜品对应的口味
     */
    public boolean saveWithFlavor(DishDto dishDto);

    public void updateWithFlavor(DishDto dishDto);
}
