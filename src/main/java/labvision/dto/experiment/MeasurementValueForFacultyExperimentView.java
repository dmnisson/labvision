package labvision.dto.experiment;

import labvision.entities.QuantityTypeId;

public class MeasurementValueForFacultyExperimentView extends MeasurementValueForExperimentView {
	private final int courseClassId;
	private final int studentId;
	
	public MeasurementValueForFacultyExperimentView(Integer id, Integer measurementId, String measurementName,
			Double value, Double uncertainty, String dimension, QuantityTypeId quantityTypeId, int courseClassId,
			int studentId) {
		super(id, measurementId, measurementName, value, uncertainty, dimension, quantityTypeId);
		this.courseClassId = courseClassId;
		this.studentId = studentId;
	}

	public int getCourseClassId() {
		return courseClassId;
	}

	public int getStudentId() {
		return studentId;
	}
}
