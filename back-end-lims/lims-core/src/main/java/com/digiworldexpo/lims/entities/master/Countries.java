package com.digiworldexpo.lims.entities.master;

import com.digiworldexpo.lims.entities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "countries", schema = "masterdata")
public class Countries extends BaseEntity{

	@Column(name = "continent_code")
	private String continentCode;
	
	@Column(name = "country_name")
	private String countryName;
	
	@Column(name = "country_code")
	private String countryCode;
	
	@Column(name = "phone_code")
	private String phoneCode;
	
	@Column(name = "currency")
	private String currency;
	
	@Column(name = "currency_symbol")
	private String currencySymbol;
	
	@Column(name = "continent_name")
	private String continentName; 
}
