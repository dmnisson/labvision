package labvision.viewmodels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import labvision.ReportStatus;
import labvision.entities.Experiment;
import labvision.entities.ReportedResult;

/**
 * Model for the tables on the Experiments page
 * @author davidnisson
 *
 */
public class ExperimentsTableModel {
	private ArrayList<Experiment> currentExperiments = new ArrayList<>();
	private ArrayList<Experiment> pastExperiments = new ArrayList<>();
	private HashMap<Experiment, ReportedResult> reportedResults = new HashMap<>();
	private HashMap<Experiment, ReportStatus> reportStatus = new HashMap<>();
	
	public void setCurrentExperiments(List<? extends Experiment> activeExperiments) {
		currentExperiments.clear();
		currentExperiments.addAll(activeExperiments);
	}

	public void setReportedResults(Map<? extends Experiment, ? extends ReportedResult> reportedResults) {
		this.reportedResults.clear();
		this.reportedResults.putAll(reportedResults);
	}

	public void setReportStatus(Map<? extends Experiment, ? extends ReportStatus> reportStatus) {
		this.reportStatus.clear();
		this.reportStatus.putAll(reportStatus);
	}

	public void setPastExperiments(List<? extends Experiment> recentExperiments) {
		pastExperiments.clear();
		pastExperiments.addAll(recentExperiments);
	}

	public ArrayList<Experiment> getCurrentExperiments() {
		return currentExperiments;
	}

	public ArrayList<Experiment> getPastExperiments() {
		return pastExperiments;
	}

	public HashMap<Experiment, ReportedResult> getReportedResults() {
		return reportedResults;
	}

	public HashMap<Experiment, ReportStatus> getReportStatus() {
		return reportStatus;
	}

}
