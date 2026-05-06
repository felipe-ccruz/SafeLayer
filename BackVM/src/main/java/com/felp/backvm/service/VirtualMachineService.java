package com.felp.backvm.service;

import com.felp.backvm.domain.VirtualMachine;
import com.felp.backvm.domain.enums.OsType;
import com.felp.backvm.domain.enums.VmProfile;
import com.felp.backvm.domain.enums.VmStatus;
import com.felp.backvm.dto.CreateVmRequest;
import com.felp.backvm.dto.VirtualMachineDTO;
import com.felp.backvm.exception.VmNotFoundException;
import com.felp.backvm.repository.VirtualMachineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VirtualMachineService {

    private static final DateTimeFormatter CREATED_AT_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final VirtualMachineRepository repository;

    public List<VirtualMachineDTO> findAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public VirtualMachineDTO create(CreateVmRequest request) {
        VmProfile profile = request.getProfile();

        VirtualMachine vm = VirtualMachine.builder()
                .name(request.getName())
                .osType(request.getOsType())
                .profile(profile)
                .status(VmStatus.STOPPED)
                .cpuCores(profile.getCpuCores())
                .ramGb(profile.getRamGb())
                .diskGb(profile.getDiskGb())
                .createdAt(LocalDateTime.now())
                .build();

        return toDto(repository.save(vm));
    }

    public VirtualMachineDTO start(Long id) {
        VirtualMachine vm = findOrThrow(id);
        if (vm.getStatus() == VmStatus.RUNNING) {
            throw new IllegalStateException("VM já está rodando");
        }
        vm.setStatus(VmStatus.RUNNING);
        return toDto(repository.save(vm));
    }

    public VirtualMachineDTO pause(Long id) {
        VirtualMachine vm = findOrThrow(id);
        if (vm.getStatus() != VmStatus.RUNNING) {
            throw new IllegalStateException("VM precisa estar rodando para ser pausada");
        }
        vm.setStatus(VmStatus.PAUSED);
        return toDto(repository.save(vm));
    }

    public VirtualMachineDTO stop(Long id) {
        VirtualMachine vm = findOrThrow(id);
        if (vm.getStatus() == VmStatus.STOPPED) {
            throw new IllegalStateException("VM já está parada");
        }
        vm.setStatus(VmStatus.STOPPED);
        return toDto(repository.save(vm));
    }

    public void delete(Long id) {
        VirtualMachine vm = findOrThrow(id);
        repository.delete(vm);
    }

    private VirtualMachine findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new VmNotFoundException(id));
    }

    private VirtualMachineDTO toDto(VirtualMachine vm) {
        return VirtualMachineDTO.builder()
                .id(vm.getId())
                .name(vm.getName())
                .osType(vm.getOsType().name())
                .osLabel(vm.getOsType() == OsType.UBUNTU ? "Ubuntu" : "Windows 11")
                .profile(vm.getProfile().name())
                .profileLabel(vm.getProfile().getLabel())
                .status(vm.getStatus().name())
                .statusLabel(vm.getStatus().getLabel())
                .cpuCores(vm.getCpuCores())
                .ramGb(vm.getRamGb())
                .diskGb(vm.getDiskGb())
                .createdAt(vm.getCreatedAt().format(CREATED_AT_FORMAT))
                .build();
    }
}
