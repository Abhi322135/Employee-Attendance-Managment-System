# Employee Attendance Management System

## Overview

The **Employee Attendance Management System** is a comprehensive solution designed to simplify and automate the process of tracking employee attendance. Built with the robust Spring Boot framework for backend services and MongoDB for data storage, this system provides a reliable and scalable platform for managing attendance data.

## Core Features

### For Employees
- **View Attendance Records**: Employees can access their attendance history, which includes details such as dates of attendance and any noted discrepancies.
- **Leave Management**: Employees can apply for leave, which is sent to their manager for approval or rejection, streamlining the leave application process.

### For Managers
- **Employee Attendance Tracking**: Managers have access to the attendance records of their team members. This allows them to monitor attendance patterns, identify potential issues, and ensure accurate record-keeping.
- **Approve/Reject Leave Requests**: Managers can review leave requests submitted by their employees and make decisions to approve or reject them. Managers can also provide feedback or reasons for any rejection.
- **Attendance Reminder**: The system automatically sends email reminders to employees who have not recorded their attendance for the day and are not on approved leave. This helps maintain up-to-date and accurate attendance records.

### Security
- **JWT Authentication**: The system uses JSON Web Tokens (JWT) to secure authentication processes. This ensures that only authorized users can access certain features and endpoints.
- **Pre and Post Authorization**: Spring Security annotations (@PreAuthorize and @PostAuthorize) are used to enforce access control policies. These annotations help in securing methods and ensuring that users have the necessary permissions to perform certain actions.

### Additional Features
- **Email Notifications**: The system is configured to send email notifications to employees who have not filled in their attendance and were not on approved leave. This functionality helps ensure that employees are reminded to complete their attendance records promptly.

## Technical Implementation

### Security Implementation
- **JWT Token**:
  - JWT tokens are issued during the login process and must be included in the header of subsequent requests for authentication and authorization.
  - Example implementation:
    ```java
    @PostMapping("/authenticate/user")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response,HttpServletRequest request) {
        return ResponseEntity.ok(userService.authenticateUser(authenticationRequest,response,request));
    }
    ```

- **Pre and Post Authorization**:
  - Use @PreAuthorize and @PostAuthorize annotations in your service methods to enforce security policies.
  - Example:
    ```java
        @PreAuthorize("#id==authentication.principal.id or hasAuthority('ADMIN')")
    @PostMapping("/attendance")
    public ResponseEntity<?> markAttendance(@RequestBody AttendanceModel attendanceModel,@RequestParam(name = "id") Long id) {
        //Code implementation
    }

    @PreAuthorize("#id==authentication.principal.id or hasAuthority('ADMIN')")
    @PostMapping("/leave")
    public ResponseEntity<?> leaveRequest(@RequestBody LeaveRequest leaveRequest, @RequestParam(name = "id") Long id) {
        //implementation
    }

    @PostAuthorize("#id==authentication.principal.id")
    @PostMapping("/leave/action")
    public ResponseEntity<?> approveOrRejectLeave(@RequestParam(name = "id") Long id,@RequestParam(name = "leaveId") String leaveId,
                                                  @RequestParam(name = "action") boolean action){
        this.managerService.leaveAction(id,leaveId,action);
        if (!action) return ResponseEntity.ok("Leave Rejected");
        return ResponseEntity.ok("Leave Approved");
    }
    
    ```

### Email Notification
- **Sending Email**:
  - Integrate with a mailing service (e.g., JavaMailSender) to send emails.
  - Example:
    ```java
    @Service
    public class EmailService {
        @Autowired
        private JavaMailSender mailSender;

        public void sendAttendanceReminder(String toEmail) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Attendance Reminder");
            message.setText("Please fill in your attendance for today.");
            mailSender.send(message);
        }
    }
    ```
- **Scheduled Task**:
  - Use @Scheduled to run a task daily that checks for employees who haven’t filled in their attendance and sends reminders.
  - Example:
    ```java
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
        employees.stream().filter(this::isOnLeave).forEach((e)->emailService.sendSimpleMessage(e.getEmail(),"ATTENDANCE","You have not filled attendance field your leave is detected"));
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
    ```

### Project Structure

```bash
employee-attendance-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/example/attendance/
│   │   │   ├── controller/           # REST controllers
│   │   │   ├── model/                # Data models
│   │   │   ├── repository/           # MongoDB repositories
│   │   │   ├── service/              # Service layer
│   │   │   ├── config/               # Configuration classes, including security
│   │   │   └── util/                 # Utility classes (e.g., JWT utility)
│   │   ├── resources/
│   │   │   ├── application.properties # Application configuration
│   │   │   └── templates/            # Email templates
│   └── test/                         # Test cases
└── pom.xml                           # Maven dependencies
