package com.chemist.system.repository.central;

import com.chemist.system.models.ERole;
import com.chemist.system.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository  extends JpaRepository<Role, Long> {
    Role findByName(ERole name);
    Optional<Role> findById(Long id);

}
