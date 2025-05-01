package com.chemist.system.repository.tenant;

import com.chemist.system.models.ERole;
import com.chemist.system.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    void findByRoleName(String roleName);

    Optional<Role> findByName(ERole eRole);
}
