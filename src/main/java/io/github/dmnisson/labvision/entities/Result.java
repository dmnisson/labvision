package io.github.dmnisson.labvision.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity( name="Result" )
public class Result extends VariableValue<ResultComputation, Result> implements LabVisionEntity {
	@Id
	@GeneratedValue( strategy=GenerationType.AUTO )
	private int id;
	 
	private String name;
	  
	@OneToMany
	@JoinColumn( name="Result_id" )
	private List<MeasurementValue> measurementValues = new ArrayList<>();
	
	@ManyToOne( fetch=FetchType.LAZY )
	@JoinColumn(name = "ResultComputation_id")
	private ResultComputation variable;

	public Integer getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<MeasurementValue> getMeasurementValues() {
		return measurementValues;
	}
	
	public void setMeasurementValues(List<MeasurementValue> measurementValues) {
		this.measurementValues = measurementValues;
	}
	
	public void addMeasurementValue(MeasurementValue measurementValue) {
		this.measurementValues.add(measurementValue);
	}
	
	public ResultComputation getComputation() {
		return variable;
	}
	
	public void setComputation(ResultComputation computation) {
		this.variable = computation;
	}

	@Override
	public ResultComputation getVariable() {
		return variable;
	}

	@Override
	public void setVariable(ResultComputation variable) {
		this.variable = variable;
	}
}
