package com.tickettech.administrationservice.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tickettech.administrationservice.dto.input.*;
import com.tickettech.administrationservice.dto.output.ResultPageTypeUserDTO;
import com.tickettech.administrationservice.dto.output.ResultPagesDTO;
import com.tickettech.administrationservice.entity.*;
import com.tickettech.administrationservice.dto.output.ResultDTO;
import com.tickettech.administrationservice.enums.Message;
import com.tickettech.administrationservice.security.ConnectInternalApi;
import com.tickettech.administrationservice.service.PageTypeUserService;
import com.tickettech.administrationservice.util.ChangeLogUtil;
import com.tickettech.administrationservice.util.EntityChangeEvent;
import com.tickettech.administrationservice.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class PageTypeUserServiceImpl implements PageTypeUserService {

    private final ConnectInternalApi connectInternalApi;
    private final PageTypeUserRepository repository;
    private final PagesRepository pagesRepository;
    private final TypeUserRepository typeUserRepository;
    private final UserRepository userRepository;
    private final CustomPageTypeUserRepository customPageTypeUserRepository;
    private final ModelMapper modelMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ChangeLogUtil<PageTypeUser> changeLogUtil;

    private final String dataNotFound = "S/D";

    @Autowired
    public PageTypeUserServiceImpl(PageTypeUserRepository repository,
                                   ConnectInternalApi connectInternalApi,
                                   PagesRepository pagesRepository,
                                   TypeUserRepository typeUserRepository,
                                   ModelMapper modelMapper,
                                   CustomPageTypeUserRepository customPageTypeUserRepository,
                                   UserRepository userRepository) {
        if (connectInternalApi == null) {
            this.connectInternalApi = new ConnectInternalApi();
        } else {
            this.connectInternalApi = connectInternalApi;
        }
        this.repository = repository;
        this.pagesRepository = pagesRepository;
        this.typeUserRepository = typeUserRepository;
        this.userRepository = userRepository;
        this.customPageTypeUserRepository = customPageTypeUserRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResultDTO saveAndUpdate(PageTypeUserSaveDTO pageTypeUserSaveDTO, String language) throws Exception {
        try {
            // --------------- Validaciones previas ---------------
            if (pageTypeUserSaveDTO.getIdUser() == null || pageTypeUserSaveDTO.getIdUser() <= 0) {
                return new ResultDTO(false,
                        connectInternalApi.chargeMessage(Message.Msj.return_field_is_required.toString(), language)
                                + "IdUser", 120);
            }
            if (pageTypeUserSaveDTO.getIdTypeUser() == null || pageTypeUserSaveDTO.getIdTypeUser() <= 0) {
                return new ResultDTO(false,
                        connectInternalApi.chargeMessage(Message.Msj.return_field_is_required.toString(), language)
                                + "IdTypeUser", 120);
            }
            if (pageTypeUserSaveDTO.getPages() == null || pageTypeUserSaveDTO.getPages().isEmpty()) {
                return new ResultDTO(false,
                        connectInternalApi.chargeMessage(Message.Msj.return_field_is_required.toString(), language)
                                + "Pages", 120);
            }

            // Buscamos User y TypeUser
            Optional<User> returnUser = userRepository.findById(pageTypeUserSaveDTO.getIdUser());
            if (returnUser.isEmpty()) {
                return new ResultDTO(false,
                        String.format(connectInternalApi.chargeMessage(Message.Msj.userNotFount.toString(), language),
                                pageTypeUserSaveDTO.getIdUser(), " User"),
                        120);
            }
            Optional<TypeUser> returnTypeUser = typeUserRepository.findById(pageTypeUserSaveDTO.getIdTypeUser());
            if (returnTypeUser.isEmpty()) {
                return new ResultDTO(false,
                        String.format(connectInternalApi.chargeMessage(Message.Msj.typeUserNotFound.toString(), language),
                                pageTypeUserSaveDTO.getIdTypeUser(), " TypeUser"),
                        120);
            }

            List<ResultPageTypeUserDTO> listDTO = new ArrayList<>();

            // Por cada página recibida en el DTO, creamos o actualizamos el registro
            for (PagesListDTO item : pageTypeUserSaveDTO.getPages()) {
                PageTypeUser oldPageTypeUser = new PageTypeUser();
                PageTypeUser pageTypeUser = new PageTypeUser();

                // 1) Chequeamos si ya existe la tupla (pageId, typeUserId)
                //    --> Ahora “findAllByPages_IdAndTypeUser_Id”
                Collection<PageTypeUser> existing = repository
                        .findAllByPages_IdAndTypeUser_Id(item.getIdPage(), pageTypeUserSaveDTO.getIdTypeUser());

                if (!existing.isEmpty()) {
                    // Si ya existe, tomamos el primero: (quizás quieras decidir si permites duplicados)
                    pageTypeUser = existing.iterator().next();
                    BeanUtils.copyProperties(pageTypeUser, oldPageTypeUser);
                    pageTypeUser.setLastUpdate(LocalDateTime.now());
                } else {
                    pageTypeUser.setCreated(LocalDateTime.now());
                }

                // 2) Verificamos que la página exista
                Optional<Pages> returnPage = pagesRepository.findById(item.getIdPage());
                if (returnPage.isEmpty()) {
                    return new ResultDTO(false,
                            String.format(connectInternalApi.chargeMessage(Message.Msj.pageNotFound.toString(), language),
                                    item.getIdPage(), " Page"),
                            120);
                }

                // 3) Asignamos las referencias obligatorias
                pageTypeUser.setUser(returnUser.get());
                pageTypeUser.setTypeUser(returnTypeUser.get());

                // 4) Ahora LA ENTIDAD tiene Set<Pages> pages; así que cambiamos el setter:
                //    Si quieres sobreescribir todos los enlaces a páginas:
                Set<Pages> pagesSet = new HashSet<>();
                pagesSet.add(returnPage.get());
                pageTypeUser.setPages(pagesSet);

                //  Si, en lugar de “sobreescribir”, quisieras “agregar” a un Set ya existente:
                //  if (!existing.isEmpty()) {
                //      pageTypeUser.getPages().add(returnPage.get());
                //  } else {
                //      pageTypeUser.setPages(pagesSet);
                //  }

                // 5) Asignamos los flags
                pageTypeUser.setIsUpdate(item.getIsUpdate());
                pageTypeUser.setIsCreate(item.getIsCreate());
                pageTypeUser.setIsActive(item.getIsActive());

                // 6) Guardamos
                PageTypeUser result = repository.save(pageTypeUser);

                // 7) Convertimos a DTO y registramos cambios
                ResultPageTypeUserDTO resultDTO = modelMapper.map(result, ResultPageTypeUserDTO.class);
                List<Map<String, Object>> changes = changeLogUtil.compararEntidades(oldPageTypeUser, pageTypeUser);
                eventPublisher.publishEvent(new EntityChangeEvent(pageTypeUser, changes, pageTypeUser.getUser().getId()));
                listDTO.add(resultDTO);
            }

            return new ResultDTO(listDTO);

        } catch (Exception err) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(Message.Msj.return_error.toString(), language), 1);
        }
    }

    @Override
    public ResultDTO getById(long id, String language) throws Exception {
        try {
            Optional<PageTypeUser> result = repository.findById(id);
            if (result.isPresent()) {
                ResultPageTypeUserDTO dto = modelMapper.map(result.get(), ResultPageTypeUserDTO.class);
                return new ResultDTO(dto);
            } else {
                return new ResultDTO(
                        false,
                        connectInternalApi.chargeMessage(
                                Message.Msj.return_not_exist_the_entity_with_id.toString(), language
                        ),
                        102
                );
            }
        } catch (Exception e) {
            return new ResultDTO(
                    false,
                    connectInternalApi.chargeMessage(Message.Msj.return_process_error.toString(), language),
                    103,
                    e.getMessage()
            );
        }
    }

    @Override
    public ResultDTO getAllItems(PageTypeUserFilterDTO filterDTO, String language)
            throws URISyntaxException, JsonProcessingException {
        try {
            Page<ResultPageTypeUserDTO> list = customPageTypeUserRepository.findAllWithCriteria(
                    filterDTO,
                    PageRequest.of(filterDTO.getPage(), filterDTO.getSize())
            );
            if (list.getContent().isEmpty()) {
                return new ResultDTO(
                        new PageDTO<>(
                                filterDTO.getPage(),
                                filterDTO.getSize(),
                                0,
                                Collections.emptyList()
                        )
                );
            } else {
                List<ResultPageTypeUserDTO> listComplete = list.getContent();
                return new ResultDTO(
                        new PageDTO<>(
                                filterDTO.getPage(),
                                filterDTO.getSize(),
                                list.getTotalPages(),
                                listComplete
                        )
                );
            }
        } catch (Exception e) {
            return new ResultDTO(
                    false,
                    connectInternalApi.chargeMessage(
                            Message.Msj.return_process_error.toString(), language
                    ),
                    103,
                    e.getMessage()
            );
        }
    }

    @Override
    public ResultDTO getByIdTypeUser(PageTypeUserByTypeFilterDTO filterDTO, String language) throws Exception {
        try {
            Optional<User> user = userRepository.findById(filterDTO.idUser());
            if (user.isEmpty()) {
                return new ResultDTO(
                        false,
                        String.format(
                                connectInternalApi.chargeMessage(Message.Msj.userNotFount.toString(), language),
                                filterDTO.idUser(),
                                " User"
                        ),
                        120
                );
            }

            Long idTypeUser = user.get().getTypeUser().getId();
            Boolean state = true;

            Page<ResultPageTypeUserDTO> pageTypeUser = customPageTypeUserRepository
                    .findAllPagesByIdTypeUser(
                            idTypeUser,
                            state,
                            PageRequest.of(filterDTO.page(), filterDTO.size())
                    );

            if (!pageTypeUser.getContent().isEmpty()) {
                List<PageTypeUserByTypeDTO> dtoList = new ArrayList<>();
                for (ResultPageTypeUserDTO item : pageTypeUser) {
                    // ------------------------------------------------------------------
                    // CAMBIO: antes intentabas usar `item.getPages().iterator()`, pero
                    // en tu DTO `ResultPageTypeUserDTO` el método getPages() devuelve
                    // un solo objeto `Pages`, no un Set<Pages>. Por tanto, basta con:
                    //
                    Set<Pages> conjuntoPags = item.getPages();
                    //
                    // ------------------------------------------------------------------
                    Pages primeraPage = conjuntoPags.iterator().next();

                    PageTypeUserByTypeDTO dto = new PageTypeUserByTypeDTO();
                    dto.setIdTypeUser(idTypeUser);
                    dto.setIdPage(primeraPage.getId());
                    dto.setIcon(primeraPage.getIcon());
                    dto.setNpage(primeraPage.getNpage());
                    dto.setNamePage(primeraPage.getName());
                    dto.setIsCreate(item.getIsCreate());
                    dto.setIsUpdate(item.getIsUpdate());
                    dto.setIsActive(item.getIsActive());

                    dtoList.add(dto);
                }
                return new ResultDTO(
                        new PageDTO<>(
                                filterDTO.page(),
                                filterDTO.size(),
                                pageTypeUser.getTotalPages(),
                                dtoList
                        )
                );
            }
            return new ResultDTO(
                    false,
                    connectInternalApi.chargeMessage(Message.Msj.pageNotFound.toString(), language)
                            + filterDTO.idUser(),
                    120
            );
        } catch (Exception err) {
            return new ResultDTO(
                    false,
                    connectInternalApi.chargeMessage(Message.Msj.return_error.toString(), language),
                    1
            );
        }
    }
}