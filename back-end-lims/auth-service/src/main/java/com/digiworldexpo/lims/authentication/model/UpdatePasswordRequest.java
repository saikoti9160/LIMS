package com.digiworldexpo.lims.authentication.model;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String email;
    private String password;
    private String newPassword;
    private String token;

}
