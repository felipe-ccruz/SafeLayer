package com.felp.backvm.controller;

import com.felp.backvm.dto.CreateVmRequest;
import com.felp.backvm.dto.VirtualMachineDTO;
import com.felp.backvm.service.VirtualMachineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vms")
@RequiredArgsConstructor
public class VirtualMachineController {

    private final VirtualMachineService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<VirtualMachineDTO> findAll() {
        return service.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VirtualMachineDTO create(@RequestBody @Valid CreateVmRequest request) {
        return service.create(request);
    }

    @PatchMapping("/{id}/start")
    @ResponseStatus(HttpStatus.OK)
    public VirtualMachineDTO start(@PathVariable Long id) {
        return service.start(id);
    }

    @PatchMapping("/{id}/pause")
    @ResponseStatus(HttpStatus.OK)
    public VirtualMachineDTO pause(@PathVariable Long id) {
        return service.pause(id);
    }

    @PatchMapping("/{id}/stop")
    @ResponseStatus(HttpStatus.OK)
    public VirtualMachineDTO stop(@PathVariable Long id) {
        return service.stop(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
