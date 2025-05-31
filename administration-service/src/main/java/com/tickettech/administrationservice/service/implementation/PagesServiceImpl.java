package com.tickettech.administrationservice.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tickettech.administrationservice.dto.input.*;
import com.tickettech.administrationservice.dto.output.ResultPagesDTO;
import com.tickettech.administrationservice.entity.*;
import com.tickettech.administrationservice.dto.output.ResultDTO;
import com.tickettech.administrationservice.entity.Module;
import com.tickettech.administrationservice.enums.Message;
import com.tickettech.administrationservice.security.ConnectInternalApi;
import com.tickettech.administrationservice.service.PagesService;
import com.tickettech.administrationservice.repository.*;
import com.tickettech.administrationservice.util.ChangeLogUtil;
import com.tickettech.administrationservice.util.EntityChangeEvent;
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
public class PagesServiceImpl implements PagesService {
    private final ConnectInternalApi connectInternalApi;
    private final PagesRepository repository;
    private final ModelMapper modelMapper;
    private final CompanyRepository companyRepository;
    private final CustomPagesRepository customPagesRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;
    private final String dataNotFound = "S/D";
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private ChangeLogUtil<Pages> changeLogUtil;

    public PagesServiceImpl(PagesRepository repository,
                            ConnectInternalApi connectInternalApi,
                            ModelMapper modelMapper,
                            CompanyRepository companyRepository,
                            CustomPagesRepository customPagesRepository,
                            UserRepository userRepository,
                            ModuleRepository moduleRepository){
        if(connectInternalApi ==null) {
            this.connectInternalApi = new ConnectInternalApi();
        }else{
            this.connectInternalApi = connectInternalApi;
        }
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.companyRepository = companyRepository;
        this.customPagesRepository = customPagesRepository;
        this.userRepository = userRepository;
        this.moduleRepository = moduleRepository;
    }

    @Override
    public ResultDTO saveAndUpdate(PagesDTO pagesDTO, String language) throws Exception {
        try {
            // Validaciones de campos obligatorios
            if (pagesDTO.getName() == null || pagesDTO.getName().trim().isEmpty()) {
                return new ResultDTO(false, connectInternalApi.chargeMessage(
                        Message.Msj.return_field_is_required.toString(), language) + " name",
                        102);
            }

            if (pagesDTO.getIdModule() == null || pagesDTO.getIdModule() <= 0) {
                return new ResultDTO(false, connectInternalApi.chargeMessage(
                        Message.Msj.return_field_is_required.toString(), language) + " idModule",
                        102);
            }

            if (pagesDTO.getIdUser() == null || pagesDTO.getIdUser() <= 0) {
                return new ResultDTO(false, connectInternalApi.chargeMessage(
                        Message.Msj.return_field_is_required.toString(), language) + " idUser", 120);
            }

            Optional<User> user = userRepository.findById(pagesDTO.getIdUser());
            if (user.isEmpty()) {
                return new ResultDTO(false, String.format(connectInternalApi.chargeMessage(
                                Message.Msj.userNotFount.toString(), language),
                        pagesDTO.getIdUser(), " User"), 120);
            }


            if (pagesDTO.getIdModule() == null || pagesDTO.getIdModule() <= 0) {
                return new ResultDTO(false, connectInternalApi.chargeMessage(
                        Message.Msj.return_field_is_required.toString(), language) + " Module", 120);
            }

            Optional<Module> module = moduleRepository.findById(pagesDTO.getIdModule());
            if (module.isEmpty()) {
                return new ResultDTO(false, String.format(connectInternalApi.chargeMessage(
                                Message.Msj.userNotFount.toString(), language),
                        pagesDTO.getIdModule(), " Module"), 120);
            }

            Pages pages = new Pages();
            Pages oldPages = new Pages();
            if (pagesDTO.getId() != null && pagesDTO.getId() > 0) {
                Optional<Pages> existingPages = repository.findById(pagesDTO.getId());
                if (existingPages.isPresent()) {
                    pages = existingPages.get();
                    BeanUtils.copyProperties(pages, oldPages);
                    pages.setLastUpdate(LocalDateTime.now());
                }else{
                    return new ResultDTO(false, String.format(connectInternalApi.chargeMessage(
                            Message.Msj.return_not_exist_the_entity_with_id.toString(), language), pagesDTO.getId()),
                            102);
                }
            } else {
                pages.setCreation(LocalDateTime.now());
            }
            pages.setName(pagesDTO.getName());
            pages.setNpage(pagesDTO.getNpage());
            pages.setIcon(pagesDTO.getIcon());
            pages.setIsActive(pagesDTO.getIsActive());
            pages.setUser(user.get());
            pages.setModule(module.get());

            Pages result = repository.save(pages);
            List<Map<String, Object>> changes = changeLogUtil.compararEntidades(oldPages, pages);
            eventPublisher.publishEvent(new EntityChangeEvent(pages, changes, pages.getUser().getId()));

            ResultPagesDTO resultDTO = modelMapper.map(result, ResultPagesDTO.class);
            return new ResultDTO(true, "Ok", 0, resultDTO);
        } catch (Exception err) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.return_error.toString(), language), 1, err.getMessage());
        }
    }

    @Override
    public ResultDTO getById(long id, String language) throws Exception {
        try {
            Optional<Pages> result = repository.findById(id);
            if (result.isPresent()) {
                ResultPagesDTO dto = modelMapper.map(result.get(), ResultPagesDTO.class);
                return new ResultDTO(dto);
            } else {
                return new ResultDTO(false, connectInternalApi.chargeMessage(
                        Message.Msj.return_not_exist_the_entity_with_id.toString(), language), 102);
            }
        } catch (Exception e) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.return_process_error.toString(), language), 103, e.getMessage());
        }
    }

    @Override
    public ResultDTO getAllItems(PagesFilterDTO filterDTO, String language) throws URISyntaxException, JsonProcessingException {
        try {
            Page<ResultPagesDTO> list = customPagesRepository.findAllWithCriteria(
                    filterDTO, PageRequest.of(filterDTO.getPage(), filterDTO.getSize()));
            if (list.getContent().isEmpty()) {
                return new ResultDTO(new PageDTO<ResultPagesDTO>(filterDTO.getPage(), filterDTO.getSize(), 0, Collections.emptyList()));
            } else {
                List<ResultPagesDTO> listComplete = list.getContent();
                return new ResultDTO(new PageDTO<ResultPagesDTO>(filterDTO.getPage(), filterDTO.getSize(), list.getTotalPages(), listComplete));
            }
        } catch (Exception e) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.return_process_error.toString(), language), 103, e.getMessage());
        }
    }
}