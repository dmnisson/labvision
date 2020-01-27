package io.github.dmnisson.labvision.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.dmnisson.labvision.dto.reportedresult.ReportForReportView;
import io.github.dmnisson.labvision.dto.student.experiment.ReportedResultForStudentExperimentView;
import io.github.dmnisson.labvision.dto.student.reports.ReportForStudentReportsTable;
import io.github.dmnisson.labvision.entities.ReportedResult;

public interface ReportedResultRepository extends JpaRepository<ReportedResult, Integer> {

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.student.reports.ReportForStudentReportsTable("
			+ "	rr.id,"
			+ "	rr.name,"
			+ "	rr.experiment.id,"
			+ "	rr.experiment.name,"
			+ "	rr.experiment.course.id,"
			+ "	rr.experiment.course.name,"
			+ "	rr.score,"
			+ "	rr.added,"
			+ "	CASE WHEN (CURRENT_TIMESTAMP() <= rr.experiment.reportDueDate) THEN TRUE ELSE FALSE END"
			+ ") FROM ReportedResult rr "
			+ "WHERE rr.student.id=:studentid "
			+ "ORDER BY rr.added DESC")
	List<ReportForStudentReportsTable> findReportsForStudentReportsTable(@Param("studentid") Integer studentId);
	
	@Query(	"SELECT new io.github.dmnisson.labvision.dto.student.experiment.ReportedResultForStudentExperimentView(" +
		    "	rr.id," +
			"	rr.name," +
		    "	rd.filename," +
		    "	rr.added," +
		    "	rr.score" +
		    ") " +
		    "FROM ReportedResult rr " +
		    "LEFT JOIN rr.reportDocument rd " +
		    "WHERE rr.experiment.id=:experimentid AND rr.student.id=:studentid " +
		    "ORDER BY rr.added DESC")
	List<ReportedResultForStudentExperimentView> findReportsForStudentExperimentView(
			@Param("experimentid") Integer experimentId, @Param("studentid") Integer studentId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.reportedresult.ReportForReportView(" +
			"	rr.id," +
			"	rr.experiment.id," +
			"	rr.name," +
			"	rr.reportDocument.fileType," +
			"	rr.reportDocument.documentType," +
			"	rr.reportDocument.filename," +
			"	rr.reportDocument.lastUpdated," +
			"	rr.score" +
			") FROM ReportedResult rr " +
			"WHERE rr.id=:reportid")
	Optional<ReportForReportView> findForReportView(@Param("reportid") Integer reportId);
}
