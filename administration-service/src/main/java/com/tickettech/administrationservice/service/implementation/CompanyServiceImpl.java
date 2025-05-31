package com.tickettech.administrationservice.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tickettech.administrationservice.dto.input.CompanyDTO;
import com.tickettech.administrationservice.dto.input.CompanyFilterDTO;
import com.tickettech.administrationservice.dto.input.PageDTO;
import com.tickettech.administrationservice.dto.output.ResultCompanyDTO;
import com.tickettech.administrationservice.dto.output.ResultDTO;
import com.tickettech.administrationservice.entity.Company;
import com.tickettech.administrationservice.entity.User;
import com.tickettech.administrationservice.enums.Message;
import com.tickettech.administrationservice.repository.CompanyRepository;
import com.tickettech.administrationservice.repository.CustomCompanyRepository;
import com.tickettech.administrationservice.repository.UserRepository;
import com.tickettech.administrationservice.security.ConnectInternalApi;
import com.tickettech.administrationservice.service.CompanyService;
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

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private final CompanyRepository repository;
    private final UserRepository userRepository;
    private final CustomCompanyRepository customCompanyRepository;
    private final ModelMapper modelMapper;
    @Autowired
    private ConnectInternalApi connectInternalApi;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private ChangeLogUtil<Company> changeLogUtil;

    public CompanyServiceImpl(CompanyRepository repository, ConnectInternalApi connectInternalApi,
                              ModelMapper modelMapper, CustomCompanyRepository customCompanyRepository,
                              UserRepository userRepository){
        if(connectInternalApi ==null) {
            this.connectInternalApi = new ConnectInternalApi();
        }else{
            this.connectInternalApi = connectInternalApi;
        }
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.customCompanyRepository = customCompanyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResultDTO saveAndUpdate(CompanyDTO companyDTO, String language) throws Exception {
        if (companyDTO.getName() == null || companyDTO.getName().isEmpty()) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.return_field_is_required.toString(), language) + " name",
                    102);
        }
        if (companyDTO.getNit() == null || companyDTO.getNit().isEmpty()) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.return_field_is_required.toString(), language) + " nit",
                    102);
        }

        if (companyDTO.getIdUser() == null || companyDTO.getIdUser() <= 0) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.return_field_is_required.toString(), language) + " idUser", 120);
        }

        Optional<User> user = userRepository.findById(companyDTO.getIdUser());
        if (user.isEmpty()) {
            return new ResultDTO(false, String.format(connectInternalApi.chargeMessage(
                            Message.Msj.userNotFount.toString(), language),
                    companyDTO.getIdUser(), "idUser"), 120);
        }

        Company company = new Company();
        Company oldCompany = new Company();
        if (companyDTO.getId() > 0) {
            Optional<Company> existingCompany = repository.findById(companyDTO.getId());
            if (existingCompany.isPresent()) {
                company = existingCompany.get();
                BeanUtils.copyProperties(company, oldCompany);
                company.setLastUpdate(LocalDateTime.now());
            } else {
                return new ResultDTO(false, String.format(connectInternalApi.chargeMessage(
                        Message.Msj.return_not_exist_the_entity_with_id.toString(), language), company.getId()),
                        102);
            }
        } else {
            company.setCreation(LocalDateTime.now());
        }

        if (repository.findFirstByNit(companyDTO.getNit()).isPresent()) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.thisNitExists.toString(), language), 120);
        }

        company.setName(companyDTO.getName());
        company.setAddress(companyDTO.getAddress());
        company.setContactPerson(companyDTO.getContactPerson());
        company.setPhone(companyDTO.getPhone());
        company.setMail(companyDTO.getMail());
        company.setState(companyDTO.getState());
        company.setImage(companyDTO.getImage());
        company.setNit(companyDTO.getNit());
        company.setUser(user.get());

        Company result = repository.save(company);
        List<Map<String, Object>> changes = changeLogUtil.compararEntidades(oldCompany, company);
        eventPublisher.publishEvent(new EntityChangeEvent(company, changes, company.getUser().getId()));

        ResultCompanyDTO resultDTO = modelMapper.map(result, ResultCompanyDTO.class);
        return new ResultDTO(true, "Ok", 0, resultDTO);
    }



    @Override
    public ResultDTO getById(Long id, String language) throws Exception {
        try {
            Optional<Company> result = repository.findById(id);
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
    public ResultDTO getAllItems(CompanyFilterDTO filterDTO, String language) throws URISyntaxException, JsonProcessingException {
        try {
            Page<ResultCompanyDTO> list = customCompanyRepository.findAllWithCriteria(
                    filterDTO, PageRequest.of(filterDTO.getPage(), filterDTO.getSize()));
            if (list.getContent().isEmpty()) {
                return new ResultDTO(new PageDTO<ResultCompanyDTO>(filterDTO.getPage(), filterDTO.getSize(), 0, Collections.emptyList()));
            } else {
                return new ResultDTO(new PageDTO<ResultCompanyDTO>(filterDTO.getPage(), filterDTO.getSize(), list.getTotalPages(), list.getContent()));
            }
        } catch (Exception e) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.return_process_error.toString(), language), 103, e.getMessage());
        }
    }
}
