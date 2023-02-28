package org.wangyl.reggie.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.wangyl.reggie.entity.AddressBook;
import org.wangyl.reggie.entity.Employee;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
