package io.github.dmnisson.labvision.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.dmnisson.labvision.dto.experiment.MeasurementForExperimentView;
import io.github.dmnisson.labvision.entities.Measurement;

public interface MeasurementRepository extends JpaRepository<Measurement, Integer> {

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.experiment.MeasurementForExperimentView("
			+ "	m.id,"
			+ "	m.name,"
			+ "	m.quantityTypeId) "
			+ "FROM Measurement m "
			+ "WHERE m.experiment.id=:experimentid "
			+ "ORDER BY LOWER(m.name) ASC")
	List<MeasurementForExperimentView> findForExperimentView(@Param("experimentid") Integer experimentId);

}
