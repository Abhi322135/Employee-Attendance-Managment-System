package com.javadeveloperzone.repository;

import com.javadeveloperzone.models.AdminRelatedModels.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends MongoRepository<Admin,String> {

    Optional<Admin> findByEmail(String email);
}
