package com.digiworldexpo.lims.entities.lab_management;


import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.digiworldexpo.lims.entities.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "test_parameter", schema = "lab")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TestParameter extends BaseEntity {


	@Column(name = "test_parameter")
	private String testParameter;

	@Column(name = "measurement")
	private String measurement;

	@Column(name = "gender")
	private String gender;

	@Column(name = "age_group")
	private String ageGroup;

	@Column(name = "value_range")
	private String valueRange;

	@Column(name = "x_axis_label")
	private String xAxisLabel;

	@Column(name = "y_axis_label")
	private String yAxisLabel;

	 @JoinColumn(name="x_axis_values")
	 private List<Integer> xAxisValues;

	 @JoinColumn(name="y_axis_values")
	private List<Integer> yAxisValues;

	@Column(name = "generate_graph_button")
	private Boolean generateGraphButton;

	@Column(name = "result")
	private String result;

	@Column(name = "description")
	private String description;

	@Column(name = "lab_id")
	private UUID labId;

}
