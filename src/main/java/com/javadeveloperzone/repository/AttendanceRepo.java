package com.javadeveloperzone.repository;

import com.javadeveloperzone.models.FolderModel.AttendanceModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepo extends MongoRepository<AttendanceModel,String> {

      List<AttendanceModel> findAllByDate(String date);

      List<AttendanceModel> findAllByPresent(Boolean present);
}
