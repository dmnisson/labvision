package labvision.viewmodels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.measure.Unit;

import labvision.entities.Measurement;
import labvision.entities.MeasurementValue;
import labvision.entities.Parameter;
import labvision.entities.ParameterValue;
import labvision.entities.ReportedResult;

/**
 * Model for the basic features of the experiment detail views
 * @author davidnisson
 *
 */
public class ExperimentViewModel {
	private HashMap<Measurement, Unit<?>> measurementUnits = new HashMap<>();
	private HashMap<Parameter, Unit<?>> parameterUnits = new HashMap<>();
	
	private HashMap<Measurement, List<MeasurementValue> > measurementValues = new HashMap<>();
	private HashMap<Parameter, List<ParameterValue> > parameterValues = new HashMap<>();
	
	private HashMap<ReportedResult, String> reportDisplay = new HashMap<>();

	public Map<Measurement, Unit<?>> getMeasurementUnits() {
		return measurementUnits;
	}

	public void setMeasurementUnits(Map<? extends Measurement, ? extends Unit<?>> measurementUnits) {
		this.measurementUnits.clear();
		this.measurementUnits.putAll(measurementUnits);
	}

	public Map<Parameter, Unit<?>> getParameterUnits() {
		return parameterUnits;
	}

	public void setParameterUnits(Map<? extends Parameter, ? extends Unit<?>> parameterUnits) {
		this.parameterUnits.clear();
		this.parameterUnits.putAll(parameterUnits);
	}

	public Map<Measurement, List<MeasurementValue> > getMeasurementValues() {
		return measurementValues;
	}

	public void setMeasurementValues(Map<? extends Measurement, ? extends List<? extends MeasurementValue> > measurementValues) {
		this.measurementValues.clear();
		measurementValues.forEach((m, v) -> {
			this.measurementValues.put(m, new ArrayList<>(v));
		});
	}

	public HashMap<Parameter, List<ParameterValue> > getParameterValues() {
		return parameterValues;
	}

	public void setParameterValues(Map<? extends Parameter, ? extends List<? extends ParameterValue> > parameterValues) {
		this.parameterValues.clear();
		parameterValues.forEach((p, v) -> {
			this.parameterValues.put(p, new ArrayList<>(v));
		});
	}

	public Map<ReportedResult, String> getReportDisplay() {
		return reportDisplay;
	}

	public void setReportDisplay(Map<? extends ReportedResult, String> reportDisplay) {
		this.reportDisplay.clear();
		this.reportDisplay.putAll(reportDisplay);
	}
}
