package labvision.dto.student.dashboard;

public class ExperimentForStudentDashboard {

	protected final String name;
	protected final int id;

	public ExperimentForStudentDashboard(int id, String name) {
		super();
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

}