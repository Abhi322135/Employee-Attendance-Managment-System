package com.javadeveloperzone.models.AdminRelatedModels;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.javadeveloperzone.models.FolderModel.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@Document(collection = "admin")
public class Admin {

    @Id
    String id;
    @NonNull
    @Field(name = "Email ID")
    private String email;
    @NonNull
    @Field(name = "Password")

    private String password;

    private  Role role=Role.ADMIN;

    @NotNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    @NotNull
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonFormat
    public void setPassword(@NotNull String password) {
        this.password = password;
    }
    @JsonIgnore
    public Role getRole() {
        return role;
    }

    @JsonFormat
    public void setRole() {
        this.role = Role.ADMIN;
    }
}
