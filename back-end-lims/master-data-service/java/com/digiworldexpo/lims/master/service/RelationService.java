package com.digiworldexpo.lims.master.service;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.master.Relation;
import com.digiworldexpo.lims.master.model.response.ResponseModel;

public interface RelationService {

	ResponseModel<Relation> addRelation(Relation relation, UUID createdBy);

	ResponseModel<List<Relation>> getAllRelations(String startsWith, int pageNumber, int pageSize, String sortedBy);

	ResponseModel<Relation> updateRelationById(UUID id, Relation relation, UUID modifiedBy);

	ResponseModel<Relation> getRelationById(UUID id);

	ResponseModel<Relation> deleteRelationById(UUID id);

}
