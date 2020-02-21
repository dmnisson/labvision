package io.github.dmnisson.labvision.dto.reportedresult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReportedResultForAdminTable extends ReportedResultInfo {

	private final Integer studentId;
	private final String studentUsername;
	private final BigDecimal score;
	
	public ReportedResultForAdminTable(Integer id, String name, LocalDateTime added, Integer studentId,
			String studentUsername, BigDecimal score) {
		super(id, name, added);
		this.studentId = studentId;
		this.studentUsername = studentUsername;
		this.score = score;
	}

	public Integer getStudentId() {
		return studentId;
	}

	public String getStudentUsername() {
		return studentUsername;
	}

	public BigDecimal getScore() {
		return score;
	}
	
}
