package com.digiworldexpo.lims.entities.master;

import com.digiworldexpo.lims.entities.BaseEntity;

import jakarta.persistence.CascadeType;
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
@Table(name = "user_master", schema = "masterdata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMaster extends BaseEntity{
	@Column(name = "user_name", nullable = false, unique = true)
    private String userName;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "phone_code", length = 5)
    private String phoneCode;

    @ManyToOne(fetch = FetchType.EAGER,cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    private Department department;

    @ManyToOne(fetch = FetchType.EAGER,cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    @Column(name = "position")
    private String position;

    @Column(name = "profile_picture", columnDefinition = "TEXT")
    private String profilePicture;

    @Column(name = "status", nullable = false)
    private String status; // ACTIVE or INACTIVE

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

}
