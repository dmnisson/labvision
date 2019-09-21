package labvision.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import labvision.entities.Measurement;
import labvision.entities.MeasurementValue;

public class StudentExperimentViewModel extends ExperimentViewModel {
	private HashMap<Measurement, ArrayList<MeasurementValue> > measurementValues = new HashMap<>();
	
	public Map<Measurement, ArrayList<MeasurementValue> > getMeasurementValues() {
		return measurementValues;
	}

	public void setMeasurementValues(Map<? extends Measurement, ? extends List<? extends MeasurementValue> > measurementValues) {
		this.measurementValues.clear();
		this.measurementValues.putAll(measurementValues.entrySet().stream()
				.collect(Collectors.<Map.Entry<? extends Measurement, ? extends List<? extends MeasurementValue>>,
						Measurement, ArrayList<MeasurementValue>,
						HashMap<Measurement, ArrayList<MeasurementValue>>>toMap(
						Map.Entry::getKey,
						e -> e.getValue().stream()
							.collect(Collectors.toCollection(ArrayList::new)),
						(l1, l2) -> Stream.concat(l1.stream(), l2.stream())
							.collect(Collectors.toCollection(ArrayList::new)),
						HashMap::new)));
	}

}
