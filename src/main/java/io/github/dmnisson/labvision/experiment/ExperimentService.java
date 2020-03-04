package io.github.dmnisson.labvision.experiment;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.dmnisson.labvision.ResourceNotFoundException;
import io.github.dmnisson.labvision.dto.experiment.MeasurementInfo;
import io.github.dmnisson.labvision.dto.experiment.MeasurementValueForExperimentView;
import io.github.dmnisson.labvision.dto.experiment.MeasurementValueForFacultyExperimentView;
import io.github.dmnisson.labvision.dto.experiment.ParameterForExperimentView;
import io.github.dmnisson.labvision.dto.experiment.ParameterValueForExperimentView;
import io.github.dmnisson.labvision.entities.Course;
import io.github.dmnisson.labvision.entities.Experiment;
import io.github.dmnisson.labvision.entities.Measurement;
import io.github.dmnisson.labvision.entities.Parameter;
import io.github.dmnisson.labvision.models.ExperimentViewModel;
import io.github.dmnisson.labvision.repositories.CourseRepository;
import io.github.dmnisson.labvision.repositories.ExperimentRepository;
import io.github.dmnisson.labvision.repositories.MeasurementRepository;
import io.github.dmnisson.labvision.repositories.MeasurementValueRepository;
import io.github.dmnisson.labvision.repositories.ParameterRepository;
import io.github.dmnisson.labvision.repositories.ParameterValueRepository;
import io.github.dmnisson.labvision.utils.ExperimentEditorData;

@Service
public class ExperimentService {
	
	@Autowired
	private ExperimentRepository experimentRepository;
	@Autowired
	private MeasurementRepository measurementRepository;
	@Autowired
	private MeasurementValueRepository measurementValueRepository;
	@Autowired
	private ParameterRepository parameterRepository;
	@Autowired
	private ParameterValueRepository parameterValueRepository;
	@Autowired
	private CourseRepository courseRepository;
	
