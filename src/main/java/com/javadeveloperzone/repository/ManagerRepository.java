package com.javadeveloperzone.repository;

import com.javadeveloperzone.models.ManagerRelatedModels.Manager;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

    @Repository

    public interface ManagerRepository extends MongoRepository<Manager, Long> {

        List<Manager> findAllByName(String name);
        Optional<Manager> findByEmail(String x);

    }

