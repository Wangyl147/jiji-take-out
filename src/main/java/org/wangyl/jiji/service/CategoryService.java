package org.wangyl.jiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.wangyl.jiji.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
