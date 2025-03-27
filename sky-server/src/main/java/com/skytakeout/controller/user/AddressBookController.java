package com.skytakeout.controller.user;

import com.skytakeout.context.BaseContext;
import com.skytakeout.entity.AddressBook;
import com.skytakeout.result.Result;
import com.skytakeout.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/user/addressBook")
@RestController
@Slf4j
@Tag(name = "用户端地址簿接口")
public class AddressBookController {

    @Autowired
    private AddressService addressService;

    @PostMapping
    @Operation(summary = "新增地址")
    public Result save(@RequestBody AddressBook addressBook) {
        log.info("正在保存用户地址, {}", addressBook);
        addressService.save(addressBook);
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "获取全部地址")
    public Result<List<AddressBook>> list() {
        log.info("获取用户全部地址");
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentID());
        List<AddressBook> list = addressService.list(addressBook);
        return Result.success(list);
    }

    @PutMapping
    @Operation(summary = "更新地址")
    public Result upodate(@RequestBody AddressBook addressBook) {
        log.info("更新地址为：{}", addressBook);
        addressService.update(addressBook);
        return Result.success();
    }

    @DeleteMapping
    @Operation(summary = "根据ID删除地址")
    public Result delete(@RequestParam Long id) {
        log.info("删除ID为 {} 的地址", id);
        addressService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询地址")
    public Result<AddressBook> getAddressById(@Param("id") Long id) {
        log.info("查询ID为 {} 的地址", id);
        AddressBook addressBookById = addressService.getAddressBookById(id);
        return Result.success(addressBookById);
    }

    @PutMapping("/default")
    @Operation(summary = "设置默认地址")
    public Result setDefaultAddress(@RequestBody AddressBook addressBook) {
        log.info("设置ID为 {} 的地址为默认", addressBook.getId());
        addressService.setDefaultAddress(addressBook);
        return Result.success();
    }
}
