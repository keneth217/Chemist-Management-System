package com.chemist.system.tenant.repository;

import com.chemist.system.tenant.model.Chemist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChemistRepository extends JpaRepository<Chemist, Long> {
    @Query("SELECT c FROM Chemist c WHERE c.chemistId = :chemistId")
    List<Chemist> findAllByChemistId(@Param("chemistId") Long chemistId);

    Optional<Chemist> findByChemistId(Long chemistId);
}
