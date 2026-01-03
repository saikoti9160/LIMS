package com.digiworldexpo.lims.master.service;

import java.util.List;

import java.util.UUID;

import com.digiworldexpo.lims.entities.master.SupportPriority;
import com.digiworldexpo.lims.master.model.response.ResponseModel;

public interface SupportPriorityService {

    ResponseModel<SupportPriority> saveSupportPriority(SupportPriority supportPriority);

    ResponseModel<List<SupportPriority>> getSupportPriorities(String startsWith, int pageNumber, int pageSize, String sortBy);

    ResponseModel<SupportPriority> getSupportPriorityById(UUID id);

    ResponseModel<SupportPriority> updateSupportPriority(UUID id, SupportPriority updatedSupportPriority);

    ResponseModel<SupportPriority> deleteSupportPriority(UUID id);

}

