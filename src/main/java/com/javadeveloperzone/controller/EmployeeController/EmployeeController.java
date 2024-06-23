package com.javadeveloperzone.controller.EmployeeController;

import com.javadeveloperzone.models.EmployeeModels.Employee;
import com.javadeveloperzone.models.FolderModel.AttendanceModel;
import com.javadeveloperzone.models.FolderModel.LeaveRequest;
import com.javadeveloperzone.service.EmployeeService.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.InvalidPropertiesFormatException;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> addEmployee(@RequestBody Employee employee) throws  InvalidPropertiesFormatException {
        return ResponseEntity.ok(employeeService.save(employee));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getEmployee() {
        return ResponseEntity.ok(this.employeeService.findAll());
    }

    @PostAuthorize("returnObject.body.id == authentication.principal.id or hasAuthority('ADMIN') or returnObject.body.managerID == authentication.principal.id")
    @GetMapping("/{id}")
    public  ResponseEntity<?> getEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(this.employeeService.findById(id).get());
    }

    @PostAuthorize("returnObject.body.id == authentication.principal.id or hasAuthority('ADMIN') or returnObject.body.managerID == authentication.principal.id")
    @GetMapping( "/email/{email}")
    public ResponseEntity<?> getEmployeeByEmail(@PathVariable String email) {
        return ResponseEntity.ok(this.employeeService.findByEmail(email).get());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping( "/regex/search")
    public ResponseEntity<?> getEmployeeByRegexSearch(@RequestBody Employee employee) {
        return ResponseEntity.ok(this.employeeService.getEmployeeList(employee));
    }

    @PreAuthorize("#managerId == authentication.principal.id  or hasAuthority('ADMIN')")
    @GetMapping( "/list/{managerId}")
    public ResponseEntity<?> getEmployeeListByManager(@PathVariable Long managerId) {
        return ResponseEntity.ok(this.employeeService.findAllByManagerID(managerId));
    }

    @PreAuthorize("#id==authentication.principal.id or hasAuthority('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id,@RequestBody Employee putEmployee){

        return ResponseEntity.ok(this.employeeService.updateEmployee(id,putEmployee));
    }



    @PreAuthorize("#id==authentication.principal.id or hasAuthority('ADMIN')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateOneEmployee(@PathVariable Long id,@RequestBody Employee patchEmployee){
        this.employeeService.updateOneEmployee(id,patchEmployee);
        return ResponseEntity.ok("Data Updated");
    }

    @DeleteMapping("/delete/all")
    public void deleteAllEmployee() {
        this.employeeService.deleteAllEmployee();
    }

    @PreAuthorize("#id==authentication.principal.id or hasAuthority('ADMIN')")
    @PostMapping("/attendance")
    public ResponseEntity<?> markAttendance(@RequestBody AttendanceModel attendanceModel,@RequestParam(name = "id") Long id) {
        employeeService.markAttendance(attendanceModel,id);
        return ResponseEntity.ok("Updated Attendance");
    }

    @PreAuthorize("#id==authentication.principal.id or hasAuthority('ADMIN')")
    @PostMapping("/leave")
    public ResponseEntity<?> leaveRequest(@RequestBody LeaveRequest leaveRequest, @RequestParam(name = "id") Long id) {
        employeeService.leaveRequest(leaveRequest,id);
        return ResponseEntity.ok("Leave Request Sent to Manager");
    }
}
