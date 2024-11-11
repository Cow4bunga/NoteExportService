package com.ilevitsky.testproject.exportservice.repository;

import com.ilevitsky.testproject.exportservice.entity.CompanyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyUserRepository extends JpaRepository<CompanyUser, UUID> {
  boolean existsCompanyUserByLogin(String login);

  Optional<CompanyUser> findCompanyUserByLogin(String login);
}
