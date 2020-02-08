package io.github.dmnisson.labvision.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.dmnisson.labvision.dto.experiment.MeasurementForErrorsView;
import io.github.dmnisson.labvision.dto.experiment.MeasurementInfo;
import io.github.dmnisson.labvision.entities.Measurement;

public interface MeasurementRepository extends JpaRepository<Measurement, Integer> {

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.experiment.MeasurementInfo("
			+ "	m.id,"
			+ "	m.name,"
			+ "	m.quantityTypeId) "
			+ "FROM Measurement m "
			+ "WHERE m.experiment.id=:experimentid "
			+ "ORDER BY LOWER(m.name) ASC")
	List<MeasurementInfo> findForExperimentView(@Param("experimentid") Integer experimentId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.experiment.MeasurementForErrorsView("
			+ "	m.id,"
			+ "	m.name,"
			+ "	m.quantityTypeId,"
			+ "	m.mean.value,"
			+ "	m.sampleStandardDeviation.value,"
			+ " COUNT(m.id)"
			+ ") "
			+ "FROM Measurement m "
			+ "LEFT JOIN m.values mv "
			+ "WHERE m.experiment.id=:experimentid "
			+ "GROUP BY m.id "
			+ "ORDER BY LOWER(m.name) ASC")
	List<MeasurementForErrorsView> findMeasurementsForErrorsView(@Param("experimentid") Integer experimentId);

}
