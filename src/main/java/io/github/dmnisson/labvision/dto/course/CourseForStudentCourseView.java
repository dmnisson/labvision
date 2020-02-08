package io.github.dmnisson.labvision.dto.course;

public class CourseForStudentCourseView extends CourseInfo {

	private final String className;
	
	public CourseForStudentCourseView(int id, String name, String className) {
		super(id, name);
		this.className = className;
	}

	public String getClassName() {
		return className;
	}
	
}
