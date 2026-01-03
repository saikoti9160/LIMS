package com.digiworldexpo.lims.entities.lab_management;

import com.digiworldexpo.lims.entities.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bill_access_configuration" , schema = "lab")
@Getter
@Setter
public class BillAccessConfiguration extends BaseEntity {
	
	 @Column(name = "show_bill_header")
	    private Boolean showHeader;

	    @Column(name = "show_bill_footer")
	    private Boolean showFooter;

	    @Column(name = "bill_header_text")
	    private String headerText;  // Accept formatted text for the header

	    @Column(name = "bill_footer_text")
	    private String footerText;  // Accept formatted text for the footer
	    
	    @OneToOne
	    @JoinColumn(name = "organization_id")
	    @JsonBackReference
	    private Organization organization;
    
}