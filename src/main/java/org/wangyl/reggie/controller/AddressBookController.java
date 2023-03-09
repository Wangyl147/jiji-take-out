package org.wangyl.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.wangyl.reggie.common.BaseContext;
import org.wangyl.reggie.common.R;
import org.wangyl.reggie.entity.AddressBook;
import org.wangyl.reggie.service.AddressBookService;

import java.util.List;

//地址簿管理
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    //新增地址
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook){
        //get到过滤器set的用户id
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success("添加地址成功");
    }

    //查询这个用户的地址列表
    @GetMapping("/list")
    public R<List<AddressBook>> list(){
        //select * from address_book where user_id=?
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseContext.getCurrentId()!=null,AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByDesc(AddressBook::getCreateTime);
        List<AddressBook> addressBooks = addressBookService.list(queryWrapper);
        return R.success(addressBooks);
    }

    //设为默认地址
    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook){
        addressBookService.setDefault(addressBook);
        return R.success("");
    }

    //获取默认地址
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        //select * from address_book where user_id=? and is_default=1
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        return R.success(addressBook);
    }

    //回显和修改地址
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }
    }

    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success("");
    }

    //删除地址
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        addressBookService.removeByIds(ids);
        return R.success("");
    }

}
