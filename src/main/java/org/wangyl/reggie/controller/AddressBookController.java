package org.wangyl.reggie.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wangyl.reggie.common.R;
import org.wangyl.reggie.service.AddressBookService;

//地址簿管理
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    //新增地址
    @PostMapping
    public R<String> save(){
        return R.success("添加地址成功");
    }

    //分页查询地址


    //回显和修改地址


    //删除地址

}
