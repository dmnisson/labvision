package io.github.dmnisson.labvision.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.dmnisson.labvision.dto.course.CourseForAdminTable;
import io.github.dmnisson.labvision.dto.experiment.ExperimentForAdmin;
import io.github.dmnisson.labvision.dto.experiment.ExperimentForAdminDetail;
import io.github.dmnisson.labvision.dto.experiment.ExperimentInfo;
import io.github.dmnisson.labvision.dto.faculty.ExperimentForFacultyExperimentTable;
import io.github.dmnisson.labvision.dto.result.ResultInfo;
import io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentExperimentTable;
import io.github.dmnisson.labvision.dto.student.experiment.PastExperimentForStudentExperimentTable;
import io.github.dmnisson.labvision.dto.student.experiment.RecentExperimentForStudentDashboard;
import io.github.dmnisson.labvision.entities.Experiment;

public interface ExperimentRepository extends JpaRepository<Experiment, Integer> {
	/**
	 * JPQL expression used to determine date of last measurement value or report of an experiment
	 */
	public static final String EXPERIMENT_LAST_UPDATED_FUNCTION =
			"	MAX(" +
			"		CASE WHEN (rr.added IS NULL OR mv.taken > rr.added)" +
			"			THEN mv.taken" +
			" 			ELSE rr.added" +
			"			END" +
			"		)";
	
	@Query( "SELECT new io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentDashboard(" +
			"	e.id," +
			"	e.name," +
			"	c.id," +
			"	c.name," +
			EXPERIMENT_LAST_UPDATED_FUNCTION + " AS lu," +
			"	e.reportDueDate" +
			") " +
			"FROM Student s " +
			"JOIN s.activeExperiments e " +
			"JOIN e.course c " +
			"LEFT JOIN e.measurements m " +
			"LEFT JOIN m.values mv " +
			"LEFT JOIN e.reportedResults rr " +
			"LEFT JOIN mv.student s2 " +
			"LEFT JOIN rr.student s3 " +
			"WHERE s.id=:studentid AND " +
			"(s2.id IS NULL OR s2.id=:studentid) AND " +
			"(s3.id IS NULL OR s3.id=:studentid) AND " +
			"rr.added IS NULL " +
			"GROUP BY e.id, e.name, c.id, c.name " + 
			"ORDER BY e.reportDueDate ASC" )
	public List<CurrentExperimentForStudentDashboard> findCurrentExperimentsForStudentDashboardNoReports(
			@Param("studentid") Integer studentId);
	
	@Query( "SELECT new io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentDashboard(" +
			"	e.id," +
			"	e.name," +
			"	c.id," +
			"	c.name," +
			EXPERIMENT_LAST_UPDATED_FUNCTION + " AS lu," +
			"	e.reportDueDate" +
			") " +
			"FROM Student s " +
			"JOIN s.activeExperiments e " +
			"JOIN e.course c " +
			"LEFT JOIN e.measurements m " +
			"LEFT JOIN m.values mv " +
			"LEFT JOIN e.reportedResults rr " +
			"LEFT JOIN mv.student s2 " +
			"LEFT JOIN rr.student s3 " +
			"WHERE s.id=:studentid AND " +
			"(s2.id IS NULL OR s2.id=:studentid) AND " +
			"(s3.id IS NULL OR s3.id=:studentid) AND " +
			"rr.added IS NOT NULL " +
			"GROUP BY e.id, e.name, c.id, c.name " +
			"ORDER BY lu DESC" )
	public List<CurrentExperimentForStudentDashboard> findCurrentExperimentsForStudentDashboardWithReports(
			@Param("studentid") Integer studentId);

	@Query( "SELECT new io.github.dmnisson.labvision.dto.student.experiment.RecentExperimentForStudentDashboard(" +
		    "	e.id," +
		    "	e.name," +
		    EXPERIMENT_LAST_UPDATED_FUNCTION + " AS lu," +
		    "	MAX(mv.taken)," +
		    "	e.reportDueDate" +
			") " +
			"FROM Experiment e " +
			"LEFT JOIN e.measurements m " +
			"LEFT JOIN m.values mv " +
			"LEFT JOIN mv.student s " +
			"LEFT JOIN e.reportedResults rr " +
			"LEFT JOIN rr.student s2 " +
			"WHERE (s.id IS NULL OR s.id=:studentid) AND " +
			"(s2.id IS NULL OR s2.id=:studentid) AND " +
			"(s.id IS NOT NULL OR s2.id IS NOT NULL) AND " +
			"rr.added IS NULL " +
			"GROUP BY e.id, e.name " +
			"ORDER BY e.reportDueDate ASC"
			)
	public List<RecentExperimentForStudentDashboard> findRecentExperimentsForStudentDashboardNoReports(
			@Param("studentid") Integer studentId);
	
