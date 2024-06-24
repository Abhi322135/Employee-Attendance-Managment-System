package com.javadeveloperzone.component.JwtComponent;

import com.javadeveloperzone.constant.MailTemplate;
import com.javadeveloperzone.models.EmployeeModels.Employee;
import com.javadeveloperzone.models.FolderModel.AttendanceModel;
import com.javadeveloperzone.models.FolderModel.LeaveRequest;
import com.javadeveloperzone.models.FolderModel.LeaveStatus;
import com.javadeveloperzone.repository.AttendanceRepo;
import com.javadeveloperzone.repository.EmployeeRepository;
import com.javadeveloperzone.repository.LeaveRepo;
import com.javadeveloperzone.service.MailService.EmailService;
import com.javadeveloperzone.utils.DateUtils;
import com.javadeveloperzone.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AttendanceReminderConfiguration {

    private final LeaveRepo leaveRepo;
    private final AttendanceRepo attendanceRepo;
    private final EmployeeRepository employeeRepository;

    private final String date= DateUtils.parseTodaysDate();
    private final EmailService emailService;

    @Autowired
    public AttendanceReminderConfiguration(LeaveRepo leaveRepo, AttendanceRepo attendanceRepo, EmployeeRepository employeeRepository,
                                           EmailService emailService) {
        this.leaveRepo = leaveRepo;
        this.attendanceRepo = attendanceRepo;
        this.employeeRepository = employeeRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 21 * * MON-FRI") // Fire at 9:00 PM (21:00) from Monday to Friday
    public void scheduleTask() {
        List<AttendanceModel> attendanceRepoList=attendanceRepo.findAllByDate(date);
        List<Employee> employees=collectAllAbsentEmployee(attendanceRepoList);
        employees.stream().filter(this::isOnLeave).forEach((e)->emailService.
                sendSimpleMessage("ATTENDANCE", "<h>You were not on leave and did not login so unpaid leave</h>",e.getEmail()));
    }

    @Scheduled(cron = "0 0 20 * * MON-FRI") // Fire at 9:00 PM (21:00) from Monday to Friday
    public void warningMail() {
        List<AttendanceModel> attendanceRepoList=attendanceRepo.findAllByDate(date);
        List<Employee> employees=collectAllAbsentEmployee(attendanceRepoList);
        employees.stream().filter(this::isOnLeave)
                .forEach( (e)-> emailService.sendSimpleMessage("ATTENDANCE", MailTemplate.LOGIN_REMAINDER_TEMPLATE,e.getEmail()));
    }

    @Scheduled(cron = "0 0 20 * * MON-FRI") // Fire at 8:00 PM (20:00) from Monday to Friday
    public void checkOutAllPresentEmployee() {
        List<AttendanceModel> attendanceRepoList=attendanceRepo.findAllByDate(date);
        attendanceRepoList.stream().filter(AttendanceModel::getPresent).forEach((e)->{
            e.setTimeout(TimeUtils.formatTime(LocalTime.now()));
            attendanceRepo.save(e);
        });
    }

    @Scheduled(cron = "0 0 0 * * MON-FRI") // Fire at 12:00 AM (00:00) from Monday to Friday
    public void markAllAsAbsent() {
        List<AttendanceModel> attendanceRepoList;
        List<Employee> employees=employeeRepository.findAll();
        attendanceRepoList=employees.stream().map((e)->mapAttendance(e,date)).collect(Collectors.toList());
        attendanceRepo.saveAll(attendanceRepoList);
    }

    private AttendanceModel mapAttendance(Employee e,String date) {
        AttendanceModel attendanceModel=new AttendanceModel();
        attendanceModel.setPresent(false);
        attendanceModel.setDate(date);
        attendanceModel.setEmployeeId(e.getId());
        attendanceModel.setManagerId(e.getManagerID());
        attendanceModel.setTimeout(null);
        attendanceModel.setTimein(null);
        return attendanceModel;
    }

    private List<Employee> collectAllAbsentEmployee(List<AttendanceModel> attendanceRepoList) {
        return  attendanceRepoList.stream().filter(this::getEmployeeAbsentId).map(this::mapEmployee).collect(Collectors.toList());
    }

    private Employee mapEmployee(AttendanceModel e) {
        return employeeRepository.findById(e.getEmployeeId()).get();
    }

    private Boolean getEmployeeAbsentId(AttendanceModel x) {
        return !attendanceRepo.findById(x.getId()).get().getPresent();
    }
    private boolean isOnLeave(Employee e) {
        List<LeaveRequest> list=leaveRepo.findAllByEmployeeId(e.getId());
        return list.stream().noneMatch((l)->l.getLeaveStatus()== LeaveStatus.APPROVED &&
                DateUtils.compareDates(l.getStartDate(),date,true) && DateUtils.compareDates(date,l.getEndDate(),true));
    }
}
