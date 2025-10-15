package com.moktob.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    List<UserAccount> findByClientId(Long clientId);
    Optional<UserAccount> findByUsername(String username);
    Optional<UserAccount> findByClientIdAndUsername(Long clientId, String username);
    List<UserAccount> findByClientIdAndIsActiveTrue(Long clientId);
}
