package org.wangyl.jiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.wangyl.jiji.entity.AddressBook;

public interface AddressBookService extends IService<AddressBook> {
    public void setDefault(AddressBook addressBook);
}
