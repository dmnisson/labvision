package io.github.dmnisson.labvision.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.github.dmnisson.labvision.DatabaseAction;
import io.github.dmnisson.labvision.entities.Measurement;
import io.github.dmnisson.labvision.entities.Parameter;
import io.github.dmnisson.labvision.entities.QuantityTypeId;

/**
 * Utility functions for handling request data from editors
 * @author David Nisson
 *
 */
public class EditorUtils {

	public static ExperimentEditorData getExperimentEditorDataFromRequestParams(Map<String, String> requestParams) {
		final String experimentName = requestParams.get("experimentName");
		final String description = requestParams.get("description");
		final LocalDateTime submissionDeadline = LocalDateTime.parse(
				requestParams.get("submissionDeadline"),
				DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a")
			);
		
		// database actions for each measurement and parameter
		// mapping is from client-side key to database action so as to allow new ones to be added
		HashMap<String, DatabaseAction> measurementActions = new HashMap<>();
		HashMap<String, DatabaseAction> parameterActions = new HashMap<>();
		HashSet<String> measurementKeys = new HashSet<>();
		HashSet<String> parameterKeys = new HashSet<>();
		
		for (Map.Entry<String, String> requestParameterEntry : requestParams.entrySet()) {
			String requestParameterName = requestParameterEntry.getKey();
			Pattern measurementParameterNamePattern = 
					Pattern.compile("^measurementAction(N?\\d+)$");
			Matcher measurementMatcher = measurementParameterNamePattern
					.matcher(requestParameterName);
			
			if (measurementMatcher.find()) {
				measurementActions.put(
					measurementMatcher.group(1),
					DatabaseAction.valueOf(requestParameterEntry.getValue())
				);
				measurementKeys.add(measurementMatcher.group(1));
				
				// early continue
				continue;
			}
			
			Pattern parameterParameterNamePattern =
					Pattern.compile("^parameterAction(N?\\d+)$");
			Matcher parameterMatcher = parameterParameterNamePattern
					.matcher(requestParameterName);
			
			if (parameterMatcher.find()) {
				parameterActions.put(
					parameterMatcher.group(1),
					DatabaseAction.valueOf(requestParameterEntry.getValue())
				);
				parameterKeys.add(parameterMatcher.group(1));
				
				// early continue
				continue;
			}
			
			// ensure we get keys for variables with no explicit database action
			// (implicit DatabaseAction.UPDATE)
			Pattern measurementNameParameterNamePattern =
					Pattern.compile("^measurementName(N?\\d+)$");
			Matcher measurementNameMatcher = measurementNameParameterNamePattern
					.matcher(requestParameterName);
			
			if (measurementNameMatcher.find()) {
				measurementKeys.add(measurementNameMatcher.group(1));
				
				// early continue
				continue;
			}
			
			Pattern parameterNameParameterNamePattern =
					Pattern.compile("^parameterName(N?\\d+)$");
			Matcher parameterNameMatcher = parameterNameParameterNamePattern
					.matcher(requestParameterName);
			
			if (parameterNameMatcher.find()) {
				parameterKeys.add(parameterNameMatcher.group(1));
			}
		}
		
		// infer update actions from keys with no specified action
		measurementActions.putAll(measurementKeys.stream()
				.filter(key -> measurementActions.get(key) == null)
				.collect(Collectors.toMap(
						Function.identity(),
						key -> DatabaseAction.UPDATE
				))
		);
		
		parameterActions.putAll(parameterKeys.stream()
				.filter(key -> parameterActions.get(key) == null)
				.collect(Collectors.toMap(
						Function.identity(),
						key -> DatabaseAction.UPDATE
				))
		);
		
		HashMap<String, Measurement> measurements = new HashMap<>();
		HashMap<String, Parameter> parameters = new HashMap<>();
		
		Map<String, String> newMeasurementNames = measurementActions.keySet().stream()
				.filter(key -> Objects.nonNull(requestParams.get("measurementName" + key)))
				.collect(Collectors.toMap(
						Function.identity(),
						key -> requestParams.get("measurementName" + key)
				));
		Map<String, QuantityTypeId> newMeasurementQuantityTypeIds = measurementActions.keySet().stream()
				.filter(key -> Objects.nonNull(requestParams.get("measurementQuantityTypeId" + key)))
				.collect(Collectors.toMap(
						Function.identity(),
						key -> QuantityTypeId.valueOf(
								requestParams.get("measurementQuantityTypeId" + key)
						)
				));
		Map<String, String> newParameterNames = parameterActions.keySet().stream()
				.filter(key -> Objects.nonNull(requestParams.get("parameterName" + key)))
				.collect(Collectors.toMap(
						Function.identity(),
						key -> requestParams.get("parameterName" + key)
				));
		Map<String, QuantityTypeId> newParameterQuantityTypeIds = parameterActions.keySet().stream()
				.filter(key -> Objects.nonNull(requestParams.get("parameterQuantityTypeId" + key)))
				.collect(Collectors.toMap(
						Function.identity(),
						key -> QuantityTypeId.valueOf(
								requestParams.get("parameterQuantityTypeId" + key)
						)
				));
		ExperimentEditorData editorData = new ExperimentEditorData(requestParams, experimentName, description, submissionDeadline, measurementActions, parameterActions, measurements, parameters,
				newMeasurementNames, newMeasurementQuantityTypeIds, newParameterNames, newParameterQuantityTypeIds);
		return editorData;
	}
	
}
