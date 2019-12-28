package labvision.dto.experiment;

/**
 * Smallest subset of information about an experiment
 * @author davidnisson
 */
public class ExperimentInfo {
	private final int id;
	private final String name;
	private final String courseName;
	
	public ExperimentInfo(int id, String name, String courseName) {
		super();
		this.id = id;
		this.name = name;
		this.courseName = courseName;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCourseName() {
		return courseName;
	}
}
