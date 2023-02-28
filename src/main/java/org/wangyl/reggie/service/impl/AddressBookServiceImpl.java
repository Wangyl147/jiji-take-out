package org.wangyl.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wangyl.reggie.dao.AddressBookMapper;
import org.wangyl.reggie.entity.AddressBook;
import org.wangyl.reggie.service.AddressBookService;


@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
