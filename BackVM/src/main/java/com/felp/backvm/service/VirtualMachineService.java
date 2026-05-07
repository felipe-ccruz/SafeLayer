package com.felp.backvm.service;

import com.felp.backvm.domain.User;
import com.felp.backvm.domain.VirtualMachine;
import com.felp.backvm.domain.enums.OsType;
import com.felp.backvm.domain.enums.UserPlan;
import com.felp.backvm.domain.enums.VmProfile;
import com.felp.backvm.domain.enums.VmStatus;
import com.felp.backvm.dto.CreateVmRequest;
import com.felp.backvm.dto.VirtualMachineDTO;
import com.felp.backvm.exception.PlanLimitException;
import com.felp.backvm.exception.VmNotFoundException;
import com.felp.backvm.repository.VirtualMachineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VirtualMachineService {

    private static final DateTimeFormatter CREATED_AT_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final VirtualMachineRepository repository;
    private final UserService userService;
    private final DockerService dockerService;

    public List<VirtualMachineDTO> findAllByUser(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    public VirtualMachineDTO create(Long userId, CreateVmRequest request) {
        User user = userService.getOrThrow(userId);
        UserPlan plan = user.getPlan();
        VmProfile profile = request.getProfile();

        if (!plan.allowsProfile(profile)) {
            throw new PlanLimitException(
                    "Seu plano (" + plan.getLabel() + ") não permite o perfil " + profile.getLabel());
        }
        if (!plan.isUnlimited() && repository.countByUserId(userId) >= plan.getMaxVms()) {
            throw new PlanLimitException(
                    "Limite de " + plan.getMaxVms() + " VM(s) atingido para o plano " + plan.getLabel());
        }

        VirtualMachine vm = VirtualMachine.builder()
                .name(request.getName())
                .osType(request.getOsType())
                .profile(profile)
                .status(VmStatus.STOPPED)
                .cpuCores(profile.getCpuCores())
                .ramGb(profile.getRamGb())
                .diskGb(profile.getDiskGb())
                .userId(userId)
                .build();

        VirtualMachine saved = repository.save(vm);

        if (saved.getOsType() == OsType.UBUNTU) {
            dockerService.createContainer(saved.getId());
        }

        return toDto(saved);
    }

    public VirtualMachineDTO start(Long userId, Long id) {
        VirtualMachine vm = findOrThrow(userId, id);
        if (vm.getStatus() == VmStatus.RUNNING) {
            throw new IllegalStateException("VM já está rodando");
        }
        vm.setStatus(VmStatus.RUNNING);
        VirtualMachine saved = repository.save(vm);

        if (saved.getOsType() == OsType.UBUNTU) {
            dockerService.startContainer(saved.getId());
        }

        return toDto(saved);
    }

    public VirtualMachineDTO pause(Long userId, Long id) {
        VirtualMachine vm = findOrThrow(userId, id);
        if (vm.getStatus() != VmStatus.RUNNING) {
            throw new IllegalStateException("VM precisa estar rodando para ser pausada");
        }
        vm.setStatus(VmStatus.PAUSED);
        VirtualMachine saved = repository.save(vm);

        if (saved.getOsType() == OsType.UBUNTU) {
            dockerService.pauseContainer(saved.getId());
        }

        return toDto(saved);
    }

    public VirtualMachineDTO stop(Long userId, Long id) {
        VirtualMachine vm = findOrThrow(userId, id);
        if (vm.getStatus() == VmStatus.STOPPED) {
            throw new IllegalStateException("VM já está parada");
        }
        vm.setStatus(VmStatus.STOPPED);
        VirtualMachine saved = repository.save(vm);

        if (saved.getOsType() == OsType.UBUNTU) {
            dockerService.stopContainer(saved.getId());
        }

        return toDto(saved);
    }

    public void delete(Long userId, Long id) {
        VirtualMachine vm = findOrThrow(userId, id);
        if (vm.getOsType() == OsType.UBUNTU) {
            dockerService.removeContainer(vm.getId());
        }
        repository.delete(vm);
    }

    private VirtualMachine findOrThrow(Long userId, Long id) {
        return repository.findByIdAndUserId(id, userId)
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
