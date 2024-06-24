package com.javadeveloperzone.controller.ManagerController;

import com.javadeveloperzone.constant.APIMessages;
import com.javadeveloperzone.constant.LeaveActions;
import com.javadeveloperzone.models.ManagerRelatedModels.Manager;
import com.javadeveloperzone.service.ManagerService.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.InvalidPropertiesFormatException;

@RestController
@RequestMapping("/manager")
public class ManagerController {
    private final ManagerService managerService;
    @Autowired
    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> addManager(@RequestBody Manager manager) throws InvalidPropertiesFormatException {
        return ResponseEntity.ok(managerService.save(manager));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getManager() {
        return ResponseEntity.ok(this.managerService.findAll());
    }

    @PostAuthorize("returnObject.body.id == authentication.principal.id or hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getManager(@PathVariable Long id) {
        return ResponseEntity.ok(this.managerService.findById(id).get());
    }

    @PostAuthorize("#id==authentication.principal.id or hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateManager(@PathVariable Long id,@RequestBody Manager putManager){
        return ResponseEntity.ok(this.managerService.updateManager(id,putManager));
    }

    @PostAuthorize("#id==authentication.principal.id")
    @PostMapping("/leave/action")
    public ResponseEntity<?> approveOrRejectLeave(@RequestParam(name = "id") Long id,@RequestParam(name = "leaveId") String leaveId,
                                                  @RequestParam(name = "action") boolean action){
        this.managerService.leaveAction(id,leaveId,action);
        if (!action) return ResponseEntity.ok(LeaveActions.LEAVE_REJECT);
        return ResponseEntity.ok(LeaveActions.LEAVE_APPROVED);
    }

    @PostAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateOneManager(@PathVariable Long id,@RequestBody Manager patchManager){
        this.managerService.updateOneManager(id,patchManager);
        return ResponseEntity.ok(APIMessages.DATA_UPDATED);
    }

    @DeleteMapping("/delete/all")
    public void deleteAllEmployee() {
        this.managerService.deleteAllManager();
    }

    @DeleteMapping("/delete/{id}")
    public void deleteManager(@PathVariable(name = "id")Long id) {
        this.managerService.deleteManager(id);
    }

    @PostMapping(value = "/save/csv/files", consumes = "multipart/form-data")
    public void updateCsvFiles(@RequestParam("file") MultipartFile file)
    {
        this.managerService.updateCsvFilesOfManager(file);
    }
}
