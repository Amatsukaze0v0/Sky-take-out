package com.skytakeout.repository;

import com.skytakeout.entity.AddressBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressBookRepository extends JpaRepository<AddressBook, Long> {
    // 根据用户ID查询地址列表
    List<AddressBook> findByUserId(Long userId);

    // 查询用户的默认地址
    AddressBook findByUserIdAndIsDefault(Long userId, Integer isDefault);
}