	public Experiment createExperiment(final Integer courseId, ExperimentEditorData editorData) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new ResourceNotFoundException(Course.class, courseId));
		
		Experiment experiment = experimentRepository.save(
				course.addExperiment(
						editorData.getExperimentName(),
						editorData.getDescription(),
						editorData.getSubmissionDeadline()
						)
				);
		
		return updateVariables(experiment, editorData);
	}
	
	public Experiment updateExperiment(final Experiment experiment, ExperimentEditorData editorData) {
		experiment.setName(editorData.getExperimentName());
		experiment.setDescription(editorData.getDescription());
		experiment.setReportDueDate(editorData.getSubmissionDeadline());
		
		return updateVariables(experiment, editorData);
	}

	private Experiment updateVariables(final Experiment experiment, ExperimentEditorData editorData) {
		// perform actions on measurements
		editorData.getMeasurementActions().forEach((key1, action) -> {
			Measurement measurement;
			switch (action) {
			case CREATE:
				measurement = experiment.addMeasurement(
					editorData.getNewMeasurementNames().get(key1),
					editorData.getNewMeasurementQuantityTypeIds().get(key1).getQuantityClass().getQuantityType()
				);
				editorData.getMeasurements().put(key1, measurement);
				break;
			case UPDATE:
				measurement = measurementRepository.findById(Integer.parseInt(key1))
					.orElseThrow(() -> new ResourceNotFoundException(Measurement.class, Integer.parseInt(key1)));
				measurement.setName(editorData.getNewMeasurementNames().get(key1));
				measurement.setQuantityTypeId(editorData.getNewMeasurementQuantityTypeIds().get(key1));
				measurement = measurementRepository.save(measurement);
				editorData.getMeasurements().put(key1, measurement);
				break;
			case DELETE:
				measurement = measurementRepository.findById(Integer.parseInt(key1))
					.orElseThrow(() -> new ResourceNotFoundException(Measurement.class, Integer.parseInt(key1)));
				experiment.removeMeasurement(measurement);
				measurementRepository.deleteById(measurement.getId());
			}
		});
		
		// perform actions on parameters
		editorData.getParameterActions().forEach((key2, action) -> {
			String measurementKey = editorData.getRequestParams().get("parameterMeasurementId" + key2);
			Measurement measurement = editorData.getMeasurements().get(measurementKey);
			Parameter parameter;
			switch (action) {
			case CREATE:
				parameter = measurement.addParameter(
						editorData.getNewParameterNames().get(key2),
						editorData.getNewParameterQuantityTypeIds().get(key2).getQuantityClass().getQuantityType()
						);
				measurement = measurementRepository.save(measurement);
				editorData.getParameters().put(key2, parameter);
				break;
			case UPDATE:
				parameter = parameterRepository.findById(Integer.parseInt(key2))
					.orElseThrow(() -> new ResourceNotFoundException(Parameter.class, Integer.parseInt(key2)));
				parameter.setName(editorData.getNewParameterNames().get(key2));
				parameter.setQuantityTypeId(editorData.getNewParameterQuantityTypeIds().get(key2));
				parameter = parameterRepository.save(parameter);
				editorData.getParameters().put(key2, parameter);
				break;
			case DELETE:
				parameter = parameterRepository.findById(Integer.parseInt(key2))
					.orElseThrow(() -> new ResourceNotFoundException(Parameter.class, Integer.parseInt(key2)));
				measurement = parameter.getMeasurement();
				measurement.removeParameter(parameter);
				parameterRepository.deleteById(parameter.getId());
				measurement = measurementRepository.save(measurement);
			}
		});
		
		Experiment savedExperiment = experimentRepository.save(experiment);
		return savedExperiment;
	}

	public ExperimentViewModel<List<MeasurementValueForExperimentView>> getExperimentViewModel(Integer experimentId) {
		List<MeasurementInfo> measurements;
		Map<Integer, List<MeasurementValueForExperimentView>> measurementValues;
		
		Experiment experiment = experimentRepository.findById(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		
		measurements = measurementRepository.findForExperimentView(experimentId);
		measurementValues = measurementValueRepository.findForExperiment(experimentId).stream()
				.collect(Collectors.groupingBy(
						MeasurementValueForExperimentView::getMeasurementId,
						Collectors.toList()
						));
		
		final Map<Integer, List<ParameterForExperimentView>> parameters = measurements.stream()
				.map(MeasurementInfo::getId)
				.collect(Collectors.toMap(
						Function.identity(),
						id -> parameterRepository.findForExperimentView(id)));
	
		final Map<Integer, Map<Integer, Map<Integer, ParameterValueForExperimentView>>> parameterValues = measurements.stream()
				.map(MeasurementInfo::getId)
				.filter(id -> !Objects.isNull(measurementValues.get(id)))
				.collect(Collectors.toMap(
						Function.identity(),
						mid -> measurementValues.get(mid).stream()
							.map(MeasurementValueForExperimentView::getId)
							.collect(Collectors.toMap(
									Function.identity(),
									vid -> parameterValueRepository.findForExperimentView(vid).stream()
										.collect(Collectors.toMap(
												ParameterValueForExperimentView::getParameterId,
												Function.identity()
												))
							))
						));
		ExperimentViewModel<List<MeasurementValueForExperimentView>> experimentViewModel = new ExperimentViewModel<>(
				measurements, measurementValues, experiment, parameters, parameterValues);
		return experimentViewModel;
	}
	
	public ExperimentViewModel<Map<Integer, Map<Integer, List<MeasurementValueForFacultyExperimentView>>>> getExperimentViewModel(Integer experimentId, int instructorId) {
		List<MeasurementInfo> measurements;
		Map<Integer, Map<Integer, Map<Integer, List<MeasurementValueForFacultyExperimentView>>>> measurementValues;
		
		Experiment experiment = experimentRepository.findById(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		
		measurements = measurementRepository.findForExperimentView(experimentId);
		measurementValues = measurementValueRepository.findForInstructor(experimentId, instructorId).stream()
				.collect(Collectors.groupingBy(
						MeasurementValueForFacultyExperimentView::getMeasurementId,
						Collectors.groupingBy(
								MeasurementValueForFacultyExperimentView::getCourseClassId,
								Collectors.groupingBy(
										MeasurementValueForFacultyExperimentView::getStudentId,
										Collectors.toList()
										))));
		
		final Map<Integer, List<ParameterForExperimentView>> parameters = measurements.stream()
				.map(MeasurementInfo::getId)
				.collect(Collectors.toMap(
						Function.identity(),
						id -> parameterRepository.findForExperimentView(id)));
		
		final Map<Integer, Map<Integer, Map<Integer, ParameterValueForExperimentView>>> parameterValues = measurements.stream()
				.map(MeasurementInfo::getId)
				.filter(id -> !Objects.isNull(measurementValues.get(id)))
				.collect(Collectors.toMap(
						Function.identity(),
						mid -> measurementValues.get(mid).entrySet().stream()
							.flatMap(e -> e.getValue().entrySet().stream())
							.flatMap(e -> e.getValue().stream())
							.map(MeasurementValueForExperimentView::getId)
							.collect(Collectors.toMap(
									Function.identity(),
									vid -> parameterValueRepository.findForExperimentView(vid).stream()
										.collect(Collectors.toMap(
												ParameterValueForExperimentView::getParameterId,
												Function.identity()
												))
							))
						));
		ExperimentViewModel<Map<Integer, Map<Integer, List<MeasurementValueForFacultyExperimentView>>>> experimentViewModel = new ExperimentViewModel<>(
				measurements, measurementValues, experiment, parameters, parameterValues);
		return experimentViewModel;
	}
	
	public <DTO> List<DTO> findExperimentData(Integer userId, int limit, Class<DTO> dtoClass) {
		Pageable noReportsPageable = PageRequest.of(0, limit);
		
		ExperimentDashboardQueries<DTO, Integer> dashboardQueries =
				ExperimentDashboardQueriesFactory.createDashboardQueriesForDtoType(
						experimentRepository, 
						dtoClass,
						Integer.class
						);
		
		List<DTO> experimentsNoReports = dashboardQueries.findExperimentsNoReports(userId, noReportsPageable);
		
		assert experimentsNoReports.size() <= limit;
		if (experimentsNoReports.size() == limit) {
			return experimentsNoReports;
		}
		
		Pageable withReportsPageable = PageRequest.of(0, limit - experimentsNoReports.size());
		
		List<DTO> experimentsWithReports = dashboardQueries.findExperimentsWithReports(userId, withReportsPageable);
		
		return Stream.concat(
					experimentsNoReports.stream(),
					experimentsWithReports.stream()
					)
				.collect(Collectors.toList());
	}
	
	public long countCurrentExperimentsByStudentId(Integer studentId) {
		return experimentRepository.countCurrentExperimentsByStudentId(studentId);
	}
	
	public long countRecentExperimentsByStudentId(Integer studentId) {
		return experimentRepository.countRecentExperimentsByStudentId(studentId);
	}
	
}
