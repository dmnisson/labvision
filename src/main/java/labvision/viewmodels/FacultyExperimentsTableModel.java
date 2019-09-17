package labvision.viewmodels;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import labvision.entities.Experiment;

public class FacultyExperimentsTableModel {

	private HashMap<Experiment, BigDecimal> averageStudentScores = new HashMap<>();
	
	public void setAverageStudentScores(Map<? extends Experiment, ? extends BigDecimal> scores) {
		this.averageStudentScores.clear();
		this.averageStudentScores.putAll(scores);
	}

}
