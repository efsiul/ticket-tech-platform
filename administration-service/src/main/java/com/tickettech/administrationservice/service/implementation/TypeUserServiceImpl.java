package com.tickettech.administrationservice.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tickettech.administrationservice.dto.input.*;
import com.tickettech.administrationservice.dto.output.ResultTypeUserDTO;
import com.tickettech.administrationservice.entity.*;
import com.tickettech.administrationservice.dto.output.ResultDTO;
import com.tickettech.administrationservice.enums.Message;
import com.tickettech.administrationservice.security.ConnectInternalApi;
import com.tickettech.administrationservice.service.TypeUserService;
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
public class TypeUserServiceImpl implements TypeUserService {
    @Autowired
    private final TypeUserRepository repository;
    private final CustomTypeUserRepository customTypeUserRepository;
    private final ConnectInternalApi connectInternalApi;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    //private final SubsidiaryRepository subsidiaryRepository;
    private final String dataNotFound = "S/D";
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private ChangeLogUtil<TypeUser> changeLogUtil;

    @Autowired
    public TypeUserServiceImpl(TypeUserRepository repository, ConnectInternalApi connectInternalApi,
                               ModelMapper modelMapper,
                               CustomTypeUserRepository customTypeUserRepository,
                               UserRepository userRepository,
                               CompanyRepository companyRepository
                               //SubsidiaryRepository subsidiaryRepository
    ) {
        if(connectInternalApi ==null) {
            this.connectInternalApi = new ConnectInternalApi();
        }else{
            this.connectInternalApi = connectInternalApi;
        }
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.customTypeUserRepository = customTypeUserRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        //this.subsidiaryRepository = subsidiaryRepository;
    }

    @Override
    public ResultDTO saveAndUpdate(TypeUserDTO typeUserDTO, String language) throws Exception {
        if (typeUserDTO.getName() == null || typeUserDTO.getName().isEmpty()) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.return_field_is_required.toString(), language) + " name",
                    102);
        }

        if (typeUserDTO.getIdUser() == null || typeUserDTO.getIdUser() <= 0) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.return_field_is_required.toString(), language) + " idUser", 120);
        }

        Optional<User> user = userRepository.findById(typeUserDTO.getIdUser());
        if (user.isEmpty()) {
            return new ResultDTO(false, String.format(connectInternalApi.chargeMessage(
                            Message.Msj.userNotFount.toString(), language),
                    typeUserDTO.getIdUser(), " User"), 120);
        }
        if (typeUserDTO.getIdCompany() == null || typeUserDTO.getIdCompany() <= 0) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.return_field_is_required.toString(), language) + " company", 120);
        }

        Optional<Company> company = companyRepository.findById(typeUserDTO.getIdCompany());
        if (company.isEmpty()) {
            return new ResultDTO(false, String.format(connectInternalApi.chargeMessage(
                            Message.Msj.companyNotFound.toString(), language),
                    typeUserDTO.getIdCompany(), " Company"), 120);
        }
        if (typeUserDTO.getIdSubsidiary() == null || typeUserDTO.getIdSubsidiary() <= 0) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.return_field_is_required.toString(), language) + " idSubsidiary", 120);
        }

        //Optional<Subsidiary> subsidiary = subsidiaryRepository.findById(typeUserDTO.getIdSubsidiary());
        //if (subsidiary.isEmpty()) {
        //    return new ResultDTO(false, String.format(connectInternalApi.chargeMessage(
        //                    Message.Msj.userNotFount.toString(), language),
        //            typeUserDTO.getIdSubsidiary(), " Subsidiary"), 120);
        //}

        TypeUser typeUser = new TypeUser();
        TypeUser oldTypeUser = new TypeUser();
        if (typeUserDTO.getId() != null && typeUserDTO.getId() > 0) {
            Optional<TypeUser> existingTypeUser = repository.findById(typeUserDTO.getId());
            if (existingTypeUser.isPresent()) {
                typeUser = existingTypeUser.get();
                BeanUtils.copyProperties(typeUser, oldTypeUser);
                typeUser.setLastUpdate(LocalDateTime.now());
            }else{
                return new ResultDTO(false, String.format(connectInternalApi.chargeMessage(
                        Message.Msj.return_not_exist_the_entity_with_id.toString(), language), typeUserDTO.getId()),
                        102);
            }
        } else {
            typeUser.setCreation(LocalDateTime.now());
        }
        Optional<TypeUser> exist = repository.findByName(typeUserDTO.getName().trim());
        if (exist.isPresent() && !exist.get().getId().equals(typeUserDTO.getId())) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.thisNameExists.toString(), language), 102);
        }
        typeUser.setName(typeUserDTO.getName().trim());
        typeUser.setCompany(company.get());
        //typeUser.setSubsidiary(subsidiary.get());
        typeUser.setUser(user.get());
        typeUser.setIsActive(typeUserDTO.getIsActive());

        TypeUser result = repository.save(typeUser);
        List<Map<String, Object>> changes = changeLogUtil.compararEntidades(oldTypeUser, typeUser);
        eventPublisher.publishEvent(new EntityChangeEvent(typeUser, changes, typeUser.getUser().getId()));

        ResultTypeUserDTO resultDTO = modelMapper.map(result, ResultTypeUserDTO.class);
        return new ResultDTO(true, "OK", 0, resultDTO);
    }


    @Override
    public ResultDTO getById(long id, String language) throws Exception {
        try {
            Optional<TypeUser> result = repository.findById(id);
            if (result.isPresent()) {
                ResultTypeUserDTO dto = modelMapper.map(result.get(), ResultTypeUserDTO.class);
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
    public ResultDTO getAllItems(TypeUserFilterDTO filterDTO, String language) throws URISyntaxException, JsonProcessingException {
        try {
            Page<ResultTypeUserDTO> list = customTypeUserRepository.findAllWithCriteria(
                    filterDTO, PageRequest.of(filterDTO.getPage(), filterDTO.getSize()));
            if (list.getContent().isEmpty()) {
                return new ResultDTO(new PageDTO<ResultTypeUserDTO>(filterDTO.getPage(), filterDTO.getSize(), 0, Collections.emptyList()));
            } else {
                List<ResultTypeUserDTO> listComplete = list.getContent();
                return new ResultDTO(new PageDTO<ResultTypeUserDTO>(filterDTO.getPage(), filterDTO.getSize(), list.getTotalPages(), listComplete));
            }
        } catch (Exception e) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.return_process_error.toString(), language), 103, e.getMessage());
        }
    }
