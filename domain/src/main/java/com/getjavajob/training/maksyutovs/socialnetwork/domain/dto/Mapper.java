package com.getjavajob.training.maksyutovs.socialnetwork.domain.dto;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public Account toNewAccount(AccountDto dto) {
        Account account = modelMapper.map(dto, Account.class);
        if (!StringUtils.isEmpty(dto.getPassword())) {
            account.setPasswordHash(account.hashPassword(dto.getPassword()));
        }
        if (!StringUtils.isEmpty(dto.getPersonalPhone())) {
            account.getPhones().add(new Phone(account, 0, dto.getPersonalPhone(), PhoneType.PERSONAL));
        }
        if (!StringUtils.isEmpty(dto.getWorkPhone())) {
            account.getPhones().add(new Phone(account, 0, dto.getWorkPhone(), PhoneType.WORK));
        }
        if (!StringUtils.isEmpty(dto.getHomeAddress())) {
            account.getAddresses().add(new Address(account, 0, dto.getHomeAddress(), AddressType.HOME));
        }
        if (!StringUtils.isEmpty(dto.getWorkAddress())) {
            account.getAddresses().add(new Address(account, 0, dto.getWorkAddress(), AddressType.WORK));
        }
        return account;
    }

    public Account toAccount(Account account, AccountDto dto) {
        account.setFirstName(dto.getFirstName());
        account.setLastName(dto.getLastName());
        account.setMiddleName(dto.getMiddleName());
        account.setUserName(dto.getUserName());
        account.setDateOfBirth(dto.getDateOfBirth());
        account.setGender(dto.getGender());
        account.setAddInfo(dto.getAddInfo());
        if (dto.getPassword() != null) {
            account.setPasswordHash(account.hashPassword(dto.getPassword()));
        }
        account.getPhones().clear();
        for (Phone phone : dto.getPhones()) {
            if (!StringUtils.isEmpty(phone.getNumber()) && phone.getPhoneType() != null) {
                account.getPhones().add(new Phone(account, phone.getId(), phone.getNumber(), phone.getPhoneType()));
            }
        }
        account.getAddresses().clear();
        for (Address addr : dto.getAddresses()) {
            if (!StringUtils.isEmpty(addr.getAddr()) && addr.getAddrType() != null) {
                account.getAddresses().add(new Address(account, addr.getId(), addr.getAddr(), addr.getAddrType()));
            }
        }
        return account;
    }

    public AccountDto toAccountDto(Account account) {
        return modelMapper.map(account, AccountDto.class);
    }

    public AccountDto toAccountDto(Object[] arr) {
        return new AccountDto((int) arr[0], (String) arr[1], (String) arr[2], (String) arr[3], (String) arr[4]);
    }

    public Group toNewGroup(GroupDto dto) {
        return new Group(dto.getId(), dto.getTitle(), dto.getMetaTitle());
    }

    public Group toGroup(Group group, GroupDto dto) {
        group.setMetaTitle(dto.getMetaTitle());
        return group;
    }

    public Group toGroup(Object[] arr) {
        return new Group((int) arr[0], (String) arr[1], (String) arr[2]);
    }

}
