package com.felp.backvm.repository;

import com.felp.backvm.domain.VirtualMachine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VirtualMachineRepository extends JpaRepository<VirtualMachine, Long> {

    List<VirtualMachine> findByUserId(Long userId);

    long countByUserId(Long userId);

    Optional<VirtualMachine> findByIdAndUserId(Long id, Long userId);
}
