package com.tickettech.administrationservice.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tickettech.administrationservice.dto.input.*;
import com.tickettech.administrationservice.dto.output.ResultCompanyDTO;
import com.tickettech.administrationservice.dto.output.ResultDTO;
import com.tickettech.administrationservice.dto.output.ResultModuleDTO;
import com.tickettech.administrationservice.entity.Company;
import com.tickettech.administrationservice.entity.Module;
import com.tickettech.administrationservice.entity.TypeUser;
import com.tickettech.administrationservice.entity.User;
import com.tickettech.administrationservice.enums.Message;
import com.tickettech.administrationservice.repository.*;
import com.tickettech.administrationservice.security.ConnectInternalApi;
import com.tickettech.administrationservice.service.CompanyService;
import com.tickettech.administrationservice.service.ModuleService;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ModuleServiceImpl implements ModuleService {

    @Autowired
    private final ModuleRepository repository;
    private final UserRepository userRepository;
    private final CustomModuleRepository customRepository;
    private final ModelMapper modelMapper;
    @Autowired
    private ConnectInternalApi connectInternalApi;

    public ModuleServiceImpl(ModuleRepository repository, ConnectInternalApi connectInternalApi,
                             ModelMapper modelMapper, CustomModuleRepository customRepository,
                             UserRepository userRepository
    ){
        if(connectInternalApi ==null) {
            this.connectInternalApi = new ConnectInternalApi();
        }else{
            this.connectInternalApi = connectInternalApi;
        }
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.customRepository = customRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResultDTO saveAndUpdate(ModuleDTO dto, String language) throws Exception {
        if (dto.getName() == null || dto.getName().isEmpty()) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.return_field_is_required.toString(), language) + " name",
                    102);
        }
        Optional<User> user = userRepository.findById(dto.getIdUser());
        if (user.isEmpty()) {
            return new ResultDTO(false, String.format(connectInternalApi.chargeMessage(
                            Message.Msj.userNotFount.toString(), language),
                    dto.getIdUser(), "User"), 120);
        }

        Module module = new Module();
        Module oldModule = new Module();
        if (dto.getId() != null && dto.getId() > 0) {
            Optional<Module> existingModule = repository.findById(dto.getId());
            if (existingModule.isPresent()) {
                module = existingModule.get();
                module.setLastUpdate(LocalDateTime.now());
            } else {
                return new ResultDTO(false, String.format(connectInternalApi.chargeMessage(
                        Message.Msj.return_not_exist_the_entity_with_id.toString(), language), module.getId()),
                        102);
            }
        } else {
            module.setCreation(LocalDateTime.now());
        }
        Optional<Module> exist = repository.findByName(dto.getName());
        if (exist.isPresent() && !exist.get().getId().equals(dto.getId())) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.thisNameExists.toString(), language), 102);
        }

        module.setName(dto.getName());
        module.setState(dto.getState());
        module.setUser(user.get());

        Module result = repository.save(module);
        ResultCompanyDTO resultDTO = modelMapper.map(result, ResultCompanyDTO.class);
        return new ResultDTO(true, "Ok", 0, resultDTO);
    }



    @Override
    public ResultDTO getById(Long id, String language) throws Exception {
        try {
            Optional<Module> result = repository.findById(id);
            if (result.isPresent()) {
                ResultCompanyDTO dto = modelMapper.map(result.get(), ResultCompanyDTO.class);
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
    public ResultDTO getAllItems(ModuleFilterDTO filterDTO, String language) throws URISyntaxException, JsonProcessingException {
        try {
            Page<ResultModuleDTO> list = customRepository.findAllWithCriteria(
                    filterDTO, PageRequest.of(filterDTO.getPage(), filterDTO.getSize()));
            if (list.getContent().isEmpty()) {
                return new ResultDTO(new PageDTO<ResultModuleDTO>(filterDTO.getPage(), filterDTO.getSize(), 0, Collections.emptyList()));
            } else {
                return new ResultDTO(new PageDTO<ResultModuleDTO>(filterDTO.getPage(), filterDTO.getSize(), list.getTotalPages(), list.getContent()));
            }
        } catch (Exception e) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.return_process_error.toString(), language), 103, e.getMessage());
        }
    }
}
