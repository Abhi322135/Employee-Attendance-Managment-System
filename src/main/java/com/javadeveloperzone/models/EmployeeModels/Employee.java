package com.javadeveloperzone.models.EmployeeModels;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.javadeveloperzone.models.FolderModel.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
@Data
@NoArgsConstructor
@Document(collection = "employee")
public class Employee{
    @Id
    private Long id;

    @Field(name = "Name")
    private String name;

    @Transient
    public static final String SEQUENCE_NAME = "employees_sequence";

    @Field(name = "Manager ID")
    private Long managerID;
    @NonNull
    private String organization;
    @NonNull
    private String email;
    private List<String> skill;
    private String password;
    private Role role=Role.EMPLOYEE;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getSkill() {
        return skill;
    }

    public void setSkill(List<String> skill) {
        this.skill = skill;
    }

    public Long getManagerID() {
        return managerID;
    }

    public void setManagerID(Long managerID) {
        this.managerID = managerID;
    }

    public Role getRole() {
        return role;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole() {

        this.role = Role.EMPLOYEE;
    }
}