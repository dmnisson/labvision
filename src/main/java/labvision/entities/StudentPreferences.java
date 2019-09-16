package labvision.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

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
	
	@OneToOne
	private Student student;
	
	// Dashboard preferences
	private int maxRecentExperiments;
	
	private int maxRecentCourses;

	public int getMaxRecentExperiments() {
		return maxRecentExperiments;
	}

	public void setMaxRecentExperiments(int maxRecentExperiments) {
		this.maxRecentExperiments = maxRecentExperiments;
	}

	public int getMaxRecentCourses() {
		return maxRecentCourses;
	}

	public void setMaxRecentCourses(int maxRecentCourses) {
		this.maxRecentCourses = maxRecentCourses;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
