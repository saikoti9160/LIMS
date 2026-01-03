package com.digiworldexpo.lims.entities.lab_management;

import com.digiworldexpo.lims.entities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "signature_master", schema = "lab")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignatureMaster extends BaseEntity {
	
	@Column(name = "signer_name")
	private String signerName;
	
	@Column(name = "upload_signature")
	private String uploadSignature;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lab_id", referencedColumnName = "id")
	private Lab lab;

}

