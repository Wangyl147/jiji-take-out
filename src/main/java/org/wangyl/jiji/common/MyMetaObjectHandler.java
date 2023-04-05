package org.wangyl.jiji.common;

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
     * 例如，更新employee时，过滤器、更新回调方法和这里的自动填充方法是在一条线程上的
     * 那么，过滤器set的员工id，就可以在这里get到
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
