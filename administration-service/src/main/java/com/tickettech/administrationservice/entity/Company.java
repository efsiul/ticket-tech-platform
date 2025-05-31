package com.tickettech.administrationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tt_adm_company")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String nit;

    private String address;
    private String contactPerson;
    private String phone;
    private String mail;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String image;
    private Boolean state;
    private LocalDateTime creation;
    private LocalDateTime lastUpdate;

    //@OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //private List<Subsidiary> subsidiaries;
}
