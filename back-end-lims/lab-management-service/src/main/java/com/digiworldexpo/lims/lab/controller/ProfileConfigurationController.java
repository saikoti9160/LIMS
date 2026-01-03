package com.digiworldexpo.lims.lab.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.request.ProfileConfigurationRequestDTO;
import com.digiworldexpo.lims.lab.response.ProfileConfigurationResponseDTO;
import com.digiworldexpo.lims.lab.service.ProfileConfigurationService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/profile-configuration")
public class ProfileConfigurationController {

    private final ProfileConfigurationService profileConfigurationService;
    private final HttpStatusCode httpStatusCode;

    public ProfileConfigurationController(ProfileConfigurationService profileConfigurationService, HttpStatusCode httpStatusCode) {
        this.profileConfigurationService = profileConfigurationService;
        this.httpStatusCode = httpStatusCode;
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseModel<ProfileConfigurationResponseDTO>> saveProfileConfiguration(
            @RequestHeader("createdBy") UUID createdBy,
            @RequestBody ProfileConfigurationRequestDTO requestDTO) {
        log.info("Begin ProfileConfiguration Controller -> save() method");
        ResponseModel<ProfileConfigurationResponseDTO> save = profileConfigurationService.saveProfileConfiguration(createdBy, requestDTO);
        log.info("End ProfileConfiguration Controller -> save() method");
        HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(save.getStatusCode());
        return ResponseEntity.status(httpStatus).body(save);
    }
}
