package labvision;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Experiment {
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	private int id;
	
	private String name;
	
	private String description;
	
	@ManyToOne
	private Course course;
	
	@OneToMany( targetEntity=Measurement.class )
	private List<Measurement<?>> measurements;
	
	@OneToMany
	private List<Result<?>> acceptedResults;
	
	@OneToMany
	private List<Result<?>> obtainedResults;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public List<Measurement<?>> getMeasurements() {
		return measurements;
	}

	public void setMeasurements(List<Measurement<?>> measurements) {
		this.measurements = measurements;
	}

	public List<Result<?>> getAcceptedResult() {
		return acceptedResults;
	}

	public void setAcceptedResults(List<Result<?>> acceptedResults) {
		this.acceptedResults = acceptedResults;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Result<?>> getObtainedResults() {
		return obtainedResults;
	}

	public void setObtainedResults(List<Result<?>> obtainedResults) {
		this.obtainedResults = obtainedResults;
	}
}
