package io.github.dmnisson.labvision.dto.course;

public class CourseInfo {
	private final int id;
	private final String name;
	
	public CourseInfo(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
