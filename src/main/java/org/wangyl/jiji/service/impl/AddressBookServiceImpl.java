package org.wangyl.jiji.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wangyl.jiji.common.BaseContext;
import org.wangyl.jiji.dao.AddressBookMapper;
import org.wangyl.jiji.entity.AddressBook;
import org.wangyl.jiji.service.AddressBookService;

@Slf4j
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    //设置默认地址
    @Override
    @Transactional
    public void setDefault(AddressBook addressBook) {
        //先把该用户的所有记录都设为不默认
        //update address_book set is_default=0 where user_id=?
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        updateWrapper.set(AddressBook::getIsDefault,0);

        this.update(updateWrapper);
        //再把对应id设为默认
        //update address_book set is_default=1 where address_book.id=id
        addressBook.setIsDefault(1);
        this.updateById(addressBook);
    }
}
