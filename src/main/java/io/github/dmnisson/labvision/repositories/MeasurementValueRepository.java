package io.github.dmnisson.labvision.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.dmnisson.labvision.dto.experiment.MeasurementValueForExperimentView;
import io.github.dmnisson.labvision.entities.MeasurementValue;

public interface MeasurementValueRepository extends JpaRepository<MeasurementValue, Integer> {

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.experiment.MeasurementValueForExperimentView("
			+ "	mv.id,"
			+ "	m.id,"
			+ "	m.name,"
			+ "	mv.value.value,"
			+ "	mv.value.uncertainty,"
			+ "	mv.taken,"
			+ "	m.dimension,"
			+ "	m.quantityTypeId) "
			+ "FROM MeasurementValue mv "
			+ "JOIN mv.variable m "
			+ "WHERE m.id=:measurementid AND mv.student.id=:studentid")
	List<MeasurementValueForExperimentView> findForStudentExperimentView(
			@Param("measurementid") Integer measurementId, @Param("studentid") Integer studentId);

}
