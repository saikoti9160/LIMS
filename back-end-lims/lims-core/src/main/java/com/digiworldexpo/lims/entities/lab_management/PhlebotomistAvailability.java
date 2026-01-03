package com.digiworldexpo.lims.entities.lab_management;

import java.sql.Timestamp;
import java.util.UUID;

import com.digiworldexpo.lims.constants.WeekDay;
import com.digiworldexpo.lims.entities.BaseEntity;

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
@Table(name = "phlebotomist_availability", schema = "lab")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PhlebotomistAvailability extends BaseEntity {
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
    @JoinColumn(name = "phlebotomist_id")
    private PhlebotomistMaster phlebotomistMaster;

    @Column(name = "created_by")
    private UUID createdBy;

    
    
}

