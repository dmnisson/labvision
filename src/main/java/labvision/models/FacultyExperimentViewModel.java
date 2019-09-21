package labvision.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import labvision.entities.CourseClass;
import labvision.entities.Measurement;
import labvision.entities.MeasurementValue;
import labvision.entities.Student;

public class FacultyExperimentViewModel extends ExperimentViewModel {
	private HashMap<Measurement, HashMap<CourseClass, HashMap<Student, ArrayList<MeasurementValue>>>> measurementValues =
			new HashMap<>();

	public Map<Measurement, HashMap<CourseClass, HashMap<Student, ArrayList<MeasurementValue>>>> getMeasurementValues() {
		return measurementValues;
	}

	public void setMeasurementValues(
			Map<? extends Measurement, 
					? extends Map<? extends CourseClass, ? extends Map<? extends Student, 
							? extends List<? extends MeasurementValue>>>> measurementValues) {
		this.measurementValues.clear();
		this.measurementValues.putAll(measurementValues.entrySet().stream()
			.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, e2 -> e2.getValue().entrySet().stream()
							.collect(Collectors.toMap(Map.Entry::getKey, e3 -> e3.getValue().stream()
									.collect(Collectors.toCollection(ArrayList::new)),
							(l1, l2) -> Stream.concat(l1.stream(), l2.stream())
									.collect(Collectors.toCollection(ArrayList::new)),
								    HashMap::new
									)),
							(m1, m2) -> Stream.concat(m1.entrySet().stream(), m2.entrySet().stream())
									.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
											(l1, l2) -> Stream.concat(l1.stream(), l2.stream())
											.collect(Collectors.toCollection(ArrayList::new)),
											HashMap::new)),
							HashMap::new
							)))));
	}
}