/*
    public ResultTypeUserDTO completeData(ResultTypeUserDTO item, String language){
        if(item == null)
            return item;

        List<ResultTypeUserDTO> list = new ArrayList<>();
        list.add(item);

        list = completeData(list, language);

        return list.get(0);
    }

    public List<ResultTypeUserDTO> completeData(List<ResultTypeUserDTO> list, String language) {

        if (list == null)
            return list;

        listUser.clear();
        listCompany.clear();

        for (ResultTypeUserDTO item : list) {
            try {
                CompletableFuture<Void> futureUser = CompletableFuture.runAsync(() -> {
                    completeDataUser(item);
                });

                CompletableFuture<Void> futureCompany = CompletableFuture.runAsync(() -> {
                    completeDataCompany(item);
                });
                CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(
                        futureUser, futureCompany
                );

                combinedFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                //No user the language service
                System.out.println("Error: completeData | Store: " + e.getMessage());
            }
        }
        return list;
    }
    Map<Long, User> listUser = new HashMap<>();
    Map<Long, Company> listCompany = new HashMap<>();

    public void completeDataUser(ResultTypeUserDTO item) {
        try {
            if(listUser.containsKey(item.getIdUser()))
            {
                item.setNameUser(listUser.get(item.getIdUser()).getName());
            }
            else {
                Optional<User> userOptional = userRepository.findById(item.getIdUser());
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    item.setNameUser(user.getName());
                    listUser.put(item.getIdUser(), user);
                } else {
                    item.setNameUser("S/D");
                }
            }
        } catch (Exception err) {
            System.out.println("CompleteData Administration TypeUser | DataUser: " + err.getMessage());
            item.setNameUser(dataNotFound);
        }
    }


    public void completeDataCompany(ResultTypeUserDTO item) {
        try {
            if(listCompany.containsKey(item.getIdCompany()))
            {
                item.setNameCompany(listCompany.get(item.getIdCompany()).getName());
            }
            else {
                Optional<Company> companyOptional = companyRepository.findById(item.getIdCompany());
                if (companyOptional.isPresent()) {
                    Company company = companyOptional.get();
                    item.setNameCompany(company.getName());
                    listCompany.put(item.getIdCompany(), company);
                } else {
                    item.setNameCompany("S/D");
                }
            }
        } catch (Exception err) {
            System.out.println("CompleteData Administration Store | Company: " + err.getMessage());
            item.setNameCompany(dataNotFound);
        }
    }

 */
}