	@Query( "SELECT new io.github.dmnisson.labvision.dto.student.experiment.RecentExperimentForStudentDashboard(" +
		    "	e.id," +
		    "	e.name," +
		    EXPERIMENT_LAST_UPDATED_FUNCTION + " AS lu," +
		    "	MAX(mv.taken)," +
		    "	e.reportDueDate" +
			") " +
			"FROM Experiment e " +
			"LEFT JOIN e.measurements m " +
			"LEFT JOIN m.values mv " +
			"LEFT JOIN mv.student s " +
			"LEFT JOIN e.reportedResults rr " +
			"LEFT JOIN rr.student s2 " +
			"WHERE (s.id IS NULL OR s.id=:studentid) AND " +
			"(s2.id IS NULL OR s2.id=:studentid) AND " +
			"(s.id IS NOT NULL OR s2.id IS NOT NULL) AND " +
			"rr.added IS NOT NULL " +
			"GROUP BY e.id, e.name " +
			"ORDER BY lu DESC"
			)
	public List<RecentExperimentForStudentDashboard> findRecentExperimentsForStudentDashboardWithReports(
			@Param("studentid") Integer studentId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentExperimentTable(" +
			"	e.id," +
			"	e.name," +
			EXPERIMENT_LAST_UPDATED_FUNCTION + " AS lu," +
			"	e.reportDueDate," +
			"	MAX(rr.added)," +
			"	CASE WHEN COUNT(rr.score) > 0 THEN SUM(rr.score) ELSE 0 END" +
			") " +
			"FROM Student s " +
			"JOIN s.activeExperiments e " +
			"LEFT JOIN e.reportedResults rr ON rr.student.id=:studentid " +
			"LEFT JOIN e.measurements m " +
			"LEFT JOIN m.values mv ON mv.student.id=:studentid " +
			"WHERE s.id=:studentid " +
			"GROUP BY e " +
			"ORDER BY lu DESC NULLS FIRST, e.reportDueDate ASC")
	public List<CurrentExperimentForStudentExperimentTable> findCurrentExperimentsForStudentExperimentTable(
			@Param("studentid") Integer studentId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.student.experiment.PastExperimentForStudentExperimentTable(" +
			"	e.id," +
			"	e.name," +
			EXPERIMENT_LAST_UPDATED_FUNCTION + " AS lu," +
			"	COUNT(rr.added)," +
			"	MAX(rr.added)," +
			"	CASE WHEN COUNT(rr.score) > 0 THEN SUM(rr.score) ELSE 0 END" +
			") " +
			"FROM Experiment e " +
			"JOIN e.course c " +
			"JOIN c.courseClasses cc " +
			"JOIN cc.students s " +
			"LEFT JOIN e.reportedResults rr ON rr.student.id=:studentid " +
			"LEFT JOIN e.measurements m " +
			"LEFT JOIN m.values mv ON mv.student.id=:studentid " +
			"WHERE s.id=:studentid " +
			"GROUP BY e " +
			"ORDER BY lu DESC NULLS FIRST")
	public List<PastExperimentForStudentExperimentTable> findPastExperimentsForStudentExperimentTable(
			@Param("studentid") Integer studentId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.experiment.ExperimentInfo(" +
			"	e.id," +
			"	e.name," +
			"	e.course.name" +
			") FROM Experiment e " +
			"WHERE e.id=:experimentid")
	public Optional<ExperimentInfo> findExperimentInfo(@Param("experimentid") Integer experimentId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.result.ResultInfo(" +
			"	ar.id," +
			"	ar.name," +
			"	ar.value.value," +
			"	ar.value.uncertainty," +
			"	ar.variable.quantityTypeId" +
			") FROM Experiment e " +
			"JOIN e.acceptedResults ar " +
			"WHERE e.id=:experimentid")
	public List<ResultInfo> getAcceptedResultInfoFor(@Param("experimentid") Integer experimentId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.faculty.ExperimentForFacultyExperimentTable(" +
			"	e.id," +
			"	e.name," +
			"	COUNT(rr)," +
			"	SUM(rr.score)/COUNT(DISTINCT s)" +
			") " +
			"FROM Experiment e " +
			"LEFT JOIN e.instructors i1 " +
			"LEFT JOIN e.course c " +
			"LEFT JOIN c.courseClasses cc " +
			"LEFT JOIN cc.instructors i2 " +
			"LEFT JOIN e.reportedResults rr " +
			"LEFT JOIN rr.student s " +
			"WHERE i1.id=:instructorid OR i2.id=:instructorid " +
			"GROUP BY e")
	public List<ExperimentForFacultyExperimentTable> findExperimentsForFacultyExperimentTable(
			@Param("instructorid") Integer instructorId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.experiment.ExperimentInfo("
			+ "	e.id,"
			+ "	e.name,"
			+ "	c.name"
			+ ") FROM Experiment e "
			+ "JOIN e.course c "
			+ "LEFT JOIN c.courseClasses cc "
			+ "LEFT JOIN e.activeStudents acs "
			+ "LEFT JOIN cc.students ccs "
			+ "WHERE acs.id=:studentid OR ccs.id=:studentid")
	public List<ExperimentInfo> findExperimentInfoForStudent(@Param("studentid") Integer studentId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.student.experiment.CurrentExperimentForStudentExperimentTable(" +
			"	e.id," +
			"	e.name," +
			EXPERIMENT_LAST_UPDATED_FUNCTION + " AS lu," +
			"	e.reportDueDate," +
			"	MAX(rr.added)," +
			"	CASE WHEN COUNT(rr.score) > 0 THEN SUM(rr.score) ELSE 0 END" +
			") " +
			"FROM Student s " +
			"JOIN s.activeExperiments e " +
			"JOIN e.course c " +
			"LEFT JOIN e.reportedResults rr ON rr.student.id=:studentid " +
			"LEFT JOIN e.measurements m " +
			"LEFT JOIN m.values mv ON mv.student.id=:studentid " +
			"WHERE s.id=:studentid AND c.id=:courseid " +
			"GROUP BY e " +
			"ORDER BY lu DESC NULLS FIRST, e.reportDueDate ASC")
	public List<CurrentExperimentForStudentExperimentTable> findCurrentExperimentsForStudentCourseExperimentTable(
			@Param("studentid") Integer studentId,
			@Param("courseid") Integer courseId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.student.experiment.PastExperimentForStudentExperimentTable(" +
			"	e.id," +
			"	e.name," +
			EXPERIMENT_LAST_UPDATED_FUNCTION + " AS lu," +
			"	COUNT(rr.added)," +
			"	MAX(rr.added)," +
			"	CASE WHEN COUNT(rr.score) > 0 THEN SUM(rr.score) ELSE 0 END" +
			") " +
			"FROM Experiment e " +
			"JOIN e.course c " +
			"JOIN c.courseClasses cc " +
			"JOIN cc.students s " +
			"LEFT JOIN e.reportedResults rr ON rr.student.id=:studentid " +
			"LEFT JOIN e.measurements m " +
			"LEFT JOIN m.values mv ON mv.student.id=:studentid " +
			"WHERE s.id=:studentid AND c.id=:courseid " +
			"GROUP BY e " +
			"ORDER BY lu DESC NULLS FIRST")
	public List<PastExperimentForStudentExperimentTable> findPastExperimentsForStudentCourseExperimentTable(
			@Param("studentid") Integer studentId,
			@Param("courseid") Integer courseId);

	@Query(	"SELECT COUNT(DISTINCT e.id) FROM Instructor i "
			+ "LEFT JOIN i.courseClasses cc "
			+ "LEFT JOIN cc.course c "
			+ "LEFT JOIN c.experiments e1 "
			+ "LEFT JOIN i.experiments e2 "
			+ "JOIN Experiment e ON e.id=e1.id OR e.id=e2.id "
			+ "WHERE i.id=:instructorid "
			+ "GROUP BY i.id")
	public Long countExperimentsForInstructor(@Param("instructorid") Integer instructorId);

	@Query(	"SELECT e FROM Experiment e "
			+ "JOIN e.instructors i "
			+ "WHERE e.id=:experimentid AND i.id=:instructorid")
	public Optional<Experiment> findByIdForInstructor(@Param("experimentid") Integer experimentId, @Param("instructorid") int instructorId);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.experiment.ExperimentForAdmin("
			+ "	e.id,"
			+ "	e.name,"
			+ "	COUNT(DISTINCT i.id),"
			+ "	COUNT(DISTINCT s.id),"
			+ "	COUNT(DISTINCT m.id),"
			+ "	COUNT(DISTINCT rr.id)"
			+ ") FROM Course c "
			+ "JOIN c.experiments e "
			+ "LEFT JOIN e.instructors i "
			+ "LEFT JOIN e.activeStudents s "
			+ "LEFT JOIN e.measurements m "
			+ "LEFT JOIN e.reportedResults rr "
			+ "WHERE c.id=:courseid "
			+ "GROUP BY e.id, e.name")
	public Page<ExperimentForAdmin> findForAdminByCourseId(@Param("courseid") Integer courseId, Pageable pageable);

	@Query(	"SELECT new io.github.dmnisson.labvision.dto.experiment.ExperimentForAdminDetail("
			+ "	e.id,"
			+ "	e.name,"
			+ "	COUNT(DISTINCT i.id),"
			+ "	COUNT(DISTINCT s.id),"
			+ "	COUNT(DISTINCT m.id),"
			+ "	COUNT(DISTINCT rr.id),"
			+ "	e.description"
			+ ") FROM Experiment e "
			+ "LEFT JOIN e.instructors i "
			+ "LEFT JOIN e.activeStudents s "
			+ "LEFT JOIN e.measurements m "
			+ "LEFT JOIN e.reportedResults rr "
			+ "WHERE e.id=:experimentid "
			+ "GROUP BY e.id, e.name")
	public Optional<ExperimentForAdminDetail> findForAdminById(@Param("experimentid") Integer experimentId);
	
		
}
