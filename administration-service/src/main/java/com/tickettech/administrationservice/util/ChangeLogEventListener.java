package com.tickettech.administrationservice.util;
/*
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tickettech.administrationservice.dto.output.ResultDTO;
import com.tickettech.administrationservice.entity.ChangeLogEntry;
import com.tickettech.administrationservice.entity.User;
import com.tickettech.administrationservice.enums.Message;
import com.tickettech.administrationservice.repository.ChangeLogRepository;
import com.tickettech.administrationservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class ChangeLogEventListener {

    @Autowired
    private ChangeLogRepository changeLogRepository;
    @Autowired
    private UserRepository userRepository;

    @EventListener
    public void handleEntityChangeEvent(EntityChangeEvent event) {
        Object entity = event.getEntity();
        List<Map<String, Object>> changesList = event.getChanges();


        ChangeLogEntry logEntry = new ChangeLogEntry();
        Optional<User> exisatUser = userRepository.findById(event.getIdUser());
        if (exisatUser.isEmpty()) {
            logEntry.setUser(exisatUser.get());
        }else{
            User us = new User();
            logEntry.setUser(us);
        }
        logEntry.setModule("Administration");
        logEntry.setTableName(entity.getClass().getSimpleName());
        logEntry.setDate(LocalDateTime.now());

        long idTable = 1;
        Optional<ChangeLogEntry> result = changeLogRepository.findFirstByOrderByIdDesc();
        if (result.isPresent()) {
            ChangeLogEntry largest = result.get();
            idTable = largest.getId() + 1;
        }
        logEntry.setId(idTable);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            List<Map<String, Object>> changeList = new ArrayList<>();
            for (Map<String, Object> change : changesList) {
                Map<String, Object> formattedChange = new HashMap<>();
                formattedChange.put("field", change.get("field"));
                formattedChange.put("oldValue", change.get("oldValue"));
                formattedChange.put("newValue", change.get("newValue"));
                changeList.add(formattedChange);
            }

            Map<String, Object> changeLogMap = new HashMap<>();
            changeLogMap.put("change", changeList);

            String jsonChanges = objectMapper.writeValueAsString(changeLogMap);
            logEntry.setChange(jsonChanges);

            ChangeLogEntry result1 = changeLogRepository.save(logEntry);
            System.out.println(result1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

 */
