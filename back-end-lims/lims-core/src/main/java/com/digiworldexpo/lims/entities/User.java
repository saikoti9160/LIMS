package com.digiworldexpo.lims.entities;

import com.digiworldexpo.lims.entities.lab_management.Lab;
import com.digiworldexpo.lims.entities.master.Account;
import com.digiworldexpo.lims.entities.master.Department;
import com.digiworldexpo.lims.entities.master.Designation;
import com.digiworldexpo.lims.entities.master.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user", schema = "identity")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "email")
	private String email;

	@Column(name = "phone_code")
	private String phoneCode;

	@Column(name = "phone")
	private String phone;

	@Column(name = "password")
	private String password;

	@Column(name = "country")
	private String country;

	@Column(name = "state")
	private String state;

	@Column(name = "city")
	private String city;

	@Column(name = "address")
	private String address;

	@Column(name = "is_email_verified")
	private boolean isEmailVerified;

	@Column(name = "is_first_login")
	private boolean isFirstLogin = true;

	@Column(name = "is_auto_generate_password")
	private boolean isAutoGeneratePassword;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

	@ManyToOne
	@JoinColumn(name = "account")
	private Account account;
	
	@ManyToOne
	@JoinColumn(name = "department_id")
	private Department department;
	
	@Column(name = "date_of_birth")
	private String dateOfBirth;
	
	@Column(name = "position")
	private String position;
	
	@Column(name = "profile_pic")
	private String profilePic;
	
	@Column(name = "status")
	private String status;
	
	@ManyToOne
	@JoinColumn(name = "designation")
	private Designation designation;

	@ManyToOne
	@JoinColumn(name = "lab_id",referencedColumnName = "id")
	private Lab lab;

	@Column(name = "lab_name")
	private String labName;
	
	@Column(name="user_sequence_id")
    private String userSequenceId;

}
