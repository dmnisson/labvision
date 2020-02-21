package io.github.dmnisson.labvision.dto.course;

public class CourseClassForAdminTable {
	private final Integer id;
	private final String name;
	private final Long numOfStudents;
	private final Long numOfInstructors;
	
	public CourseClassForAdminTable(Integer id, String name, Long numOfStudents, Long numOfInstructors) {
		super();
		this.id = id;
		this.name = name;
		this.numOfStudents = numOfStudents;
		this.numOfInstructors = numOfInstructors;
	}
	
	public Integer getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Long getNumOfStudents() {
		return numOfStudents;
	}
	
	public Long getNumOfInstructors() {
		return numOfInstructors;
	}
	
}
