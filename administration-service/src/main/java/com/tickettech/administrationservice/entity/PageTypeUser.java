package com.tickettech.administrationservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "hq_adm_page_type_user")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PageTypeUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*******************************************************************************************
     * Cambiamos de “ManyToOne a Pages” a “ManyToMany con Pages” porque
     * cada registro de PageTypeUser puede apuntar a N páginas.
     ******************************************************************************************/
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "hq_adm_page_type_user_pages",             // nueva tabla intermedia
            joinColumns = @JoinColumn(name = "page_type_user_id"),
            inverseJoinColumns = @JoinColumn(name = "pages_id")
    )
    private Set<Pages> pages = new HashSet<>();

    /** Transient para leer en JSON la lista de IDs de páginas **/
    @Transient
    private List<Long> pagesIds;

    /*******************************************************************************************
     * La relación con el “tipo de usuario” NO cambia: es ManyToOne, porque
     * cada registro de PageTypeUser está ligado a un solo TypeUser.
     ******************************************************************************************/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_user_id", nullable = false)
    private TypeUser typeUser;

    @Transient
    private Long idTypeUser;

    /*******************************************************************************************
     * La relación con “User”: muchos PageTypeUser ↔ un User.
     ******************************************************************************************/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)  // puede dejarse nullable
    private User user;

    @Transient
    private Long idUser;

    private Boolean isCreate;
    private Boolean isUpdate;
    private Boolean isActive;
    private LocalDateTime created;
    private LocalDateTime lastUpdate;
}