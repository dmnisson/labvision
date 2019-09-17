package labvision.viewmodels;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import labvision.entities.Measurement;
import labvision.entities.Parameter;
import labvision.entities.ReportedResult;

/**
 * Model for the basic features of the experiment detail views
 * @author davidnisson
 *
 */
public class ExperimentViewModel {
	private HashMap<Measurement, String> measurementUnits = new HashMap<>();
	private HashMap<Parameter, String> parameterUnits = new HashMap<>();
	
	private HashMap<ReportedResult, String> reportDisplay = new HashMap<>();

	public Map<Measurement, String> getMeasurementUnits() {
		return measurementUnits;
	}

	public void setMeasurementUnits(Map<? extends Measurement, String> measurementUnits) {
		this.measurementUnits.clear();
		this.measurementUnits.putAll(measurementUnits);
	}

	public Map<Parameter, String> getParameterUnits() {
		return parameterUnits;
	}

	public void setParameterUnits(Map<? extends Parameter, String> parameterUnits) {
		this.parameterUnits.clear();
		this.parameterUnits.putAll(parameterUnits);
	}

	public Map<ReportedResult, String> getReportDisplay() {
		return reportDisplay;
	}

	public void setReportDisplay(Map<? extends ReportedResult, String> reportDisplay) {
		this.reportDisplay.clear();
		this.reportDisplay.putAll(reportDisplay);
	}
	
	public static final Function<ReportedResult, String> REPORT_DISPLAY_FUNCTION = rr -> {
		if (Objects.isNull(rr.getReportDocument())) {
			return String.format("Report %d", rr.getId());
		} else {
			return rr.getReportDocument().getFilename();
		}
	};
}
