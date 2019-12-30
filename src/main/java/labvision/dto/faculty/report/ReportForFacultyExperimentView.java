package labvision.dto.faculty.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReportForFacultyExperimentView {
	private final int id;
	private final int studentId;
	private final String name;
	private final LocalDateTime added;
	private final BigDecimal score;
	
	public ReportForFacultyExperimentView(int id, int studentId, String name, LocalDateTime added, BigDecimal score) {
		super();
		this.id = id;
		this.studentId = studentId;
		this.name = name;
		this.added = added;
		this.score = score;
	}
	
	public int getId() {
		return id;
	}
	public int getStudentId() {
		return studentId;
	}
	public String getName() {
		return name;
	}
	public LocalDateTime getAdded() {
		return added;
	}
	public BigDecimal getScore() {
		return score;
	}
}
