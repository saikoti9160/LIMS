package com.digiworldexpo.lims.lab.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResponseModel<T> {

	private String statusCode;
	 
    private String message;
 
    private T data;
 
    private Integer totalCount;
    
    private Integer pageNumber;
    
    private Integer pageSize;
    
    private String sortedBy;
    
   
}
