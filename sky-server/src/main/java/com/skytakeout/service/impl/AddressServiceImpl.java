package com.skytakeout.service.impl;

import com.skytakeout.context.BaseContext;
import com.skytakeout.entity.AddressBook;
import com.skytakeout.repository.AddressBookRepository;
import com.skytakeout.service.AddressService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressBookRepository addressBookRepository;

    @Override
    public void save(AddressBook addressBook) {
        addressBookRepository.save(addressBook);
    }

    @Override
    public List<AddressBook> list(AddressBook addressBook) {
        return addressBookRepository.findByUserId(addressBook.getUserId());
    }

    @Override
    public AddressBook defaultAddress() {
        return null;
    }

    @Override
    public void update(AddressBook addressBook) {
        AddressBook toEdit = addressBookRepository.getReferenceById(addressBook.getId());
        BeanUtils.copyProperties(addressBook, toEdit);
        addressBookRepository.save(toEdit);
    }

    @Override
    public AddressBook getAddressBookById(Long id) {
        return addressBookRepository.getReferenceById(id);
    }

    @Override
    public void deleteById(Long id) {
        addressBookRepository.deleteById(id);
    }

    @Override
    public void setDefaultAddress(AddressBook addressBook) {
        //将用户ID下所有的地址设为非默认
        Long userId = BaseContext.getCurrentID();
        addressBookRepository.setAllAddressToUnused(userId);
        //将本地址设为默认
    }
}
