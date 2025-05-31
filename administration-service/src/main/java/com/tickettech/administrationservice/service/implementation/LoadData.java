package com.tickettech.administrationservice.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tickettech.administrationservice.entity.*;
import com.tickettech.administrationservice.repository.*;
import com.tickettech.administrationservice.security.ConnectInternalApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class LoadData {

    private final TypeUserRepository typeUserRepository;
    private final UserRepository usersRepository;
    private final CompanyRepository companyRepository;
    private final PagesRepository pagesRepository;
    private final PageTypeUserRepository pageTypeUserRepository;
    private final ObjectMapper objectMapper;
    private ConnectInternalApi connectInternalApi;

    @Autowired
    public LoadData(ConnectInternalApi connectInternalApi,
                    UserRepository usersRepository,
                    TypeUserRepository typeUserRepository,
                    PagesRepository pagesRepository,
                    PageTypeUserRepository pageTypeUserRepository,
                    CompanyRepository companyRepository,
                    ObjectMapper objectMapper) {
        if (connectInternalApi == null) {
            this.connectInternalApi = new ConnectInternalApi();
        } else {
            this.connectInternalApi = connectInternalApi;
        }
        this.usersRepository = usersRepository;
        this.typeUserRepository = typeUserRepository;
        this.companyRepository = companyRepository;
        this.pagesRepository = pagesRepository;
        this.pageTypeUserRepository = pageTypeUserRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Método público que invoca la carga de datos en todas las tablas.
     * Si alguna tabla ya contiene filas, no reinserta para evitar duplicados.
     */
    public void seedData() {
        try {
            loadAndSaveEntities("dataSeeder/companyData.json", Company.class, companyRepository);
            loadAndSaveEntities("dataSeeder/typeUsersData.json", TypeUser.class, typeUserRepository);
            loadAndSaveEntities("dataSeeder/usersData.json", User.class, usersRepository);
            loadAndSaveEntities("dataSeeder/pagesData.json", Pages.class, pagesRepository);
            loadAndSaveEntities("dataSeeder/pageTypeUserData.json", PageTypeUser.class, pageTypeUserRepository);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void loadAndSaveEntities(String fileName, Class<T> entityClass, Object repository) throws IOException {
        // 1) Abrimos el JSON desde resources/dataSeeder/…
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IOException("File not found: " + fileName);
        }

        // 2) Leemos la lista de entidades genéricas (T) a partir del JSON
        List<T> entities = objectMapper.readValue(
                inputStream,
                objectMapper.getTypeFactory().constructCollectionType(List.class, entityClass)
        );

        // ================================
        //  Bloque para entidad Company
        // ================================
        if (repository instanceof CompanyRepository && entityClass.equals(Company.class)) {
            CompanyRepository repo = (CompanyRepository) repository;
            if (repo.findAll().isEmpty()) {
                repo.saveAll((List<Company>) entities);
            }
        }
        // ================================
        //  Bloque para entidad TypeUser
        // ================================
        else if (repository instanceof TypeUserRepository && entityClass.equals(TypeUser.class)) {
            TypeUserRepository repo = (TypeUserRepository) repository;
            if (repo.findAll().isEmpty()) {
                repo.saveAll((List<TypeUser>) entities);
            }
        }
        // ================================
        //  Bloque para entidad User
        // ================================
        else if (repository instanceof UserRepository && entityClass.equals(User.class)) {
            UserRepository repo = (UserRepository) repository;
            if (repo.count() == 0) {
                List<User> users = (List<User>) entities;
                for (User user : users) {
                    // Si viene solo idCompany, lo vinculamos
                    if (user.getCompany() == null && user.getIdCompany() != null) {
                        companyRepository.findById(user.getIdCompany()).ifPresent(user::setCompany);
                    }
                    // Si viene solo idTypeUser, lo vinculamos
                    if (user.getTypeUser() == null && user.getIdTypeUser() != null) {
                        typeUserRepository.findById(user.getIdTypeUser()).ifPresent(user::setTypeUser);
                    }
                    // No tocamos el campo user.user (autorreferencia) en este seed
                }
                repo.saveAll(users);
            }
        }
        // ================================
        //  Bloque para entidad Pages
        // ================================
        else if (repository instanceof PagesRepository && entityClass.equals(Pages.class)) {
            PagesRepository repo = (PagesRepository) repository;
            if (repo.count() == 0) {
                System.out.println("✅ Insertando datos en tt_adm_pages...");
                repo.saveAll((List<Pages>) entities);
                System.out.println("✅ Datos insertados correctamente en tt_adm_pages.");
            } else {
                System.out.println("⚠️ La tabla tt_adm_pages ya tiene datos, no se insertarán nuevamente.");
            }
        }
        // ===========================================================
        //  Bloque específico para entidad PageTypeUser (ManyToMany)
        // ===========================================================
        else if (repository instanceof PageTypeUserRepository && entityClass.equals(PageTypeUser.class)) {
            PageTypeUserRepository repo = (PageTypeUserRepository) repository;
            List<PageTypeUser> pageTypeUsers = (List<PageTypeUser>) entities;

            for (PageTypeUser pageTypeUser : pageTypeUsers) {
                // 1) Vincular el Set<Pages> a partir de pagesIds
                Set<Pages> pagesSet = new HashSet<>();
                if (pageTypeUser.getPagesIds() != null) {
                    for (Long pageId : pageTypeUser.getPagesIds()) {
                        pagesRepository.findById(pageId).ifPresent(pagesSet::add);
                    }
                }
                pageTypeUser.setPages(pagesSet);

                // 2) Vincular el TypeUser (ManyToOne) si viene solo idTypeUser
                if (pageTypeUser.getTypeUser() == null && pageTypeUser.getIdTypeUser() != null) {
                    typeUserRepository.findById(pageTypeUser.getIdTypeUser())
                            .ifPresent(pageTypeUser::setTypeUser);
                }

                // 3) Vincular el User (ManyToOne) si viene solo idUser
                if (pageTypeUser.getUser() == null && pageTypeUser.getIdUser() != null) {
                    usersRepository.findById(pageTypeUser.getIdUser())
                            .ifPresent(pageTypeUser::setUser);
                }

                // 4) Guardar la entidad corregida
                repo.save(pageTypeUser);
            }
        }
        // ===========================================================
        //  Cualquier otro caso (no contemplado)
        // ===========================================================
        else {
            // No hacemos nada si la entidad/repository no coincide con ninguno de los casos anteriores.
        }
    }
}
