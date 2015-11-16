package io.pivotal.accounts.repository;

import org.springframework.data.repository.CrudRepository;

import io.pivotal.accounts.domain.Account;

public interface AccountRepository extends CrudRepository<Account,Integer> {
	Account findByUseridAndPasswd(String userId, String passwd);
	Account findByUserid(String userId);
}
