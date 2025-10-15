package com.moktob.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByClientId(Long clientId);
    Optional<Role> findByClientIdAndRoleName(Long clientId, String roleName);
}
