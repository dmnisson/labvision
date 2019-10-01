package labvision.dto.student.dashboard;

public class CurrentExperimentForStudentDashboard extends ExperimentForStudentDashboard {
	private final String courseName;
	private final int courseId;
	
	public CurrentExperimentForStudentDashboard(int id, String name, int courseId, String courseName) {
		super(id, name);
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
