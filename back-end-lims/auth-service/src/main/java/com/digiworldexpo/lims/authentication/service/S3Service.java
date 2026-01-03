package com.digiworldexpo.lims.authentication.service;

import org.springframework.web.multipart.MultipartFile;

import com.digiworldexpo.lims.authentication.model.ResponseModel;

public interface S3Service {

	public ResponseModel<String> uploadFile(String key, MultipartFile multipartFile);
}
