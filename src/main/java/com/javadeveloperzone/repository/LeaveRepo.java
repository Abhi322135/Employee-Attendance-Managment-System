package com.javadeveloperzone.repository;

import com.javadeveloperzone.models.FolderModel.LeaveRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepo extends MongoRepository<LeaveRequest,String> {

      List<LeaveRequest> findAllByEmployeeId(Long id);
}
