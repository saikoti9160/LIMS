package com.digiworldexpo.lims.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseModel<T> {
	
	private String statusCode;
	private String message;
	private T data;
	private String timestamp;
	
	private Integer totalCount;
    private Integer pageNumber;
    private Integer pageSize;
    private String sortedBy;

}
