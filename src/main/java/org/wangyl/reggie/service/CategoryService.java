package org.wangyl.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.wangyl.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
