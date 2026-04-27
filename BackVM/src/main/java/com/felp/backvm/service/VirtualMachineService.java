package com.felp.backvm.service;

import com.felp.backvm.domain.VirtualMachine;
import com.felp.backvm.domain.enums.OsType;
import com.felp.backvm.domain.enums.VmProfile;
import com.felp.backvm.domain.enums.VmStatus;
import com.felp.backvm.dto.CreateVmRequest;
import com.felp.backvm.dto.VirtualMachineDTO;
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

    private VirtualMachineDTO toDto(VirtualMachine vm) {
        return VirtualMachineDTO.builder()
                .id(vm.getId())
                .name(vm.getName())
                .osType(vm.getOsType().name())
                .osLabel(vm.getOsType() == OsType.WINDOWS_10 ? "Windows 10" : "Windows 11")
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
