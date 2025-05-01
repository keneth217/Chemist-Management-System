package com.chemist.system.repository.central;

import com.chemist.system.models.MasterUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MasterRepository  extends JpaRepository<MasterUser, Long> {
    Optional<MasterUser> findByPhoneNo(String phoneNo);
}
