package com.javadeveloperzone.service.AdminService;

import com.javadeveloperzone.constant.ErrorMessage;
import com.javadeveloperzone.models.AdminRelatedModels.Admin;
import com.javadeveloperzone.models.EmployeeModels.Employee;
import com.javadeveloperzone.models.ManagerRelatedModels.Manager;
import com.javadeveloperzone.repository.AdminRepository;
import com.javadeveloperzone.repository.EmployeeRepository;
import com.javadeveloperzone.repository.ManagerRepository;
import com.javadeveloperzone.utils.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService{

    private final AdminRepository adminRepository;
    private final ManagerRepository managerRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository, ManagerRepository managerRepository, EmployeeRepository employeeRepository
                           ,PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.managerRepository = managerRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveAdmin(Admin admin) {
        Optional<Admin> optionalAdmin=adminRepository.findByEmail(admin.getEmail());
        Optional<Manager> optionalManager=managerRepository.findByEmail(admin.getEmail());
        Optional<Employee> optionalEmployee=employeeRepository.findByEmail(admin.getEmail());
        if (optionalAdmin.isPresent() || optionalManager.isPresent() || optionalEmployee.isPresent()){
            ExceptionUtils.sendMessage(ErrorMessage.ADMIN_PRESENT);
        }
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
       adminRepository.save(admin);
    }

    @Override
    public void deleteAdmin(String id) {
        adminRepository.deleteById(id);
    }
}
