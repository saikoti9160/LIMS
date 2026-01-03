package com.digiworldexpo.lims.master.model.response;


import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Setter
@Getter
public class ResponseModel<T> {

	private String statusCode;
	 
    private String message;
 
    private T data;
 
    private Integer totalCount;
    
    private Integer pageNumber;
    
    private Integer pageSize;
    
    private String sortedBy;
    
}
