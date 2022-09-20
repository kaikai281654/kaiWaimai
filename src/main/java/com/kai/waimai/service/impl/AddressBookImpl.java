package com.kai.waimai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kai.waimai.common.BaseContext;
import com.kai.waimai.entity.AddressBook;
import com.kai.waimai.mapper.AddressBookMapper;
import com.kai.waimai.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService{
    @Override
    public void setDefault(AddressBook addressBook) {
        // 首相把所有地址的 isDefault设置为 0
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId,userId);
        AddressBook ad = new AddressBook();
        ad.setIsDefault(0);
        update(ad, wrapper);
        // 把当前id地址设置为1
        addressBook.setIsDefault(1);
        updateById(addressBook);
    }

}
