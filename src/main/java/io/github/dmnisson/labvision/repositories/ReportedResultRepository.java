package io.github.dmnisson.labvision.repositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import io.github.dmnisson.labvision.dto.faculty.ReportForFacultyExperimentView;
import io.github.dmnisson.labvision.dto.reportedresult.ReportForAdminReportView;
import io.github.dmnisson.labvision.dto.reportedresult.ReportForFacultyReportView;
import io.github.dmnisson.labvision.dto.reportedresult.ReportForReportView;
import io.github.dmnisson.labvision.dto.reportedresult.ReportedResultForAdminTable;
import io.github.dmnisson.labvision.dto.reportedresult.ReportedResultInfo;
import io.github.dmnisson.labvision.dto.result.ResultInfo;
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
	Page<ReportForStudentReportsTable> findReportsForStudentReportsTable(@Param("studentid") Integer studentId, Pageable pageable);
	
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

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.faculty.ReportForFacultyExperimentView("
			+ "	rr.id,"
			+ " rr.student.id,"
			+ " rr.name,"
			+ "	rr.added,"
			+ "	rr.score"
			+ ") FROM ReportedResult rr "
			+ "WHERE rr.experiment.id=:experimentid "
			+ "ORDER BY rr.added DESC")
	List<ReportForFacultyExperimentView> findReportsForFacultyExperimentView(@Param("experimentid") Integer experimentId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.reportedresult.ReportForFacultyReportView(" +
			"	rr.id," +
			"	rr.experiment.id," +
			"	rr.name," +
			"	rr.reportDocument.fileType," +
			"	rr.reportDocument.documentType," +
			"	rr.reportDocument.filename," +
			"	rr.reportDocument.lastUpdated," +
			"	rr.score," +
			"	rr.student.id," +
			"	rr.student.name" +
			") FROM ReportedResult rr " +
			"WHERE rr.id=:reportid")
	ReportForFacultyReportView findReportForFacultyReportView(@Param("reportid") Integer reportId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.result.ResultInfo(" +
			"	ar.id," +
			"	ar.name," +
			"	ar.value.value," +
			"	ar.value.uncertainty," +
			"	ar.variable.quantityTypeId" +
			") FROM ReportedResult rr " +
			"JOIN rr.experiment e " +
			"JOIN e.acceptedResults ar " +
			"WHERE rr.id=:reportid")
	List<ResultInfo> findAcceptedResultsForReportedResult(@Param("reportid") Integer reportId);

	@Modifying
	@Transactional
	@Query("UPDATE ReportedResult SET score = :score WHERE id=:reportid")
	void updateReportScore(@Param("reportid") Integer reportId, @Param("score") BigDecimal score);

	@Query(	"SELECT COUNT(rr.id) FROM ReportedResult rr "
			+ "JOIN rr.experiment e "
			+ "JOIN e.course c "
			+ "JOIN c.courseClasses cc "
			+ "JOIN cc.instructors i "
			+ "WHERE i.id=:instructorid AND rr.score IS NULL")
	Long countUnscoredReportsForInstructor(@Param("instructorid") Integer instructorId);

	@Query( "SELECT new io.github.dmnisson.labvision.dto.reportedresult.ReportedResultInfo("
			+ "	rr.id,"
			+ "	rr.name,"
			+ "	rr.added"
			+ ") FROM Experiment e "
			+ "JOIN e.reportedResults rr "
			+ "WHERE e.id=:experimentid")
	List<ReportedResultInfo> findForExperimentView(@Param("experimentid") Integer experimentId);

	@Query( "SELECT new io.github.dmnisson.labvision.dto.reportedresult.ReportedResultForAdminTable("
			+ "	rr.id,"
			+ "	rr.name,"
			+ "	rr.added,"
			+ "	s.id,"
			+ "	s.username,"
			+ "	rr.score"
			+ ") FROM Experiment e "
			+ "JOIN e.reportedResults rr "
			+ "JOIN rr.student s "
			+ "WHERE e.id=:experimentid")
	Page<ReportedResultForAdminTable> findForAdminByExperimentId(@Param("experimentid") Integer experimentId, Pageable pageable);
	
	@Query(	"SELECT new io.github.dmnisson.labvision.dto.reportedresult.ReportForAdminReportView("
			+ "	rr.id,"
			+ "	rr.experiment.id,"
			+ "	rr.name,"
			+ "	rd.fileType,"
			+ "	rd.documentType,"
			+ "	rd.filename,"
			+ "	rd.lastUpdated," 
			+ "	rr.score,"
			+ "	rr.student.username"
			+ ") FROM ReportedResult rr "
			+ "LEFT JOIN rr.reportDocument rd "
			+ "WHERE rr.id=:reportid")
	Optional<ReportForAdminReportView> findForAdminById(@Param("reportid") Integer reportId);
}
