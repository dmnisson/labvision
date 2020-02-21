package io.github.dmnisson.labvision.models;

import java.util.List;
import java.util.Map;

import io.github.dmnisson.labvision.dto.experiment.MeasurementInfo;
import io.github.dmnisson.labvision.dto.experiment.ParameterForExperimentView;
import io.github.dmnisson.labvision.dto.experiment.ParameterValueForExperimentView;
import io.github.dmnisson.labvision.entities.Experiment;

/**
 * Model for experiment views
 * @author davidnisson
 *
 * @param <MVGROUPING> the data structure used to group measurement values; can be a simple List or Map of Maps
 */
public class ExperimentViewModel<MVGROUPING> {
	private List<MeasurementInfo> measurements;
	private Map<Integer, MVGROUPING> measurementValues;
	private Experiment experiment;
	private Map<Integer, List<ParameterForExperimentView>> parameters;
	private Map<Integer, Map<Integer, Map<Integer, ParameterValueForExperimentView>>> parameterValues;

	public ExperimentViewModel(List<MeasurementInfo> measurements, Map<Integer, MVGROUPING> measurementValues,
			Experiment experiment,
			Map<Integer, List<ParameterForExperimentView>> parameters, Map<Integer, Map<Integer, Map<Integer, ParameterValueForExperimentView>>> parameterValues) {
		this.measurements = measurements;
		this.measurementValues = measurementValues;
		this.experiment = experiment;
		this.parameters = parameters;
		this.parameterValues = parameterValues;
	}

	public List<MeasurementInfo> getMeasurements() {
		return measurements;
	}

	public Map<Integer, MVGROUPING> getMeasurementValues() {
		return measurementValues;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public Map<Integer, List<ParameterForExperimentView>> getParameters() {
		return parameters;
	}

	public Map<Integer, Map<Integer, Map<Integer, ParameterValueForExperimentView>>> getParameterValues() {
		return parameterValues;
	}
}