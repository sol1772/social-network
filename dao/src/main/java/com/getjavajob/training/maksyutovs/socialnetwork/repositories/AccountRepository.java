package com.getjavajob.training.maksyutovs.socialnetwork.repositories;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.Account;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    Logger logger = LoggerFactory.getLogger(AccountRepository.class);

    Account findByEmail(String email);

    @EntityGraph(attributePaths = {"phones"})
    Account findAccountById(@Param("id") Integer id);

    default Account findAccountByIdEagerly(@Param("id") Integer id) {
        Account account = findAccountById(id);
        if (account != null) {
            Hibernate.initialize(account.getAddresses());
            Hibernate.initialize(account.getMessengers());
        }
        return account;
    }

    default Account findAccountByEmailEagerly(String email) {
        Account account = findByEmail(email);
        if (account != null) {
            Hibernate.initialize(account.getPhones());
            Hibernate.initialize(account.getAddresses());
            Hibernate.initialize(account.getMessengers());
        }
        return account;
    }

    Page<Account> findByFirstNameLikeOrLastNameLike(String firstName, String lastName, Pageable pageable);

}
