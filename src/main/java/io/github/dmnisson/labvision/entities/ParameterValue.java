package io.github.dmnisson.labvision.entities;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Value of a parameter
 * @author davidnisson
 *
 * @param <M> the measurement quantity
 * @param <P> the parameter quantity
 */
@Entity
public class ParameterValue extends VariableValue<Parameter, ParameterValue> {
	@ManyToOne(targetEntity = Parameter.class, fetch = FetchType.LAZY )
	@JoinColumn( name="Parameter_id" )
	private Parameter variable;
	
	@ManyToOne(targetEntity = MeasurementValue.class, fetch = FetchType.LAZY )
	@JoinColumn( name="MeasurementValue_id" )
	private MeasurementValue measurementValue;

	@Override
	public Parameter getVariable() {
		return variable;
	}

	@Override
	public void setVariable(Parameter parameter) {
		if (!Objects.isNull(measurementValue)
				&& !parameter.getMeasurement().equals(measurementValue.getVariable())) {
			throw new IllegalArgumentException(
					"parameter value's measurement and parameter's measurement don't match");
		}
		this.variable = parameter;
	}

	public MeasurementValue getMeasurementValue() {
		return measurementValue;
	}

	public void setMeasurementValue(MeasurementValue measurementValue) {
		if (!Objects.isNull(variable)
				&& !variable.getMeasurement().equals(measurementValue.getVariable())) {
			throw new IllegalArgumentException(
					"parameter value's measurement and parameter's measurement don't match");
		}
		this.measurementValue = measurementValue;
	}}
