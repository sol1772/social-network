package com.getjavajob.training.maksyutovs.socialnetwork.service;

import com.getjavajob.training.maksyutovs.socialnetwork.dao.AccountDao;
import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
@Transactional(readOnly = true)
public class AccountValidator implements Validator {

    private static final Logger logger = LoggerFactory.getLogger(AccountValidator.class);
    private AccountDao dao;

    public AccountValidator() {
    }

    @Autowired
    public AccountValidator(AccountDao dao) {
        this.dao = dao;
    }

    public AccountDao getDao() {
        return dao;
    }

    public void setDao(AccountDao dao) {
        this.dao = dao;
    }

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return Account.class.equals(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        Account account = (Account) target;
        if (dao.selectByEmail(account.getEmail()) != null) {
            String str = "Account with email '" + account.getEmail() + "'already exists";
            errors.rejectValue("email", "", str);
            logger.warn(str);
        }
    }

}
