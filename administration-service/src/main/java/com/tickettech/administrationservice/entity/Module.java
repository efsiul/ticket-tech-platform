package com.tickettech.administrationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tt_adm_module")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    private Boolean state;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private LocalDateTime creation;
    private LocalDateTime lastUpdate;
}
