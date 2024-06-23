package com.javadeveloperzone.controller.AdminController;

import com.javadeveloperzone.models.AdminRelatedModels.Admin;
import com.javadeveloperzone.service.AdminService.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveAdmin(@RequestBody Admin admin){
        adminService.saveAdmin(admin);
        return ResponseEntity.ok("Admin Saved");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAdmin(String id){
        adminService.deleteAdmin(id);
        return ResponseEntity.ok("Admin Deleted");
    }
}
