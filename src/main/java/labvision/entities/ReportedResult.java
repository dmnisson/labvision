package labvision.entities;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * A report of experimental results by a student
 * @author davidnisson
 *
 */
public class ReportedResult {
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	private int id;
	
	@ManyToOne
	private Student student;
	
	@ManyToOne
	private Experiment experiment;
	
	@OneToMany
	private List<Result<?>> results;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

	public List<Result<?>> getResults() {
		return results;
	}

	public void setResults(List<Result<?>> results) {
		this.results = results;
	}
}
