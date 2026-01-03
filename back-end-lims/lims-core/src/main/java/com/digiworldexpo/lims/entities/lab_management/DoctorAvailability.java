package com.digiworldexpo.lims.entities.lab_management;

import java.sql.Timestamp;

import com.digiworldexpo.lims.constants.WeekDay;
import com.digiworldexpo.lims.entities.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "doctor_availability", schema = "lab")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAvailability extends BaseEntity {
	

	@Enumerated(EnumType.STRING)
	@Column(name = "week_day")
	private WeekDay weekDay;

	@Column(name = "start_time")
	private Timestamp startTime;

	@Column(name = "end_time")
	private Timestamp endTime;

	@Column(name = "is_available")
	private boolean isAvailable;

	@ManyToOne
	@JoinColumn(name = "doctor_master_id")
    @JsonBackReference // Prevent infinite recursion
	private DoctorMaster doctorMaster;
   
}


