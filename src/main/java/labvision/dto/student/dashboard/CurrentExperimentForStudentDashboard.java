package labvision.dto.student.dashboard;

public class CurrentExperimentForStudentDashboard {
	private final String name;
	private final int id;
	private final String courseName;
	private final int courseId;
	
	public CurrentExperimentForStudentDashboard(int id, String name, int courseId, String courseName) {
		this.name = name;
		this.id = id;
		this.courseName = courseName;
		this.courseId = courseId;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public String getCourseName() {
		return courseName;
	}

	public int getCourseId() {
		return courseId;
	}
}
