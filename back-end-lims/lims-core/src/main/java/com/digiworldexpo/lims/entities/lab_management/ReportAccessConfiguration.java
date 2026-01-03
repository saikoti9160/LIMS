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
@Table(name = "report_access_configuration", schema = "lab")
@Getter
@Setter
public class ReportAccessConfiguration extends BaseEntity {
	
	 @Column(name = "show_report_header")
	    private Boolean showHeader;

	    @Column(name = "show_report_footer")
	    private Boolean showFooter;

	    @Column(name = "bill_header_text")
	    private String headerText;  

	    @Column(name = "bill_footer_text")
	    private String footerText;  
	    
	    @OneToOne
	    @JoinColumn(name = "organization_id")
	    @JsonBackReference
	    private Organization organization;
    
}
