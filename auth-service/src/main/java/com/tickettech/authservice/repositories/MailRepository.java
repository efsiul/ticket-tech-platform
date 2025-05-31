package com.tickettech.authservice.repositories;

import com.tickettech.authservice.models.Mail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MailRepository extends JpaRepository<Mail, Long> {
    Optional<Mail> findFirstByIdMail(long idMail);
}
