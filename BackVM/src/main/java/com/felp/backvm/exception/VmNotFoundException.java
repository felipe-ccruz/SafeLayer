package com.felp.backvm.exception;

public class VmNotFoundException extends RuntimeException {

    public VmNotFoundException(Long id) {
        super("VM não encontrada com id: " + id);
    }
}
