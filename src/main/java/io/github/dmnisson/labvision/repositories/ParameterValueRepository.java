package io.github.dmnisson.labvision.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.dmnisson.labvision.dto.experiment.ParameterValueForExperimentView;
import io.github.dmnisson.labvision.entities.ParameterValue;

public interface ParameterValueRepository extends JpaRepository<ParameterValue, Integer> {

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.experiment.ParameterValueForExperimentView("
			+ "	pv.id,"
			+ "	pv.variable.id,"
			+ " pv.value.value,"
			+ "	pv.value.uncertainty) "
			+ "FROM ParameterValue pv "
			+ "WHERE pv.measurementValue.id=:measurementvalueid "
			+ "AND pv.variable.id=:parameterid")
	public ParameterValueForExperimentView getForExperimentView(
			@Param("measurementvalueid") Integer measurementValueId, 
			@Param("parameterid") Integer parameterId
			);

}
