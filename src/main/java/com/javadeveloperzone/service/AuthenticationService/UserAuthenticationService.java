package com.javadeveloperzone.service.AuthenticationService;

import com.javadeveloperzone.models.AdminRelatedModels.Admin;
import com.javadeveloperzone.models.EmployeeModels.Employee;
import com.javadeveloperzone.models.FolderModel.Role;
import com.javadeveloperzone.models.ManagerRelatedModels.Manager;
import com.javadeveloperzone.repository.AdminRepository;
import com.javadeveloperzone.repository.EmployeeRepository;
import com.javadeveloperzone.repository.ManagerRepository;
import com.javadeveloperzone.utils.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserAuthenticationService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final ManagerRepository managerRepository;

    private final AdminRepository adminRepository;

    @Autowired
    UserAuthenticationService(EmployeeRepository employeeRepository, ManagerRepository managerRepository, AdminRepository adminRepository){
         this.employeeRepository=employeeRepository;
         this.managerRepository=managerRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Employee> employee=employeeRepository.findByEmail(email);
        Optional<Manager> manager=managerRepository.findByEmail(email);
        Optional<Admin> admin=adminRepository.findByEmail(email);
        List<SimpleGrantedAuthority> roles;
        Role role = null;
        if (employee.isPresent()){
            role=employee.get().getRole();
        } else if (manager.isPresent()) {
            role=manager.get().getRole();
        } else if (admin.isPresent()) {
            role=admin.get().getRole();
        } else ExceptionUtils.sendMessage(HttpStatus.NOT_FOUND,"Employee or Manager not found with email: " + email);
        if(role.equals(Role.EMPLOYEE) && employee.isPresent())
        {
            roles = List.of(new SimpleGrantedAuthority("EMPLOYEE"));
            return new UserExtend(new User(employee.get().getEmail(), employee.get().getPassword(),roles),employee.get().getId());
        }
        else if(role.equals(Role.MANAGER) && manager.isPresent())
        {
            roles = List.of(new SimpleGrantedAuthority("MANAGER"));
            return new UserExtend(new User(manager.get().getEmail(), manager.get().getPassword(),roles),manager.get().getId());
        }
        else if(role.equals(Role.ADMIN) && admin.isPresent()){
            roles = List.of(new SimpleGrantedAuthority("ADMIN"));
            return new UserExtend(new User(admin.get().getEmail(), admin.get().getPassword(),roles),12L);
        }
        return null;
    }
}
