package com.chemist.system.master.repository;

import com.chemist.system.master.model.MasterUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MasterUserRepository extends JpaRepository<MasterUser, Long> {


    Optional<MasterUser> findByPhoneNo(String username);

    Optional<MasterUser>  findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByPhoneNo(String phoneNo);
}
