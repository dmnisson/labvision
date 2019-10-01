package labvision.dto.student.experiment;

public class ExperimentForStudentExperimentTable {

	protected final int id;
	protected final String name;

	public ExperimentForStudentExperimentTable(int id, String name) {
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