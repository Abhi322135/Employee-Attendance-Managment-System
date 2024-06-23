package com.javadeveloperzone.service.EmployeeService;

import com.javadeveloperzone.models.EmployeeModels.Employee;
import com.javadeveloperzone.models.FolderModel.AttendanceModel;
import com.javadeveloperzone.models.FolderModel.LeaveRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    Employee save(Employee employee) throws InvalidPropertiesFormatException;
    List<Employee> findAll();
    Optional<Employee> findById(Long id);
    Optional<Employee> findByEmail(String email);
    Employee updateEmployee(Long id, Employee putEmployee);
     void deleteAllEmployee();
    void updateCsvFilesOfEmployee(MultipartFile file);
    void updateOneEmployee(Long id,Employee patchEmployee);

    List<Employee> findAllByManagerID(Long id);

    List<Employee> getEmployeeList(Employee employee);

    void markAttendance(AttendanceModel attendanceModel, Long id);

    void leaveRequest(LeaveRequest leaveRequest, Long id);
}
