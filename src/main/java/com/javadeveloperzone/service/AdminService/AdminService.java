package com.javadeveloperzone.service.AdminService;

import com.javadeveloperzone.models.AdminRelatedModels.Admin;

public interface AdminService {
    void saveAdmin(Admin admin);

    void deleteAdmin(String id);
}
