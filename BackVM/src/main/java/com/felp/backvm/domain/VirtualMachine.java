package com.felp.backvm.domain;

import com.felp.backvm.domain.enums.OsType;
import com.felp.backvm.domain.enums.VmProfile;
import com.felp.backvm.domain.enums.VmStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "virtual_machines")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VirtualMachine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OsType osType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VmProfile profile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VmStatus status;

    @Column(nullable = false)
    private Integer cpuCores;

    @Column(nullable = false)
    private Integer ramGb;

    @Column(nullable = false)
    private Integer diskGb;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
