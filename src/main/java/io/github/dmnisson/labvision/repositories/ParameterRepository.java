package io.github.dmnisson.labvision.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.dmnisson.labvision.dto.experiment.ParameterForExperimentView;
import io.github.dmnisson.labvision.entities.Parameter;

public interface ParameterRepository extends JpaRepository<Parameter, Integer> {

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.experiment.ParameterForExperimentView(" +
			"	p.id," +
			"	p.name," +
			"	p.quantityTypeId) " +
			"FROM Parameter p " +
			"WHERE p.measurement.id=:measurementid " +
			"ORDER BY LOWER(p.name) ASC")
	List<ParameterForExperimentView> findForExperimentView(@Param("measurementid") Integer measurementId);

}
