package com.javadeveloperzone.service.ManagerService;

import com.javadeveloperzone.models.ManagerRelatedModels.Manager;
import org.springframework.web.multipart.MultipartFile;

import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Optional;

public interface ManagerService {
    Manager save(Manager employee) throws InvalidPropertiesFormatException;
    List<Manager> findAll();
    Optional<Manager> findById(Long id);
    List<Manager> findAllByName(String name);
    Manager updateManager(Long id, Manager putManager);
    void deleteAllManager();
    void updateCsvFilesOfManager(MultipartFile file);
    void updateOneManager(Long id,Manager manager);

    void deleteManager(Long id);

    void leaveAction(Long id, String leaveId, boolean action);
}
