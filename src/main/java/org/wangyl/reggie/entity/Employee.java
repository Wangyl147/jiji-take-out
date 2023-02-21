package org.wangyl.reggie.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/*
* 序列化完整地保存了某一状态下的对象信息，是一个整体，而不是零散的
* 序列化将一个对象freeze住，然后进行存储，等到再次需要的时候再de-freeze就可以立即使用
* 用于将对象保存在持久化设备、套接字传输、RMI传输中时
* */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String username;
    private String password;
    private String phone;
    private String sex;
    private String idNumber;//身份证号
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
