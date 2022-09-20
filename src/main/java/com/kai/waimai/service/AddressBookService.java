package com.kai.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kai.waimai.entity.AddressBook;

public interface AddressBookService extends IService<AddressBook> {
    void setDefault(AddressBook addressBook);
}
