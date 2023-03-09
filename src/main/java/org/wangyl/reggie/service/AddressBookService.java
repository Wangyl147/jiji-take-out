package org.wangyl.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.wangyl.reggie.entity.AddressBook;

public interface AddressBookService extends IService<AddressBook> {
    public void setDefault(AddressBook addressBook);
}
