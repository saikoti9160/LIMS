package com.digiworldexpo.lims.lab.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentDetailsDTO {
	
	private List<Prepaid> prepaid;
    private List<PostPaid> postPaid;
}
