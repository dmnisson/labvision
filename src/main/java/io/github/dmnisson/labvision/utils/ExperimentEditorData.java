package io.github.dmnisson.labvision.utils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import io.github.dmnisson.labvision.DatabaseAction;
import io.github.dmnisson.labvision.entities.Measurement;
import io.github.dmnisson.labvision.entities.Parameter;
import io.github.dmnisson.labvision.entities.QuantityTypeId;

public class ExperimentEditorData {
	private Map<String, String> requestParams;
	private String experimentName;
	private String description;
	private LocalDateTime submissionDeadline;
	private HashMap<String, DatabaseAction> measurementActions;
	private HashMap<String, DatabaseAction> parameterActions;
	private HashMap<String, Measurement> measurements;
	private HashMap<String, Parameter> parameters;
	private Map<String, String> newMeasurementNames;
	private Map<String, QuantityTypeId> newMeasurementQuantityTypeIds;
	private Map<String, String> newParameterNames;
	private Map<String, QuantityTypeId> newParameterQuantityTypeIds;

	public ExperimentEditorData(Map<String, String> requestParams, String experimentName,
			String description, LocalDateTime submissionDeadline, HashMap<String, DatabaseAction> measurementActions,
			HashMap<String, DatabaseAction> parameterActions, HashMap<String, Measurement> measurements,
			HashMap<String, Parameter> parameters, Map<String, String> newMeasurementNames,
			Map<String, QuantityTypeId> newMeasurementQuantityTypeIds, Map<String, String> newParameterNames,
			Map<String, QuantityTypeId> newParameterQuantityTypeIds) {
		this.requestParams = requestParams;
		this.experimentName = experimentName;
		this.description = description;
		this.submissionDeadline = submissionDeadline;
		this.measurementActions = measurementActions;
		this.parameterActions = parameterActions;
		this.measurements = measurements;
		this.parameters = parameters;
		this.newMeasurementNames = newMeasurementNames;
		this.newMeasurementQuantityTypeIds = newMeasurementQuantityTypeIds;
		this.newParameterNames = newParameterNames;
		this.newParameterQuantityTypeIds = newParameterQuantityTypeIds;
	}

	public Map<String, String> getRequestParams() {
		return requestParams;
	}

	public String getExperimentName() {
		return experimentName;
	}

	public String getDescription() {
		return description;
	}

	public LocalDateTime getSubmissionDeadline() {
		return submissionDeadline;
	}

	public HashMap<String, DatabaseAction> getMeasurementActions() {
		return measurementActions;
	}

	public HashMap<String, DatabaseAction> getParameterActions() {
		return parameterActions;
	}

	public HashMap<String, Measurement> getMeasurements() {
		return measurements;
	}

	public HashMap<String, Parameter> getParameters() {
		return parameters;
	}

	public Map<String, String> getNewMeasurementNames() {
		return newMeasurementNames;
	}

	public Map<String, QuantityTypeId> getNewMeasurementQuantityTypeIds() {
		return newMeasurementQuantityTypeIds;
	}

	public Map<String, String> getNewParameterNames() {
		return newParameterNames;
	}

	public Map<String, QuantityTypeId> getNewParameterQuantityTypeIds() {
		return newParameterQuantityTypeIds;
	}
}