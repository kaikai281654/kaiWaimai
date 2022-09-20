package com.kai.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kai.waimai.common.BaseContext;
import com.kai.waimai.entity.AddressBook;
import com.kai.waimai.entity.R;
import com.kai.waimai.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {
    @Resource
    private AddressBookService addressBookService;

    /**
     * 添加地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R add(@RequestBody AddressBook addressBook){
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return R.success("添加地址成功！");
    }

    /**
     * 查询当前用户所有地址
     * @return
     */
    @GetMapping("/list")
    public R getList(){
        Long userId = BaseContext.getCurrentId();
        List<AddressBook> list = addressBookService.list(new LambdaQueryWrapper<AddressBook>()
                .orderByDesc(AddressBook::getIsDefault)
                .eq(AddressBook::getUserId,userId));
        return R.success(list);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R changeDefault(@RequestBody AddressBook addressBook){
        addressBookService.setDefault(addressBook);
        return R.success("默认地址设置成功!");
    }

    /**
     * 根据id获取地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R getAddress(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }

    /**
     * 获取默认地址
     * @return
     */
    @GetMapping("/default")
    public R getDefault(){

        System.out.println("git删除测试");

        Long userId = BaseContext.getCurrentId();
        AddressBook one = addressBookService.getOne(
                new LambdaQueryWrapper<AddressBook>()
                        .eq(AddressBook::getIsDefault, true)
                        .eq(AddressBook::getUserId,userId));
        return R.success(one);

    }


}
