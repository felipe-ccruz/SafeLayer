package com.felp.backvm.repository;

import com.felp.backvm.domain.VirtualMachine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VirtualMachineRepository extends JpaRepository<VirtualMachine, Long> {
}
