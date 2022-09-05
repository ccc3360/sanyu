package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.R;
import com.example.entity.Category;
import com.example.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName,category.getName());
        int count = categoryService.count(queryWrapper);
        if(count!=0){
            return R.error("分类已存在");
        }
        categoryService.save(category);
        return R.success("添加成功");
    }

    /**
     * 分页查询分类
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize){
        Page<Category> pageInfo=new Page<>();
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteById(Long ids){
        categoryService.remove(ids);
        return R.success("删除成功");
    }

    /**
     * 修改分类
     * @param category
     * @return
     */
    @PutMapping
    public R<String>  update(@RequestBody Category category){
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName,category.getName());
        queryWrapper.ne(Category::getId,category.getId());
        int count = categoryService.count(queryWrapper);
        if(count!=0){
            return R.error("分类已存在");
        }
        categoryService.updateById(category);
        return R.success("修改成功");
    }

//    @GetMapping("/{name}")
//    public R<String> checkName(@PathVariable String name){
//        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.eq(Category::getName,name);
//        int count = categoryService.count(queryWrapper);
//        if(count!=0){
//            return R.error("分类已存在");
//        }
//        return R.success(null);
//    }
}
