package labvision.viewmodels;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import labvision.entities.Experiment;
import labvision.entities.ReportedResult;

/**
 * Model for the tables on the Experiments page
 * @author davidnisson
 *
 */
public class StudentExperimentsTableModel {
	/**
	 * Student's currently active experiments
	 */
	private ArrayList<Experiment> currentExperiments = new ArrayList<>();
	
	/**
	 * Past experiments
	 */
	private ArrayList<Experiment> pastExperiments = new ArrayList<>();
	
	/**
	 * Reported results for student
	 */
	private HashMap<Experiment, List<ReportedResult>> reportedResults = new HashMap<>();
	
	/**
	 * Last time student updated his report
	 */
	private HashMap<Experiment, LocalDateTime> lastReportUpdated = new HashMap<>();
	
	/**
	 * Total score of all student reports
	 */
	private HashMap<Experiment, BigDecimal> totalReportScore = new HashMap<>();
	
	public void setCurrentExperiments(List<? extends Experiment> activeExperiments) {
		currentExperiments.clear();
		currentExperiments.addAll(activeExperiments);
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

	public Map<Experiment, List<ReportedResult>> getReportedResults() {
		return reportedResults;
	}

	public void setReportedResults(Map<? extends Experiment, ? extends List<? extends ReportedResult>> reportedResults) {
		this.reportedResults.clear();
		reportedResults.forEach((e, list) -> {
			this.reportedResults.put(e, new ArrayList<ReportedResult>(list));
		});
	}

	public HashMap<Experiment, LocalDateTime> getLastReportUpdated() {
		return lastReportUpdated;
	}

	public void setLastReportUpdated(Map<? extends Experiment, ? extends LocalDateTime> lastReportUpdated) {
		this.lastReportUpdated.clear();
		this.lastReportUpdated.putAll(lastReportUpdated);
	}

	public Map<Experiment, BigDecimal> getTotalReportScore() {
		return totalReportScore;
	}

	public void setTotalReportScore(Map<? extends Experiment, ? extends BigDecimal> totalReportScore) {
		this.totalReportScore.clear();
		this.totalReportScore.putAll(totalReportScore);
	}
}
