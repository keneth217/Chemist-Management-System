package com.chemist.system.tenant.repository;

import com.chemist.system.tenant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.nio.channels.FileChannel;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNo(String username);


   Optional<User> findByUsernameAndChemistId(String username, Long chemistId);
}
