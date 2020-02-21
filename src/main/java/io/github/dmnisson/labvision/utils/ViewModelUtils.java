package io.github.dmnisson.labvision.utils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.ui.Model;

import io.github.dmnisson.labvision.entities.Experiment;
import io.github.dmnisson.labvision.entities.QuantityTypeId;
import io.github.dmnisson.labvision.models.ExperimentViewModel;

public class ViewModelUtils {

	public static <MVGROUPING> Experiment buildExperimentModelAttributes(
			ExperimentViewModel<MVGROUPING> experimentViewModel, Model model) {
		model.addAttribute("experiment", experimentViewModel.getExperiment());
		model.addAttribute("measurements", experimentViewModel.getMeasurements());
		model.addAttribute("parameters", experimentViewModel.getParameters());
		model.addAttribute("measurementValues", experimentViewModel.getMeasurementValues());
		model.addAttribute("parameterValues", experimentViewModel.getParameterValues());
		
		model.addAttribute("course", experimentViewModel.getExperiment().getCourse());
		model.addAttribute("name", experimentViewModel.getExperiment().getName());
		model.addAttribute("description", experimentViewModel.getExperiment().getDescription());
		model.addAttribute("reportDueDate", experimentViewModel.getExperiment().getReportDueDate());
		
		buildQuantityTypeIdValues(model);
		
		return experimentViewModel.getExperiment();
	}

	public static void buildQuantityTypeIdValues(Model model) {
		model.addAttribute(
				"quantityTypeIdValues", 
				Stream.of(QuantityTypeId.values())
					.filter(qt -> !qt.equals(QuantityTypeId.UNKNOWN))
					.sorted((qt1, qt2) -> qt1.getDisplayName().compareTo(qt2.getDisplayName()))
					.collect(Collectors.toList())
				);
	}

}
