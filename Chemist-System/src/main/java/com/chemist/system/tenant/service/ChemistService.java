package com.chemist.system.tenant.service;

import com.chemist.system.tenant.dto.ChemistProfileDTO;
import com.chemist.system.tenant.model.Chemist;
import com.chemist.system.tenant.repository.ChemistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChemistService {

    private final ChemistRepository chemistRepository;

    public Chemist saveProfile(ChemistProfileDTO profileDTO) {
        Chemist chemist = new Chemist();
        chemist.setChemistId(profileDTO.getChemistId());
        chemist.setChemistName(profileDTO.getChemistName());
        chemist.setChemistCode(profileDTO.getChemistCode());
        chemist.setPhone(profileDTO.getPhone());
        chemist.setLocation(profileDTO.getLocation());
        chemist.setAddress(profileDTO.getAddress());
        chemist.setDbUrl(profileDTO.getDbUrl());
        chemist.setEmail(profileDTO.getEmail());
        chemist.setChemistOwnerName(profileDTO.getChemistOwnerName());
        return chemistRepository.save(chemist);
    }

    @Transactional(readOnly = true)
    public List<Chemist> getAllChemists() {
        return chemistRepository.findAll();
    }

    public Chemist updateChemist(Chemist chemist) {
        Chemist existingChemist = chemistRepository.findById(chemist.getChemistId())
                .orElseThrow(() -> new RuntimeException("Chemist not found with id: " + chemist.getChemistId()));

        existingChemist.setChemistName(chemist.getChemistName());
        existingChemist.setChemistCode(chemist.getChemistCode());
        existingChemist.setPhone(chemist.getPhone());
        existingChemist.setLocation(chemist.getLocation());
        existingChemist.setAddress(chemist.getAddress());
        existingChemist.setDbUrl(chemist.getDbUrl());
        existingChemist.setEmail(chemist.getEmail());
        existingChemist.setChemistOwnerName(chemist.getChemistOwnerName());

        return chemistRepository.save(existingChemist);
    }

    public void deleteChemist(Long id) {
        if (!chemistRepository.existsById(id)) {
            throw new RuntimeException("Chemist not found with id: " + id);
        }
        chemistRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Chemist findByChemistId(String chemistId) {
        if (chemistId == null || chemistId.isEmpty()) {
            throw new IllegalArgumentException("Chemist ID cannot be null or empty");
        }
        return chemistRepository.findById(Long.valueOf(chemistId))
                .orElseThrow(() -> new RuntimeException("Chemist not found with id: " + chemistId));
    }
}