package com.javadeveloperzone.service.ManagerService;

import com.javadeveloperzone.models.AdminRelatedModels.Admin;
import com.javadeveloperzone.models.EmployeeModels.Employee;
import com.javadeveloperzone.models.FolderModel.LeaveRequest;
import com.javadeveloperzone.models.FolderModel.LeaveStatus;
import com.javadeveloperzone.models.ManagerRelatedModels.Manager;
import com.javadeveloperzone.repository.AdminRepository;
import com.javadeveloperzone.repository.EmployeeRepository;
import com.javadeveloperzone.repository.LeaveRepo;
import com.javadeveloperzone.repository.ManagerRepository;
import com.javadeveloperzone.service.MailService.EmailService;
import com.javadeveloperzone.service.SequenceGeneratorService.ManagerDatabaseSequenceGenerator;
import com.javadeveloperzone.utils.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class ManagerServiceImpl implements ManagerService{
    private final ManagerRepository managerRepository;
    private final ManagerDatabaseSequenceGenerator sequenceGeneratorService;
    private final LeaveRepo leaveRepo;
    private final EmployeeRepository employeeRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;
    @Autowired
    public ManagerServiceImpl(ManagerRepository managerRepository, ManagerDatabaseSequenceGenerator sequenceGeneratorService,
                              LeaveRepo leaveRepo, EmployeeRepository employeeRepository, AdminRepository adminRepository,
                              PasswordEncoder passwordEncoder, EmailService emailService) {
        this.managerRepository = managerRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
        this.leaveRepo = leaveRepo;
        this.employeeRepository = employeeRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public Manager save(Manager manager) throws InvalidPropertiesFormatException {
        Optional<Manager> optionalManager=managerRepository.findByEmail(manager.getEmail());
        Optional<Employee> optionalEmployee=employeeRepository.findByEmail(manager.getEmail());
        Optional<Admin> optionalAdmin=adminRepository.findByEmail(manager.getEmail());
        if(optionalManager.isPresent() || optionalEmployee.isPresent() || optionalAdmin.isPresent()) {
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,"Please Enter Unique email address");
        }

        manager.setId(sequenceGeneratorService.generateSequence(Manager.SEQUENCE_NAME));
        manager.setPassword(passwordEncoder.encode((manager.getPassword())));
        return  managerRepository.save(manager);
    }

    @Override
    public List<Manager> findAll() {
        return managerRepository.findAll();
    }

    @Override
    public Optional<Manager> findById(Long id) {
        return managerRepository.findById(id);
    }

    @Override
    public List<Manager> findAllByName(String name) {
        return managerRepository.findAllByName(name);
    }

    @Override
    public Manager updateManager(Long id, Manager putManager) {
        Optional<Manager> list=managerRepository.findById(id);
        Manager existingManager=list.orElse(null);
        if (list.isEmpty()){
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,"Manager Name not found");
        }
        if (putManager.getId()!=null){
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,"ID Updation not allowed");
        }
        existingManager.setSkill(putManager.getSkill());
        existingManager.setEmail(putManager.getEmail());
        existingManager.setOrganization(putManager.getOrganization());
        existingManager.setName(putManager.getName());
        existingManager.setId(id);
        managerRepository.save(existingManager);
        return existingManager;
    }

    @Override
    public void deleteAllManager() {
        managerRepository.deleteAll();
    }

    @Override
    public void updateCsvFilesOfManager(MultipartFile file) {
        BufferedReader br;
        List<Manager> result = new ArrayList<>();
        try {
            String line;
            InputStream is = file.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            boolean skip=true;
            while ((line = br.readLine()) != null) {
                if(!skip) {
                    String[] data = line.split(",");
                    Manager man = new Manager();
                    man.setName(data[0]);
                    man.setEmail(data[1]);
                    man.setOrganization(data[2]);
                    man.setSkill(Collections.singletonList(data[3]));
                    man.setId(sequenceGeneratorService.generateSequence(Manager.SEQUENCE_NAME));
                }
                skip=false;
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        managerRepository.saveAll(result);
    }

    @Override
    public void updateOneManager(Long id, Manager manager) {
        Optional<Manager> list=managerRepository.findById(id);
        if (list.isEmpty()){
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,"Manager Name not found");
        }
        if (manager.getId()!=null){
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,"ID Updation is not allowed");
        }
        if (manager.getName()!=null){
            list.get().setName(manager.getName());
        }
        if (manager.getEmail()!=null){
            list.get().setEmail(manager.getEmail());
        }
        if (manager.getOrganization()!=null){
            list.get().setOrganization(manager.getOrganization());
        }
        if (manager.getSkill()!=null){
            list.get().setSkill(manager.getSkill());
        }
        managerRepository.save(list.get());
    }

    @Override
    public void deleteManager(Long id) {
        try {
            managerRepository.deleteById(id);
        } catch (Exception e) {
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,"Manager id is not present");
        }
        List<Employee> employees=employeeRepository.findAllByManagerID(id);
        employees.forEach(employeeRepository::delete);
    }

    @Override
    public void leaveAction(Long id, String leaveId, boolean action) {
        Optional<LeaveRequest> getLeaveRequest=leaveRepo.findById(leaveId);

        if (getLeaveRequest.isEmpty()){
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,"Leave Request is not found");
        }
        if (getLeaveRequest.get().getLeaveStatus()==LeaveStatus.APPROVED){
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,"Leave Request is already approved");
        }
        Optional<Employee> employee=employeeRepository.findById(getLeaveRequest.get().getEmployeeId());
        if (action){
            getLeaveRequest.get().setLeaveStatus(LeaveStatus.APPROVED);
            emailService.sendSimpleMessage(employee.get().getEmail(),"LEAVE Action","Your leave is approved");
            leaveRepo.save(getLeaveRequest.get());
        }
        else {
            getLeaveRequest.get().setLeaveStatus(LeaveStatus.REJECTED);
            emailService.sendSimpleMessage(employee.get().getEmail(),"LEAVE Action","Your leave is rejected");
            leaveRepo.deleteById(leaveId);
        }
    }

}
