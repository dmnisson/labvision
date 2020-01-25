package io.github.dmnisson.labvision.dto.student.reports;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReportForStudentReportsTable {
	private final int id;
	private final String name;
	private final int experimentId;
	private final String experimentName;
	private final int courseId;
	private final String courseName;
	private final BigDecimal score;
	private final LocalDateTime added;
	private final boolean editAllowed;
	
	public ReportForStudentReportsTable(int id, String name, int experimentId, String experimentName, int courseId,
			String courseName, BigDecimal score, LocalDateTime added, boolean editAllowed) {
		this.id = id;
		this.name = name;
		this.experimentId = experimentId;
		this.experimentName = experimentName;
		this.courseId = courseId;
		this.courseName = courseName;
		this.score = score;
		this.added = added;
		this.editAllowed = editAllowed;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getExperimentId() {
		return experimentId;
	}

	public String getExperimentName() {
		return experimentName;
	}

	public int getCourseId() {
		return courseId;
	}

	public String getCourseName() {
		return courseName;
	}

	public BigDecimal getScore() {
		return score;
	}

	public LocalDateTime getAdded() {
		return added;
	}

	public boolean isEditAllowed() {
		return editAllowed;
	}
}
