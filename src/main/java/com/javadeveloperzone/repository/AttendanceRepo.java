package com.javadeveloperzone.repository;

import com.javadeveloperzone.models.FolderModel.AttendanceModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepo extends MongoRepository<AttendanceModel,String> {
      List<AttendanceModel> findAllByDate(String date);
      List<AttendanceModel> findAllByPresent(Boolean present);
      @Query("{ 'date': ?0, 'employeeId': ?1 }")
      Optional<AttendanceModel> findByDateAndId(String date,Long id);

}
