package com.digiworldexpo.lims.entities.lab_management;

import java.sql.Timestamp;
import java.util.List;

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
@Table(name = "coupon_and_discount", schema = "lab")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CouponAndDiscountMaster extends BaseEntity {
    
    @Column(name = "start_date")
    private Timestamp startDate;
    
    @Column(name = "end_date")
    private Timestamp endDate;
    
    @Column(name = "coupon_name")
    private String couponName;
    
    @Column(name = "coupon_description")
    private String couponDescription;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id", referencedColumnName = "id")
    private DiscountMaster discountMaster;
    
    @Column(name = "discount_amount")
    private Double discountAmount;
    
    @Column(name = "discount_percentage")
    private String discountPercentage;
    
    @Column(name = "min_amount_required")
    private Double minAmountRequired;
    
    @Column(name = "max_amount_required")
    private Double maxAmountRequired;
    
    @Column(name = "gender")
    private String gender;
    
    @Column(name = "age_range")
    private Integer ageRange;
    
    @Column(name = "from_age")
    private Integer fromAge;
    
    @Column(name = "to_age")
    private Integer toAge;
    
    @Column(name = "visit_frequency")
    private List<String> visitFrequency;
    
    @Column(name = "specific_number")
    private Integer specificNumber;
    
    @Column(name = "others")
    private String others;
   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_id", referencedColumnName = "id")
    private Lab lab;
    
    @Column(name = "coupon_sequence_id")
    private String couponSequenceId;

}