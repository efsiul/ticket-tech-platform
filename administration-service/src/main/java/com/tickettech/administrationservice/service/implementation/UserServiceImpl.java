package com.tickettech.administrationservice.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tickettech.administrationservice.dto.input.*;
import com.tickettech.administrationservice.dto.output.ResultTypeUserDTO;
import com.tickettech.administrationservice.dto.output.ResultUserDTO;
import com.tickettech.administrationservice.entity.*;
import com.tickettech.administrationservice.dto.output.ResultDTO;
import com.tickettech.administrationservice.enums.Message;
import com.tickettech.administrationservice.security.ConnectInternalApi;
import com.tickettech.administrationservice.service.UserService;
import com.tickettech.administrationservice.util.ChangeLogUtil;
import com.tickettech.administrationservice.util.EntityChangeEvent;
import com.tickettech.administrationservice.repository.*;
import com.tickettech.administrationservice.util.securityUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository repository;

    @Autowired
    private final CompanyRepository companyRepository;

    @Autowired
    private final TypeUserRepository typeUserRepository;
    //private final SubsidiaryRepository subsidiaryRepository;
    @Autowired
    private CustomUserRepository customUserRepository;

    private final ConnectInternalApi connectInternalApi;

    @Value("${temporalPassword}")
    String temporalPassword;

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private ChangeLogUtil<User> changeLogUtil;

    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserRepository repository,
                           CompanyRepository companyRepository,
                           TypeUserRepository typeUserRepository,
                           ConnectInternalApi connectInternalApi,
                           ModelMapper modelMapper
                           //SubsidiaryRepository subsidiaryRepository
                           ) {
        if(connectInternalApi ==null) {
            this.connectInternalApi = new ConnectInternalApi();
        }else{
            this.connectInternalApi = connectInternalApi;
        }
        this.repository = repository;
        this.companyRepository = companyRepository;
        this.typeUserRepository = typeUserRepository;
        this.modelMapper = modelMapper;
        //this.subsidiaryRepository = subsidiaryRepository;
    }

    @Override
    public ResultDTO login(LoginDTO loginDTO, String language) throws URISyntaxException, JsonProcessingException {
        try {
            if (loginDTO.getEmail() == null || loginDTO.getEmail().isEmpty()) {
                return new ResultDTO(false, connectInternalApi.chargeMessage(
                        Message.Msj.return_field_is_required.toString(), language) + loginDTO.getEmail(),
                        1);
            }
            List<User> listUser = repository.findByEmail(loginDTO.getEmail());
            if(listUser.isEmpty())
            {
                return new ResultDTO(false,String.format(connectInternalApi.chargeMessage(
                        Message.Msj.userNotFount.toString(), language), " User"), 100);
            }
            User user = listUser.get(0);
            try {
                String lsPassword = securityUtil.decrypt(loginDTO.getPassword());
                String lsPasswordUser = securityUtil.decrypt(user.getPassword());
                if (!lsPasswordUser.equals(lsPassword)) {
                    return new ResultDTO(false, connectInternalApi.chargeMessage(
                            Message.Msj.passwordIsIncorrect.toString(), language), 101);
                }
            }catch (Exception err)
            {
                return new ResultDTO(false, connectInternalApi.chargeMessage(
                        Message.Msj.passwordIsIncorrect.toString(), language), 101);
            }
            if(!user.getIsActive())
            {
                return new ResultDTO(false,connectInternalApi.chargeMessage(
                        Message.Msj.return_exist.toString(), language), 100);
            }
            user.setPassword("xxxxxxxxxxx");

            ResultUserDTO resultDTO = modelMapper.map(user, ResultUserDTO.class);
            return new ResultDTO(resultDTO);
        }
        catch (Exception err)
        {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.return_error.toString(), language),
                    1);
        }
    }

    @Override
    public ResultDTO saveAndUpdate(UserDTO userDTO, String language) throws Exception {

        if(userDTO.getEmail().isEmpty())
            return new ResultDTO(false, connectInternalApi.chargeMessage(Message.Msj.return_field_is_required.toString(),language) + " email", 102);

        if(userDTO.getName().isEmpty())
            return new ResultDTO(false, connectInternalApi.chargeMessage(Message.Msj.return_field_is_required.toString(),language) + " name", 102);


        if (userDTO.getIdCompany() == null || userDTO.getIdCompany() <= 0) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.return_field_is_required.toString(), language) + " Company", 120);
        }

        Optional<Company> company = companyRepository.findById(userDTO.getIdCompany());
        if (company.isEmpty()) {
            return new ResultDTO(false, String.format(connectInternalApi.chargeMessage(
                            Message.Msj.companyNotFound.toString(), language),
                    userDTO.getIdCompany(), " Company"), 120);
        }
        if (userDTO.getIdSubsidiary() == null || userDTO.getIdSubsidiary() <= 0) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.return_field_is_required.toString(), language) + " Subsidiary", 120);
        }

        //Optional<Subsidiary> subsidiary = subsidiaryRepository.findById(userDTO.getIdSubsidiary());
        //if (subsidiary.isEmpty()) {
        //    return new ResultDTO(false, String.format(connectInternalApi.chargeMessage(
        //                    Message.Msj.userNotFount.toString(), language),
        //            userDTO.getIdSubsidiary(), " Subsidiary"), 120);
        //}

        if(userDTO.getIdTypeUser() == null || userDTO.getIdTypeUser() <= 0)
            return new ResultDTO(false, connectInternalApi.chargeMessage(Message.Msj.return_field_is_required.toString(),language) + " idType", 102);

        Optional<TypeUser> typeUser = typeUserRepository.findById(userDTO.getIdTypeUser());
        if (typeUser.isEmpty()) {
            return new ResultDTO(false, String.format(connectInternalApi.chargeMessage(
                            Message.Msj.typeUserNotFound.toString(), language),
                    userDTO.getIdUser(), " typeUser"), 120);
        }

        if (userDTO.getIdUser() == null || userDTO.getIdUser() <= 0) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.return_field_is_required.toString(), language) + " idUser", 120);
        }

        Optional<User> exisatUser = repository.findById(userDTO.getIdUser());
        if (exisatUser.isEmpty()) {
            return new ResultDTO(false, String.format(connectInternalApi.chargeMessage(
                            Message.Msj.userNotFount.toString(), language),
                    userDTO.getIdUser(), " User"), 120);
        }

        Collection<User> existingByEmail  = repository.findByEmail(userDTO.getEmail());
        User oldUser = new User();
        User user = new User();
        if(userDTO.getId() != null && userDTO.getId() > 0){
            if(!existingByEmail.isEmpty()){
                for (User userValid : existingByEmail) {
                    if(!userValid.getId().equals(userDTO.getId()))
                        return new ResultDTO(false, connectInternalApi.chargeMessage(Message.Msj.return_exist.toString(),language), 102);
                }
            }

            Optional<User> existingItem  = repository.findById(userDTO.getId());
            if (existingItem.isPresent() ) {
                user = existingItem.get();
                BeanUtils.copyProperties(user, oldUser);
                user.setLastUpdate(LocalDateTime.now());
            }else{
                return new ResultDTO(false, connectInternalApi.chargeMessage(
                        Message.Msj.return_not_exist_the_entity_with_id.toString(),language)+ userDTO.getId(),
                        1);
            }
        }else{
            if(!existingByEmail.isEmpty())
            {
                return new ResultDTO(false, connectInternalApi.chargeMessage(Message.Msj.return_exist.toString(),language), 102);
            }

            user.setCreation(LocalDateTime.now());

            if(userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()){
                user.setPassword(userDTO.getPassword());
            }else{
                user.setPassword(this.temporalPassword);
            }
        }

        user.setCompany(company.get());
        //user.setSubsidiary(subsidiary.get());
        user.setTypeUser(typeUser.get());
        user.setName(userDTO.getName().trim());
        user.setLastName(userDTO.getLastName().trim());
        user.setEmail(userDTO.getEmail().trim());
        user.setCellphone(userDTO.getCellphone().trim());
        user.setIsActive(userDTO.getIsActive());
        user.setIsAdmin(userDTO.getIsAdmin());
        user.setUser(exisatUser.get());

        User result = repository.save(user);
        result.setPassword("");

        List<Map<String, Object>> changes = changeLogUtil.compararEntidades(oldUser, user);
        eventPublisher.publishEvent(new EntityChangeEvent(user, changes, user.getUser().getId()));

        ResultUserDTO resultDTO = modelMapper.map(user, ResultUserDTO.class);
        return new ResultDTO(resultDTO);
    }

    @Override
    public ResultDTO resetPassword(ResetUserPasswordDTO resetUserPasswordDTO, String language) throws Exception {
        Optional<User> result = repository.findById(resetUserPasswordDTO.getId());

        if (!result.isPresent()) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.return_not_exist_the_entity_with_id.toString(), language),
                    102);
        }

        if (resetUserPasswordDTO.getPassword() == null || resetUserPasswordDTO.getPassword().isEmpty()) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.passwordIsIncorrect.toString(), language), 1);
        }

        User user = result.get();

        try {
            String lsPassword = securityUtil.decrypt(resetUserPasswordDTO.getOldPassword());
            String lsPasswordUser = securityUtil.decrypt(user.getPassword());
            if (!lsPasswordUser.equals(lsPassword)) {
                return new ResultDTO(false, connectInternalApi.chargeMessage(
                        Message.Msj.passwordIsIncorrect.toString(), language), 101);
            }
        } catch (Exception err) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.passwordIsIncorrect.toString(), language), 101);
        }

        user.setPassword(resetUserPasswordDTO.getPassword());

        User resultUser = repository.save(user);
        resultUser.setPassword("");

        ResultUserDTO resultDTO = modelMapper.map(user, ResultUserDTO.class);
        return new ResultDTO(resultDTO);
    }

    @Override
    public ResultDTO getById(long id, String language) throws Exception {
        try {
            Optional<User> result = repository.findById(id);
            if (result.isPresent()) {
                ResultUserDTO dto = modelMapper.map(result.get(), ResultUserDTO.class);
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
    public ResultDTO getAllItems(UserFilterDTO filterDTO, String language) throws URISyntaxException, JsonProcessingException {
        try {
            Page<ResultUserDTO> list = customUserRepository.findAllWithCriteria(
                    filterDTO, PageRequest.of(filterDTO.getPage(), filterDTO.getSize()));
            if (list.getContent().isEmpty()) {
                return new ResultDTO(new PageDTO<ResultUserDTO>(filterDTO.getPage(), filterDTO.getSize(), 0, Collections.emptyList()));
            } else {
                List<ResultUserDTO> listComplete = list.getContent();
                return new ResultDTO(new PageDTO<ResultUserDTO>(filterDTO.getPage(), filterDTO.getSize(), list.getTotalPages(), listComplete));
            }
        } catch (Exception e) {
            return new ResultDTO(false, connectInternalApi.chargeMessage(
                    Message.Msj.return_process_error.toString(), language), 103, e.getMessage());
        }
    }
}
