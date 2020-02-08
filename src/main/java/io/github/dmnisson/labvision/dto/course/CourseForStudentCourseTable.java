package io.github.dmnisson.labvision.dto.course;

import java.time.LocalDateTime;

public class CourseForStudentCourseTable extends CourseInfo {

	private final String enrolledClass;
	private final long numOfExperiments;
	private final LocalDateTime nextReportDue;
	
	
	public CourseForStudentCourseTable(int id, String name, String enrolledClass, long numOfExperiments,
			LocalDateTime nextReportDue) {
		super(id, name);
		this.enrolledClass = enrolledClass;
		this.numOfExperiments = numOfExperiments;
		this.nextReportDue = nextReportDue;
	}


	public String getEnrolledClass() {
		return enrolledClass;
	}


	public long getNumOfExperiments() {
		return numOfExperiments;
	}


	public LocalDateTime getNextReportDue() {
		return nextReportDue;
	}
	
}
