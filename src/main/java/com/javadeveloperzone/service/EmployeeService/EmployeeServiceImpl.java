package com.javadeveloperzone.service.EmployeeService;

import com.javadeveloperzone.constant.BooleanFlag;
import com.javadeveloperzone.constant.EmployeeConstants;
import com.javadeveloperzone.constant.ErrorMessage;
import com.javadeveloperzone.constant.MailMessages;
import com.javadeveloperzone.models.AdminRelatedModels.Admin;
import com.javadeveloperzone.models.EmployeeModels.Employee;
import com.javadeveloperzone.models.FolderModel.AttendanceModel;
import com.javadeveloperzone.models.FolderModel.LeaveRequest;
import com.javadeveloperzone.models.FolderModel.LeaveStatus;
import com.javadeveloperzone.models.FolderModel.Weekends;
import com.javadeveloperzone.models.ManagerRelatedModels.Manager;
import com.javadeveloperzone.repository.*;
import com.javadeveloperzone.service.MailService.EmailService;
import com.javadeveloperzone.service.SequenceGeneratorService.EmployeeSequenceGeneratorService;
import com.javadeveloperzone.utils.DateUtils;
import com.javadeveloperzone.utils.ExceptionUtils;
import com.javadeveloperzone.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;


@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final Query query;
    private final EmployeeRepository employeeRepository;

    private final EmployeeSequenceGeneratorService sequenceGeneratorService;

    private final ManagerRepository managerRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AttendanceRepo attendanceRepo;
    private final LeaveRepo leaveRepo;
    private final MongoTemplate mongoTemplate;

    private final EmailService emailService;

    @Autowired
    public EmployeeServiceImpl(Query query, EmployeeRepository employeeRepository, EmployeeSequenceGeneratorService sequenceGeneratorService,
                               ManagerRepository managerRepository, AdminRepository adminRepository, PasswordEncoder passwordEncoder,
                               AttendanceRepo attendanceRepo, LeaveRepo leaveRepo, MongoTemplate mongoTemplate, EmailService emailService) {
        this.query = query;
        this.employeeRepository = employeeRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
        this.managerRepository = managerRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.attendanceRepo = attendanceRepo;
        this.leaveRepo = leaveRepo;
        this.mongoTemplate = mongoTemplate;
        this.emailService = emailService;
    }

    @Override
    public Employee save(Employee employee)  {
           Optional<Employee> list=employeeRepository.findByEmail(employee.getEmail());
           Optional<Manager> managerOptional=managerRepository.findByEmail(employee.getEmail());
           Optional<Admin> admin=adminRepository.findByEmail(employee.getEmail());
           if(list.isPresent() || managerOptional.isPresent() || admin.isPresent()) {
               ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,ErrorMessage.UNIQUE_EMAIL);
           }
           boolean managerExist= managerRepository.findById(employee.getManagerID()).isPresent();
           if (!managerExist){
               ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,ErrorMessage.MANAGER_NOT_FOUND);
           }
           employee.setId(sequenceGeneratorService.generateSequence(Employee.SEQUENCE_NAME));
           employee.setPassword(passwordEncoder.encode(employee.getPassword()));
           return  employeeRepository.save(employee);
       }
    @Override
    public List<Employee> findAll()
    {
        return employeeRepository.findAll(Sort.by(Sort.Direction.DESC,("CreatedDate")));
    }
    @Override
    public Optional<Employee> findById(Long Id) {
        return employeeRepository.findById(Id);
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        Optional<Employee> employee=employeeRepository.findByEmail(email);
        if (employee.isEmpty()){
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,ErrorMessage.EMPLOYEE_NOT_FOUND);
        }
          return employeeRepository.findByEmail(email);
    }

    @Override
    public Employee updateEmployee(Long id,Employee putEmployee) {
        Optional<Employee> list=employeeRepository.findById(id);
        Employee existingEmployee=list.orElse(null);
        if (list.isEmpty()){
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,ErrorMessage.EMPLOYEE_NOT_FOUND);
        }
        if (!managerRepository.existsById(putEmployee.getManagerID())){
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST, ErrorMessage.MANAGER_NOT_FOUND);
        }
        existingEmployee.setManagerID(putEmployee.getManagerID());
        existingEmployee.setSkill(putEmployee.getSkill());
        existingEmployee.setEmail(putEmployee.getEmail());
        existingEmployee.setOrganization(putEmployee.getOrganization());
        existingEmployee.setName(putEmployee.getName());
        existingEmployee.setId(id);

        employeeRepository.save(existingEmployee);
        return existingEmployee;
    }
    @Override
    public void deleteAllEmployee()
    {
        employeeRepository.deleteAll();
    }
    public void updateCsvFilesOfEmployee(MultipartFile file){
        BufferedReader br;
        List<Employee> result = new ArrayList<>();
        try {
            String line;
            InputStream is = file.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            boolean skip=true;
            while ((line = br.readLine()) != null) {
                if(!skip) {
                    String[] data = line.split(",");
                    Employee emp = new Employee();
                    emp.setName(data[0]);
                    emp.setEmail(data[1]);
                    emp.setOrganization(data[2]);
                    emp.setSkill(Collections.singletonList(data[3]));
                    emp.setId(sequenceGeneratorService.generateSequence(Employee.SEQUENCE_NAME));
                    result.add(emp);
                }
                skip=false;
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

           employeeRepository.saveAll(result);    }

    @Override
    public void updateOneEmployee(Long id,Employee patchEmployee) {
        Optional<Employee> list=employeeRepository.findById(id);
        if (list.isEmpty()){
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,ErrorMessage.EMPLOYEE_NOT_FOUND);
        }
        if (patchEmployee.getName()!=null){
            list.get().setName(patchEmployee.getName());
        }
        if (patchEmployee.getEmail()!=null){
            list.get().setEmail(patchEmployee.getEmail());
        }
        if (patchEmployee.getOrganization()!=null){
            list.get().setOrganization(patchEmployee.getOrganization());
        }
        if (patchEmployee.getSkill()!=null){
            list.get().setSkill(patchEmployee.getSkill());
        }
        if(patchEmployee.getManagerID()!=null && managerRepository.existsById(patchEmployee.getManagerID())){
            list.get().setManagerID(patchEmployee.getManagerID());
        }
        else ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,ErrorMessage.MANAGER_NOT_FOUND);
        employeeRepository.save(list.get());
    }

    @Override
    public List<Employee> findAllByManagerID(Long id) {
        return employeeRepository.findAllByManagerID(id);
    }

    @Override
    public List<Employee> getEmployeeList(Employee employee) {
        final List<String> fields=new ArrayList<>();
        final List<String> value=new ArrayList<>();
        if (employee.getEmail()!=null){
            fields.add("email");
            value.add(employee.getEmail());
        }
        if (employee.getName()!=null){
            fields.add("Name");
            value.add(employee.getName());
        }
        if (employee.getOrganization()!=null){
            fields.add("organization");
            value.add(employee.getOrganization());
        }

        List<Criteria> orCriteria = new ArrayList<>();
        int cnt=0;
        for (String field : fields) {
            String regex=value.get(cnt);
            Pattern pattern=Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            orCriteria.add(Criteria.where(field).regex(pattern));
            cnt++;
        }
        query.addCriteria(new Criteria().orOperator(orCriteria.toArray(new Criteria[0])));
        return mongoTemplate.find(query, Employee.class);

    }

    @Override
    public void markAttendance(AttendanceModel attendanceModel, Long id) {
        String today =DateUtils.getDayOfTheWeek(DateUtils.parseTodaysDate());

        if (today.equalsIgnoreCase(Weekends.SATURDAY.name()) || today.equalsIgnoreCase(Weekends.SUNDAY.name())){
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,ErrorMessage.TODAY_SATURDAY_SUNDAY);
        }
        attendanceModel.setDate(DateUtils.parseTodaysDate());
        attendanceModel.setPresent(BooleanFlag.TRUE);
        Optional<Employee> employee=employeeRepository.findById(id);

        if (employee.isEmpty()){
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,ErrorMessage.EMPLOYEE_NOT_FOUND);
        }
        if (attendanceModel.getManagerId()!=employee.get().getManagerID()){
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,ErrorMessage.MANAGER_NOT_FOUND);
        }
        if(checkLeave(attendanceModel.getDate())){
            ExceptionUtils.sendMessage(EmployeeConstants.TODAY_ON_LEAVE);
        }
        if (DateUtils.getHours()>=20){
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,ErrorMessage.CHECK_IN_CHECK_OUT_OVER);
        }
        List<AttendanceModel> attendanceModel1=attendanceRepo.findAllByDate(DateUtils.parseTodaysDate()        );
        Optional<AttendanceModel> attendance=attendanceModel1.stream().filter((x)-> x.getEmployeeId().equals(id)).findFirst();
        if (attendance.isPresent() && attendance.get().getPresent()){
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,ErrorMessage.ALREADY_PRESENT);
        }
        else if(attendance.isPresent() && !attendance.get().getPresent()){
            AttendanceModel model=attendance.get();
            model.setPresent(BooleanFlag.TRUE);
            model.setTimein(TimeUtils.formatTime(LocalTime.now()));
            attendanceRepo.save(model);
        }
        else
        attendanceRepo.save(attendanceModel);
    }

    @Override
    public void leaveRequest(LeaveRequest leaveRequest, Long id) {
        Optional<Employee> employee=employeeRepository.findById(id);
        Optional<Manager> manager=managerRepository.findById(leaveRequest.getManagerId());
        if (employee.isEmpty()){
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,ErrorMessage.EMPLOYEE_NOT_FOUND);
        }
        if (leaveRequest.getManagerId()!=employee.get().getManagerID()){
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,ErrorMessage.MANAGER_NOT_FOUND);
        }
        if(DateUtils.compareDates(leaveRequest.getEndDate(),leaveRequest.getStartDate(), BooleanFlag.FALSE)){
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,ErrorMessage.END_DATE_BEFORE_START_DATE);
        }
        if (DateUtils.compareDates(leaveRequest.getStartDate(),DateUtils.parseTodaysDate(),BooleanFlag.TRUE)){
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,ErrorMessage.START_DATE_ERROR);
        }
        if (checkLeave(leaveRequest.getEndDate()) || checkLeave(leaveRequest.getStartDate())){
            ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,ErrorMessage.OVERLAP_LEAVE);
        }
        leaveRequest.setLeaveStatus(LeaveStatus.PENDING);
        emailService.sendSimpleMessage(MailMessages.LEAVE_APPROVAL,employee.get().getName()+" applied for leave",manager.get().getEmail());
        leaveRepo.save(leaveRequest);
    }

    @Override
    public void checkout(Long id) {
        AttendanceModel attendanceModel;

         if (attendanceRepo.findByDateAndId(DateUtils.parseTodaysDate(),id).isPresent()) {
             attendanceModel=attendanceRepo.findByDateAndId(DateUtils.parseTodaysDate(),id).get();
             attendanceModel.setTimeout(TimeUtils.formatTime(LocalTime.now()));
             attendanceRepo.save(attendanceModel);
         }
         else ExceptionUtils.sendMessage(HttpStatus.BAD_REQUEST,ErrorMessage.ATTENDANCE_ID_NOT_EXIST);
    }

    private boolean checkLeave(String date) {
        List<LeaveRequest> list=leaveRepo.findAll();
        return list.stream().anyMatch((leaveRequest) -> DateUtils.compareDates(leaveRequest.getStartDate(),date,BooleanFlag.TRUE) &&
                DateUtils.compareDates(date,leaveRequest.getEndDate(),BooleanFlag.TRUE) &&
                leaveRequest.getLeaveStatus().equals(LeaveStatus.APPROVED));
    }

}
