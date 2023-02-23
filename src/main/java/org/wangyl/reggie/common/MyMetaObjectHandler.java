package org.wangyl.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//自定义元数据对象处理器
//不光是添加user，后面添加菜品都可以走这里
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 这里不能获得Session，也就不能从Session获得当前用户id
     * 可用ThreadLocal获取：客户端发送的每次http请求，服务端都会对应地分配一个新线程来处理
     * 例如，LoginCheckFilter.doFilter方法、EmployeeController.update方法和MyMetaObjectHandler.updateFill方法是在一条线程上的
     * ThreadLocal不是线程，而是线程的局部变量。使用ThreadLocal维护变量时，使用这个变量的所有线程都有一个独立的副本，每个线程只能独立地改变自
     * 己的副本，而不能改变其他线程的副本。
     * ThreadLocal为每个线程单独提供一份存储空间，具有线程隔离的效果，只有在线程内才能获取对应的值，线程外则不能访问。
     */

    //插入时自动填充
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充【insert】");
        log.info(metaObject.toString());

        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());

        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    //更新时自动填充
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充【update】");
        log.info(metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());

        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
