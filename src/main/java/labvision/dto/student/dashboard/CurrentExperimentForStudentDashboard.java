package labvision.dto.student.dashboard;

import java.time.LocalDateTime;

public class CurrentExperimentForStudentDashboard extends ExperimentForStudentDashboard {
	private final String courseName;
	private final int courseId;
	
	public CurrentExperimentForStudentDashboard(
			int id,
			String name,
			int courseId,
			String courseName,
			LocalDateTime lastUpdated,
			LocalDateTime reportDueDate) {
		super(id, name, lastUpdated, reportDueDate);
		this.courseName = courseName;
		this.courseId = courseId;
	}

	public String getCourseName() {
		return courseName;
	}

	public int getCourseId() {
		return courseId;
	}
}
