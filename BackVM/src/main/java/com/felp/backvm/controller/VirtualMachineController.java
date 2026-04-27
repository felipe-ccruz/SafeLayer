package com.felp.backvm.controller;

import com.felp.backvm.dto.CreateVmRequest;
import com.felp.backvm.dto.VirtualMachineDTO;
import com.felp.backvm.service.VirtualMachineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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
}
