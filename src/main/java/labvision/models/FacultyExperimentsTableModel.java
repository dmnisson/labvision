package labvision.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import labvision.entities.Experiment;
import labvision.entities.ReportedResult;

public class FacultyExperimentsTableModel {

	private HashMap<Experiment, BigDecimal> averageStudentScores = new HashMap<>();
	private HashMap<Experiment, ArrayList<ReportedResult>> reportedResults = new HashMap<>();
	
	public void setAverageStudentScores(Map<? extends Experiment, ? extends BigDecimal> scores) {
		this.averageStudentScores.clear();
		this.averageStudentScores.putAll(scores);
	}

	public Map<Experiment, BigDecimal> getAverageStudentScores() {
		return averageStudentScores;
	}

	public void setReportedResults(Map<? extends Experiment, ? extends List<? extends ReportedResult>> reportedResults) {
		this.reportedResults.clear();
		reportedResults.forEach((e, rrl) -> {
			this.reportedResults.put(e, new ArrayList<>(rrl));
		});
	}

	public Map<Experiment, ArrayList<ReportedResult>> getReportedResults() {
		return reportedResults;
	}
}
