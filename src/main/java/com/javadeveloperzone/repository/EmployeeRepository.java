package com.javadeveloperzone.repository;

import com.javadeveloperzone.models.EmployeeModels.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface EmployeeRepository extends MongoRepository<Employee, Long> {

    List<Employee> findAllByName(String name);
    Optional<Employee> findByEmail(String x);
    List<Employee> findAllByManagerID(Long id);
}
