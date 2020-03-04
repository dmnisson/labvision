package io.github.dmnisson.labvision.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;

/**
 * Stores a student user preferences
 * @author davidnisson
 *
 */
@Entity
public class StudentPreferences implements LabVisionEntity {
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	private int id;
	
	@OneToOne( mappedBy = "studentPreferences" )
	private Student student;
	
	// --- Dashboard preferences ---
	
	@Min(1)
	private Integer maxCurrentExperiments;
	
	@Min(1)
	private Integer maxRecentExperiments;
	
	@Min(1)
	private Integer maxRecentCourses;
	
	public int getMaxRecentExperiments() {
		return maxRecentExperiments;
	}

	public void setMaxRecentExperiments(Integer maxRecentExperiments) {
		this.maxRecentExperiments = maxRecentExperiments;
	}

	public int getMaxRecentCourses() {
		return maxRecentCourses;
	}

	public void setMaxRecentCourses(Integer maxRecentCourses) {
		this.maxRecentCourses = maxRecentCourses;
	}

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMaxCurrentExperiments() {
		return maxCurrentExperiments;
	}

	public void setMaxCurrentExperiments(Integer maxCurrentExperiments) {
		this.maxCurrentExperiments = maxCurrentExperiments;
	}
}
