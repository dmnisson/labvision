package io.github.dmnisson.labvision.admin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import io.github.dmnisson.labvision.DashboardUrlService;
import io.github.dmnisson.labvision.ReportedResultService;
import io.github.dmnisson.labvision.ResourceNotFoundException;
import io.github.dmnisson.labvision.auth.LabVisionUserDetails;
import io.github.dmnisson.labvision.auth.LabVisionUserDetailsManager;
import io.github.dmnisson.labvision.auth.ResetPasswordController;
import io.github.dmnisson.labvision.dto.admin.LabVisionUserForAdminTable;
import io.github.dmnisson.labvision.dto.admin.LabVisionUserInfo;
import io.github.dmnisson.labvision.dto.course.CourseClassForAdminTable;
import io.github.dmnisson.labvision.dto.course.CourseForAdminTable;
import io.github.dmnisson.labvision.dto.course.CourseInfo;
import io.github.dmnisson.labvision.dto.experiment.ExperimentForAdmin;
import io.github.dmnisson.labvision.dto.experiment.ExperimentForAdminDetail;
import io.github.dmnisson.labvision.dto.experiment.ExperimentInfo;
import io.github.dmnisson.labvision.dto.experiment.MeasurementInfo;
import io.github.dmnisson.labvision.dto.reportedresult.ReportForAdminReportView;
import io.github.dmnisson.labvision.dto.reportedresult.ReportForReportView;
import io.github.dmnisson.labvision.dto.reportedresult.ReportedResultForAdminTable;
import io.github.dmnisson.labvision.dto.reportedresult.ReportedResultInfo;
import io.github.dmnisson.labvision.dto.result.ResultInfo;
import io.github.dmnisson.labvision.entities.AdminInfo;
import io.github.dmnisson.labvision.entities.Course;
import io.github.dmnisson.labvision.entities.CourseClass;
import io.github.dmnisson.labvision.entities.Experiment;
import io.github.dmnisson.labvision.entities.Instructor;
import io.github.dmnisson.labvision.entities.LabVisionUser;
import io.github.dmnisson.labvision.entities.ReportDocumentType;
import io.github.dmnisson.labvision.entities.ReportedResult;
import io.github.dmnisson.labvision.entities.Student;
import io.github.dmnisson.labvision.entities.UserRole;
import io.github.dmnisson.labvision.experiment.ExperimentService;
import io.github.dmnisson.labvision.models.NavbarModel;
import io.github.dmnisson.labvision.reportdocs.ReportDocumentService;
import io.github.dmnisson.labvision.repositories.CourseClassRepository;
import io.github.dmnisson.labvision.repositories.CourseRepository;
import io.github.dmnisson.labvision.repositories.ExperimentRepository;
import io.github.dmnisson.labvision.repositories.InstructorRepository;
import io.github.dmnisson.labvision.repositories.MeasurementRepository;
import io.github.dmnisson.labvision.repositories.ReportedResultRepository;
import io.github.dmnisson.labvision.repositories.StudentRepository;
import io.github.dmnisson.labvision.utils.EditorUtils;
import io.github.dmnisson.labvision.utils.ExperimentEditorData;
import io.github.dmnisson.labvision.utils.PaginationUtils;
import io.github.dmnisson.labvision.utils.ViewModelUtils;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private DashboardUrlService dashboardUrlService;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private CourseClassRepository courseClassRepository;
	
	@Autowired
	private ExperimentRepository experimentRepository;
	
	@Autowired
	private MeasurementRepository measurementRepository;
	
	@Autowired
	private ReportedResultRepository reportedResultRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private InstructorRepository instructorRepository;
	
	@Autowired
	private LabVisionUserDetailsManager userDetailsManager;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private ExperimentService experimentService;

	@Autowired
	private ReportDocumentService reportDocumentService;
	
	@Autowired
	private ReportedResultService reportedResultService;
	
	@ModelAttribute
	public void populateModel(Model model, @AuthenticationPrincipal LabVisionUserDetails userDetails) {
		NavbarModel navbarModel = buildAdminNavbar(userDetails);
		model.addAttribute("navbarModel", navbarModel);
	}

	@GetMapping("/dashboard")
	public String dashboard(@AuthenticationPrincipal LabVisionUserDetails userDetails, Model model) {
		model.addAttribute("user", userDetails.getLabVisionUser());
		
		model.addAttribute("numOfCourses", courseRepository.count());
		model.addAttribute("numOfStudents", userDetailsManager.countStudents());
		model.addAttribute("numOfInstructors", userDetailsManager.countInstructors());
		model.addAttribute("numOfAdmins", userDetailsManager.countAdmins());
		
		return "admin/dashboard";
	}
	
	@GetMapping("/courses")
	public String courses(Model model, Pageable pageable) {
		Page<Course> coursePage = courseRepository.findAll(pageable);
		
		Page<CourseForAdminTable> courses = new PageImpl<CourseForAdminTable>(
				coursePage.stream()
					.map(course -> new CourseForAdminTable(
							course.getId(), 
							course.getName(),
							Long.valueOf(course.getCourseClasses().size()),
							Long.valueOf(course.getExperiments().size())
							))
					.collect(Collectors.toList()),
					pageable,
					courseRepository.count()
				);
		
		model.addAttribute("courses", courses);
		
		PaginationUtils.addPageModelAttributes(model, courses, null, AdminController.class, "courses", null, null);
		
		return "admin/courses";
	}

	@GetMapping("/course/{id}")
	public String getCourse(@PathVariable Integer id, Model model) {
		CourseForAdminTable course = courseRepository.findForAdminTable(id)
				.orElseThrow(() -> new ResourceNotFoundException(Course.class, id));
		
		model.addAttribute("course", course);
		
		return "admin/course";
	}
	
	@GetMapping("/course/new")
	public String newCourse(Model model) {
		model.addAttribute("actionUrl", MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "createCourse", new String())
				.replaceQuery(null)
				.build()
				.toUriString()
				);
		
		return "editors/editcourse";
	}
	
	@PostMapping("/course/new")
	public String createCourse(String name) {
		Course course = new Course();
		
		course.setName(name);
		
		course = courseRepository.save(course);
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "getCourse", course.getId(), new Object())
				.build()
				.toUriString();
	}	
	
	@GetMapping("/course/edit/{id}")
	public String editCourse(@PathVariable Integer id, Model model) {
		Course course = courseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Course.class, id));
		
		model.addAttribute("course", course);
		
		model.addAttribute("actionUrl", MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "updateCourse", id, new String())
				.replaceQuery(null)
				.build()
				.toUriString()
				);
		
		return "editors/editcourse";
	}
	
	@PostMapping("/course/edit/{id}")
	public String updateCourse(@PathVariable Integer id, String name) {
		Course course = courseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Course.class, id));
		
		course.setName(name);
		
		course = courseRepository.save(course);
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "getCourse", course.getId(), new Object())
				.build()
				.toUriString();
	}
	
	@GetMapping("/course/delete/{id}")
	public String deleteCourse(@PathVariable Integer id, Model model) {
		Course course = courseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Course.class, id));
		
		model.addAttribute("course", course);
		
		return "admin/deletecourse";
	}
	
	@PostMapping("/course/delete/{id}")
	public String confirmDeleteCourse(@PathVariable Integer id) {
		courseRepository.deleteById(id);
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "courses", new Object(), new Object())
				.build()
				.toUriString();
	}
	
	@GetMapping("/course/{courseId}/classes")
	public String classes(@PathVariable Integer courseId, Model model, Pageable pageable) {
		CourseInfo course = courseRepository.findCourseInfo(courseId)
				.orElseThrow(() -> new ResourceNotFoundException(Course.class, courseId));
		model.addAttribute("course", course);
		
		Page<CourseClassForAdminTable> classes = courseClassRepository.findForAdminByCourseId(courseId, pageable);
		model.addAttribute("classes", classes);
		
		PaginationUtils.addPageModelAttributes(model, classes, null, AdminController.class, "classes", courseId, null, null);
		
		return "admin/classes";
	}
	
	@GetMapping("/class/{courseClassId}")
	public String getCourseClass(@PathVariable Integer courseClassId, Model model) {
		CourseClassForAdminTable courseClass = courseClassRepository.findForAdminById(courseClassId)
				.orElseThrow(() -> new ResourceNotFoundException(CourseClass.class, courseClassId));
		model.addAttribute("courseClass", courseClass);
		
		CourseInfo course = courseRepository.findCourseInfoByCourseClassId(courseClassId)
				.orElseThrow(() -> new ResourceNotFoundException(CourseClass.class, courseClassId));
		model.addAttribute("course", course);
		
		return "admin/class";
	}
	
	@GetMapping("/class/new/for/{courseId}")
	public String newCourseClass(@PathVariable Integer courseId, Model model) {
		CourseInfo course = courseRepository.findCourseInfo(courseId)
				.orElseThrow(() -> new ResourceNotFoundException(Course.class, courseId));
		model.addAttribute("course", course);
		
		model.addAttribute("actionUrl", MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "createCourseClass", courseId, null)
				.replaceQuery(null)
				.build()
				.toUriString()
				);
		
		return "editors/editclass";
	}
	
	@PostMapping("/class/new/for/{courseId}")
	public String createCourseClass(@PathVariable Integer courseId, String name) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new ResourceNotFoundException(Course.class, courseId));
		
		CourseClass courseClass = course.addCourseClass(name);
		CourseClass savedCourseClass = courseClassRepository.save(courseClass);
		// ensure we save the course with a reference to the correct course class object
		if (savedCourseClass != courseClass) {
			course.removeCourseClass(courseClass);
			course.addCourseClass(savedCourseClass);
		}
		
		course = courseRepository.save(course);
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "getCourseClass", savedCourseClass.getId(), null)
				.build()
				.toUriString();
	}
	
	@GetMapping("/class/edit/{courseClassId}")
	public String editCourseClass(@PathVariable Integer courseClassId, Model model) {
		CourseInfo course = courseRepository.findCourseInfoByCourseClassId(courseClassId)
				.orElseThrow(() -> new ResourceNotFoundException(CourseClass.class, courseClassId));
		model.addAttribute("course", course);
		
		CourseClass courseClass = courseClassRepository.findById(courseClassId)
				.orElseThrow(() -> new ResourceNotFoundException(CourseClass.class, courseClassId));
		model.addAttribute("courseClass", courseClass);
		
		model.addAttribute("actionUrl", MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "updateCourseClass", courseClassId, null)
				.replaceQuery(null)
				.build()
				.toUriString()
				);
		
		return "editors/editclass";
	}
	
	@PostMapping("/class/edit/{courseClassId}")
	public String updateCourseClass(@PathVariable Integer courseClassId, String name) {
		CourseClass courseClass = courseClassRepository.findById(courseClassId)
				.orElseThrow(() -> new ResourceNotFoundException(CourseClass.class, courseClassId));
		
		courseClass.setName(name);
		
		courseClass = courseClassRepository.save(courseClass);
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "getCourseClass", courseClass.getId(), null)
				.build()
				.toUriString();
	}
	
	@GetMapping("/class/delete/{courseClassId}")
	public String deleteCourseClass(@PathVariable Integer courseClassId, Model model) {
		CourseInfo course = courseRepository.findCourseInfoByCourseClassId(courseClassId)
				.orElseThrow(() -> new ResourceNotFoundException(CourseClass.class, courseClassId));
		model.addAttribute("course", course);
		
		CourseClass courseClass = courseClassRepository.findById(courseClassId)
				.orElseThrow(() -> new ResourceNotFoundException(CourseClass.class, courseClassId));
		model.addAttribute("courseClass", courseClass);
		
		return "admin/deleteclass";
	}
	
	@PostMapping("/class/delete/{courseClassId}")
	public String confirmDeleteCourseClass(@PathVariable Integer courseClassId) {
		CourseClass courseClass = courseClassRepository.findById(courseClassId)
				.orElseThrow(() -> new ResourceNotFoundException(CourseClass.class, courseClassId));
		
		Course course = courseClass.getCourse();
		
		course.removeCourseClass(courseClass);
		courseClassRepository.delete(courseClass);
		
		course = courseRepository.save(course);
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "classes", course.getId(), null, null)
				.build()
				.toUriString();
	}
	
	@GetMapping("/class/{courseClassId}/students")
	public String studentsForCourseClass(@PathVariable Integer courseClassId, String error, Model model, Pageable pageable) {
		CourseInfo course = courseRepository.findCourseInfoByCourseClassId(courseClassId)
				.orElseThrow(() -> new ResourceNotFoundException(CourseClass.class, courseClassId));
		model.addAttribute("course", course);
		
		CourseClassForAdminTable courseClass = courseClassRepository.findForAdminById(courseClassId)
				.orElseThrow(() -> new ResourceNotFoundException(CourseClass.class, courseClassId));
		model.addAttribute("courseClass", courseClass);
		
		Page<LabVisionUserInfo> students = studentRepository.findForAdminByCourseClassId(courseClassId, pageable);
		model.addAttribute("students", students);
		
		PaginationUtils.addPageModelAttributes(model, students, null, AdminController.class, "studentsForCourseClass", courseClassId, null, null, null);
		
		model.addAttribute("enrollStudentActionUrl", MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "enrollStudent", courseClassId, null)
				.replaceQuery(null)
				.build()
				.toUriString()
				);
		
		model.addAttribute("error", error);
		
		return "admin/studentsforclass";
	}
	
	@PostMapping("/class/{courseClassId}/enroll")
	public String enrollStudent(@PathVariable Integer courseClassId, String studentUsername) {
		String error = null;
		
		CourseClass courseClass = courseClassRepository.findById(courseClassId)
				.orElseThrow(() -> new ResourceNotFoundException(CourseClass.class, courseClassId));
		
		Optional<Student> student = studentRepository.findByUsername(studentUsername);
		
		if (!student.isPresent()) {
			error = "nostudentfound";
		} else if (studentRepository.existsByUsernameAndCourseClassesId(studentUsername, courseClassId)) {
			error = "alreadyenrolled";
		} else {
			courseClass.addStudent(student.get());
			courseClass = courseClassRepository.save(courseClass);
			studentRepository.save(student.get());
		}
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "studentsForCourseClass", courseClass.getId(), error, null, null)
				.build()
				.toUriString();
	}
	
	@GetMapping("/class/{courseClassId}/leave/{studentId}")
	public String leaveStudent(@PathVariable Integer courseClassId, @PathVariable Integer studentId, Model model) {
		CourseClassForAdminTable courseClass = courseClassRepository.findForAdminById(courseClassId)
				.orElseThrow(() -> new ResourceNotFoundException(CourseClass.class, courseClassId));
		model.addAttribute("courseClass", courseClass);
		
		LabVisionUserInfo student = studentRepository.findInfoById(studentId)
				.orElseThrow(() -> new ResourceNotFoundException(Student.class, studentId));
		model.addAttribute("student", student);
		
		return "admin/leavestudent";
	}
	
	@PostMapping("/class/{courseClassId}/leave/{studentId}")
	public String confirmLeaveStudent(@PathVariable Integer courseClassId, @PathVariable Integer studentId) {
		CourseClass courseClass = courseClassRepository.findById(courseClassId)
				.orElseThrow(() -> new ResourceNotFoundException(CourseClass.class, courseClassId));
		
		Student student = studentRepository.findById(studentId)
				.orElseThrow(() -> new ResourceNotFoundException(Student.class, studentId));
		
		courseClass.removeStudent(student);
		
		courseClass = courseClassRepository.save(courseClass);
		studentRepository.save(student);
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "studentsForCourseClass", courseClass.getId(), null, null, null)
				.build()
				.toUriString();
	}
	
	@GetMapping("/class/{courseClassId}/instructors")
	public String instructorsForCourseClass(@PathVariable Integer courseClassId, String error, Model model, Pageable pageable) {
		
		CourseInfo course = courseRepository.findCourseInfoByCourseClassId(courseClassId)
				.orElseThrow(() -> new ResourceNotFoundException(CourseClass.class, courseClassId));
		model.addAttribute("course", course);
		
		CourseClassForAdminTable courseClass = courseClassRepository.findForAdminById(courseClassId)
				.orElseThrow(() -> new ResourceNotFoundException(CourseClass.class, courseClassId));
		model.addAttribute("courseClass", courseClass);
		
		Page<LabVisionUserInfo> instructors = instructorRepository.findForAdminByCourseClassId(courseClassId, pageable);
		model.addAttribute("instructors", instructors);
		
		PaginationUtils.addPageModelAttributes(model, instructors, null, AdminController.class, "instructorsForCourseClass", courseClassId, null, null, null);
		
		model.addAttribute("assignInstructorActionUrl", MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "assignInstructor", courseClassId, null)
				.replaceQuery(null)
				.build()
				.toUriString()
				);
		
		model.addAttribute("error", error);
		
		return "admin/instructorsforclass";
	}
	
	@PostMapping("/class/{courseClassId}/assign")
	public String assignInstructor(@PathVariable Integer courseClassId, String instructorUsername) {
		String error = null;
		
		CourseClass courseClass = courseClassRepository.findById(courseClassId)
				.orElseThrow(() -> new ResourceNotFoundException(CourseClass.class, courseClassId));
		
		Optional<Instructor> instructor = instructorRepository.findByUsername(instructorUsername);
		
		if (!instructor.isPresent()) {
			error = "noinstructorfound";
		} else if (instructorRepository.existsByUsernameAndCourseClassesId(instructorUsername, courseClassId)) {
			error = "instructoralreadyassigned";
		} else {
			courseClass.addInstructor(instructor.get());
			courseClass = courseClassRepository.save(courseClass);
			instructorRepository.save(instructor.get());
		}
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "instructorsForCourseClass", courseClass.getId(), error, null, null)
				.build()
				.toUriString();
	}
	
	@GetMapping("/class/{courseClassId}/unassign/{instructorId}")
	public String unassignInstructor(@PathVariable Integer courseClassId, @PathVariable Integer instructorId, Model model) {
		CourseClassForAdminTable courseClass = courseClassRepository.findForAdminById(courseClassId)
				.orElseThrow(() -> new ResourceNotFoundException(CourseClass.class, courseClassId));
		model.addAttribute("courseClass", courseClass);
		
		LabVisionUserInfo instructor = instructorRepository.findInfoById(instructorId)
				.orElseThrow(() -> new ResourceNotFoundException(Instructor.class, instructorId));
		model.addAttribute("instructor", instructor);
		
		return "admin/unassigninstructor";
	}
	
	@PostMapping("/class/{courseClassId}/unassign/{instructorId}")
	public String confirmUnassignInstructor(@PathVariable Integer courseClassId, @PathVariable Integer instructorId) {
		CourseClass courseClass = courseClassRepository.findById(courseClassId)
				.orElseThrow(() -> new ResourceNotFoundException(CourseClass.class, courseClassId));
		
		Instructor instructor = instructorRepository.findById(instructorId)
				.orElseThrow(() -> new ResourceNotFoundException(Instructor.class, instructorId));
		
		courseClass.removeInstructor(instructor);
		
		courseClass = courseClassRepository.save(courseClass);
		instructorRepository.save(instructor);
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "instructorsForCourseClass", courseClass.getId(), null, null, null)
				.build()
				.toUriString();
	}
	
	@GetMapping("/course/{courseId}/experiments")
	public String experiments(@PathVariable Integer courseId, Model model, Pageable pageable) {
		CourseInfo course = courseRepository.findCourseInfo(courseId)
				.orElseThrow(() -> new ResourceNotFoundException(Course.class, courseId));
		model.addAttribute("course", course);
		
		Page<ExperimentForAdmin> experiments = experimentRepository.findForAdminByCourseId(courseId, pageable);
		model.addAttribute("experiments", experiments);
		
		PaginationUtils.addPageModelAttributes(model, experiments, null, AdminController.class, "experiments", courseId, null, null);
		
		return "admin/experiments";
	}
	
	@GetMapping("/experiment/{id}")
	public String getExperiment(@PathVariable Integer id, Model model) {
		ExperimentForAdminDetail experiment = experimentRepository.findForAdminById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, id));
		model.addAttribute("experiment", experiment);
		
		CourseInfo course = courseRepository.findCourseInfoByExperimentId(id)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, id));
		model.addAttribute("course", course);
		
		List<MeasurementInfo> measurements = measurementRepository.findForExperimentView(id);
		model.addAttribute("measurements", measurements);
		
		List<ReportedResultInfo> reports = reportedResultRepository.findForExperimentView(id);
		model.addAttribute("reports", reports);
		
		return "admin/experiment";
	}
	
	@GetMapping("/experiment/new/for/{courseId}")
	public String newExperiment(@PathVariable Integer courseId, Model model) {
		
		CourseInfo course = courseRepository.findCourseInfo(courseId)
				.orElseThrow(() -> new ResourceNotFoundException(Course.class, courseId));
		model.addAttribute("course", course);
		
		model.addAttribute("actionURL", MvcUriComponentsBuilder
				.fromMethodName(
						AdminController.class, 
						"createExperiment", 
						courseId, null
						)
				.build()
				.toUriString()
				);
		
		ViewModelUtils.buildQuantityTypeIdValues(model);
		
		return "editors/editexperiment";
	}
	
	@PostMapping("/experiment/new/for/{courseId}")
	public String createExperiment(@PathVariable Integer courseId, @RequestParam Map<String, String> requestParams) {
		ExperimentEditorData editorData = EditorUtils.getExperimentEditorDataFromRequestParams(requestParams);
		
		Experiment experiment = experimentService.createExperiment(courseId, editorData);
		
		return "redirect:" + MvcUriComponentsBuilder.fromMethodName(
					AdminController.class,
					"getExperiment",
					experiment.getId(),
					null
				).build()
				.toUriString();
	}
	
	@GetMapping("/experiment/edit/{id}")
	public String editExperiment(@PathVariable Integer id, Model model) {
		Experiment experiment = ViewModelUtils.buildExperimentModelAttributes(
				experimentService.getExperimentViewModel(id), model);
		
		model.addAttribute("actionURL", MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "updateExperiment", experiment.getId(), null)
				.build()
				.toUriString()
				);
		
		return "editors/editexperiment";
	}
	
	@PostMapping("/experiment/edit/{id}")
	public String updateExperiment(@PathVariable Integer id, @RequestParam Map<String, String> requestParams) {
		ExperimentEditorData editorData = EditorUtils.getExperimentEditorDataFromRequestParams(requestParams);
		
		Experiment experiment = experimentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, id));
		
		experiment = experimentService.updateExperiment(experiment, editorData);
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "getExperiment", experiment.getId(), null)
				.build()
				.toUriString();
	}
	
	@GetMapping("/experiment/delete/{id}")
	public String deleteExperiment(@PathVariable Integer id, Model model) {
		ExperimentInfo experiment = experimentRepository.findExperimentInfo(id)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, id));
		model.addAttribute("experiment", experiment);
		
		CourseInfo course = courseRepository.findCourseInfoByExperimentId(id)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, id));
		model.addAttribute("course", course);
		
		return "admin/deleteexperiment";
	}
	
	@PostMapping("/experiment/delete/{id}")
	public String confirmDeleteExperiment(@PathVariable Integer id) {
		Experiment experiment = experimentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, id));
		
		Course course = experiment.getCourse();
		
		course.removeExperiment(experiment);
		experimentRepository.delete(experiment);
		
		course = courseRepository.save(course);
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "experiments", course.getId(), null, null)
				.build()
				.toUriString();
	}
	
	@GetMapping("/experiment/{experimentId}/instructors")
	public String instructorsForExperiment(@PathVariable Integer experimentId, String error, Model model, Pageable pageable) {
		CourseInfo course = courseRepository.findCourseInfoByExperimentId(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		model.addAttribute("course", course);
		
		ExperimentForAdminDetail experiment = experimentRepository.findForAdminById(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		model.addAttribute("experiment", experiment);
		
		Page<LabVisionUserInfo> instructors = instructorRepository.findForAdminByExperimentId(experimentId, pageable);
		model.addAttribute("instructors", instructors);
		
		PaginationUtils.addPageModelAttributes(model, instructors, null, AdminController.class, "instructorsForExperiment", experimentId, null, null, null);
		
		model.addAttribute("error", error);
		
		model.addAttribute("assignInstructorActionUrl", MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "assignInstructorToExperiment", experimentId, null)
				.replaceQuery(null)
				.build()
				.toUriString()
				);
		
		return "admin/instructorsforexperiment";
	}
	
	@PostMapping("/experiment/{experimentId}/assign")
	public String assignInstructorToExperiment(@PathVariable Integer experimentId, String instructorUsername) {
		String error = null;
		
		Experiment experiment = experimentRepository.findById(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		
		Optional<Instructor> instructor = instructorRepository.findByUsername(instructorUsername);
		
		if (!instructor.isPresent()) {
			error = "noinstructorfound";
		} else if (instructorRepository.existsByUsernameAndExperimentsId(instructorUsername, experimentId)) {
			error = "instructoralreadyassigned";
		} else {
			experiment.addInstructor(instructor.get());
			experiment = experimentRepository.save(experiment);
			instructorRepository.save(instructor.get());
		}
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "instructorsForExperiment", experiment.getId(), error, null, null)
				.build()
				.toUriString();
	}
	
	@GetMapping("/experiment/{experimentId}/unassign/{instructorId}")
	public String unassignInstructorFromExperiment(@PathVariable Integer experimentId, @PathVariable Integer instructorId, Model model) {
		ExperimentForAdminDetail experiment = experimentRepository.findForAdminById(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		model.addAttribute("experiment", experiment);
		
		LabVisionUserInfo instructor = instructorRepository.findInfoById(instructorId)
				.orElseThrow(() -> new ResourceNotFoundException(Instructor.class, instructorId));
		model.addAttribute("instructor", instructor);
		
		return "admin/unassigninstructorfromexperiment";
	}
	
	@PostMapping("/experiment/{experimentId}/unassign/{instructorId}")
	public String confirmUnassignInstructorFromExperiment(@PathVariable Integer experimentId, @PathVariable Integer instructorId) {
		Experiment experiment = experimentRepository.findById(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		
		Instructor instructor = instructorRepository.findById(instructorId)
				.orElseThrow(() -> new ResourceNotFoundException(Instructor.class, instructorId));
		
		experiment.removeInstructor(instructor);
		
		experiment = experimentRepository.save(experiment);
		instructorRepository.save(instructor);
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "instructorsForExperiment", experiment.getId(), null, null, null)
				.build()
				.toUriString();
	}
	
	@GetMapping("/experiment/{experimentId}/activestudents")
	public String activeStudentsForExperiment(@PathVariable Integer experimentId, String error, Model model, Pageable pageable) {
		CourseInfo course = courseRepository.findCourseInfoByExperimentId(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		model.addAttribute("course", course);
		
		ExperimentForAdminDetail experiment = experimentRepository.findForAdminById(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		model.addAttribute("experiment", experiment);
		
		Page<LabVisionUserInfo> students = studentRepository.findForAdminByActiveExperimentId(experimentId, pageable);
		model.addAttribute("students", students);
		
		PaginationUtils.addPageModelAttributes(model, students, null, AdminController.class, "activeStudentsForExperiment", experimentId, error, null, null);
		
		model.addAttribute("activateStudentActionUrl", MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "activateStudent", experimentId, null)
				.replaceQuery(null)
				.build()
				.toUriString()
				);
		
		model.addAttribute("error", error);
		
		return "admin/activestudentsforexperiment";
	}
	
	@PostMapping("/experiment/{experimentId}/activate")
	public String activateStudent(@PathVariable Integer experimentId, String studentUsername) {
		String error = null;
		
		Experiment experiment = experimentRepository.findById(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		
		Optional<Student> student = studentRepository.findByUsername(studentUsername);
		
		if (!student.isPresent()) {
			error = "nostudentfound";
		} else if (studentRepository.existsByUsernameAndActiveExperimentsId(studentUsername, experimentId)) {
			error = "studentalreadyactive";
		} else {
		    student.get().addActiveExperiment(experiment);
		    experiment = experimentRepository.save(experiment);
		    studentRepository.save(student.get());
		}
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "activeStudentsForExperiment", experiment.getId(), error, null, null)
				.build()
				.toUriString();
	}
	
	@GetMapping("/experiment/{experimentId}/deactivate/{studentId}")
	public String deactivateStudent(@PathVariable Integer experimentId, @PathVariable Integer studentId, Model model) {
		ExperimentForAdminDetail experiment = experimentRepository.findForAdminById(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		model.addAttribute("experiment", experiment);
		
		LabVisionUserInfo student = studentRepository.findInfoById(studentId)
				.orElseThrow(() -> new ResourceNotFoundException(Student.class, studentId));
		model.addAttribute("student", student);
		
		return "admin/deactivatestudent";
	}
	
	@PostMapping("/experiment/{experimentId}/deactivate/{studentId}")
	public String confirmDeactivateStudent(@PathVariable Integer experimentId, @PathVariable Integer studentId) {
		Experiment experiment = experimentRepository.findById(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		
		Student student = studentRepository.findById(studentId)
				.orElseThrow(() -> new ResourceNotFoundException(Student.class, studentId));
		
		student.removeActiveExperiment(experiment);
		
		experiment = experimentRepository.save(experiment);
		studentRepository.save(student);
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "activeStudentsForExperiment", experimentId, null, null, null)
				.build()
				.toUriString();
	}
	
	@GetMapping("/experiment/{experimentId}/reports")
	public String reportedResultsForExperiment(@PathVariable Integer experimentId, Model model, Pageable pageable) {
		ExperimentInfo experiment = experimentRepository.findExperimentInfo(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		model.addAttribute("experiment", experiment);
		
		CourseInfo course = courseRepository.findCourseInfoByExperimentId(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		model.addAttribute("course", course);
		
		Page<ReportedResultForAdminTable> reports = reportedResultRepository.findForAdminByExperimentId(experimentId, pageable);
		model.addAttribute("reports", reports);
		
		PaginationUtils.addPageModelAttributes(model, reports, null, AdminController.class, "reportedResultsForExperiment", experimentId, null, null);
		
		return "admin/reportedresultsforexperiment";
	}
	
	@GetMapping("/report/{id}")
	public String getReportedResult(@PathVariable Integer id, Model model) throws MalformedURLException, UnsupportedEncodingException {
		ReportedResult report = reportedResultRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(ReportedResult.class, id));
		model.addAttribute("report", report);
		
		model.addAttribute("reportDocumentUrl", reportDocumentService.buildReportDocumentUrl(id));
		
		return "admin/report";
	}
	
	@GetMapping("/report/new/for/experiment/{experimentId}")
	public String newReportedResult(@PathVariable Integer experimentId, @RequestParam Boolean uploadfile, 
			@RequestParam(required=false) String[] errors, Model model) {
		ExperimentInfo experiment = experimentRepository.findInfoById(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		model.addAttribute("experiment", experiment);
		
		model.addAttribute("admin", true);
		
		List<ResultInfo> acceptedResults = experimentRepository.getAcceptedResultInfoFor(experimentId);
		model.addAttribute("acceptedResults", acceptedResults);
		
		String actionUrl = MvcUriComponentsBuilder.fromMethodName(
				AdminController.class,
				"createReport",
				experimentId, null, null, null, null, null
				).replaceQuery(null)
				.build()
				.toUriString();
		model.addAttribute("actionUrl", actionUrl);
		
		model.addAttribute("uploadfile", uploadfile);
		
		model.addAttribute("errors", errors);
		
		return "editors/editreport";
	}
	
	@PostMapping("/report/new/for/experiment/{experimentId}")
	public String createReport(@PathVariable Integer experimentId,
			String reportName, String studentUsername,
			@RequestParam(name="documentType", required=false) ReportDocumentType documentType,
			@RequestParam(name="externalDocumentURL", required=false) URL externalDocumentURL,
			@RequestParam(name="filesystemDocumentFile", required=false) MultipartFile filesystemDocumentFile) throws IOException {
		ArrayList<String> errors = new ArrayList<>();
		
		Experiment experiment = experimentRepository.findById(experimentId)
				.orElseThrow(() -> new ResourceNotFoundException(Experiment.class, experimentId));
		
		Optional<Student> student = studentRepository.findByUsername(studentUsername);
		
		ReportedResult reportedResult = null;
		
		if (!student.isPresent()) {
			errors.add("$nostudentfound");
		} else {
			try {
				reportedResult = reportedResultService.createReportedResult(
							experimentId, reportName, documentType, externalDocumentURL, filesystemDocumentFile, student.get(), experiment);
			} catch (TransactionSystemException e) {
				if (e.contains(ConstraintViolationException.class)) {
					throw (ConstraintViolationException) e.getRootCause();
				} else {
					throw e;
				}
			} catch (ConstraintViolationException e) {
				errors.addAll(e.getConstraintViolations().stream()
						.map(ConstraintViolation::getMessage)
						.collect(Collectors.toList())
						);
			}
		}
		
		if (errors.size() > 0) {
			return "redirect:" + MvcUriComponentsBuilder
					.fromMethodName(
							AdminController.class,
							"newReportedResult",
							experiment.getId(),
							false,
							errors.toArray(new String[errors.size()]),
							null
							).build()
							.toUriString();
		}
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "getReportedResult", reportedResult.getId(), null)
				.build()
				.toUriString();
	}
	
	@GetMapping("/report/edit/{id}")
	public String editReportedResult(@PathVariable Integer id, Boolean uploadfile, 
			@RequestParam(required=false) String[] errors, Model model) throws MalformedURLException, UnsupportedEncodingException {
		ReportForAdminReportView report = reportedResultRepository.findForAdminById(id)
				.orElseThrow(() -> new ResourceNotFoundException(ReportedResult.class, id));
		model.addAttribute("report", report);
		
		String reportDocumentUrl = reportDocumentService.buildReportDocumentUrl(id);
		model.addAttribute("reportDocumentUrl", reportDocumentUrl);
		
		ExperimentInfo experiment = experimentRepository.findExperimentInfo(report.getExperimentId()).get();
		model.addAttribute("experiment", experiment);
		
		List<ResultInfo> acceptedResults = experimentRepository.getAcceptedResultInfoFor(report.getExperimentId());
		model.addAttribute("acceptedResults", acceptedResults);
		
		String actionUrl = MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "updateReportedResult", id,
						null, null, null, null)
				.replaceQuery(null)
				.build()
				.toUriString();
		model.addAttribute("actionUrl", actionUrl);
		
		model.addAttribute("uploadfile", uploadfile);
		
		String changeReportDocumentUrl = MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "editReportedResult", id, true, null, null)
				.build()
				.toUriString();
		model.addAttribute("changeReportDocumentUrl", changeReportDocumentUrl);
		
		return "editors/editreport";
	}
	
	@PostMapping("/report/edit/{id}")
	public String updateReportedResult(@PathVariable Integer id,
			String reportName, @RequestParam(name="documentType", required=false) ReportDocumentType documentType,
			@RequestParam(name="externalDocumentURL", required=false) URL externalDocumentURL,
			@RequestParam(name="filesystemDocumentFile", required=false) MultipartFile filesystemDocumentFile) throws IOException {
		ReportedResult reportedResult = reportedResultRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(ReportedResult.class, id));
		
		Student student = reportedResult.getStudent();
		
		reportedResult = reportedResultService.updateReportedResult(
				reportName, documentType, externalDocumentURL, filesystemDocumentFile, reportedResult, student);
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "getReportedResult", reportedResult.getId(), null)
				.build()
				.toUriString();
	}
	
	@GetMapping("/report/delete/{id}")
	public String deleteReportedResult(@PathVariable Integer id, Model model) {
		ReportForReportView report = reportedResultRepository.findForReportView(id)
				.orElseThrow(() -> new ResourceNotFoundException(ReportedResult.class, id));
		model.addAttribute("report", report);
		
		ExperimentInfo experiment = experimentRepository.findInfoById(report.getExperimentId()).get();
		model.addAttribute("experiment", experiment);
		
		return "admin/deletereport";
	}
	
	@PostMapping("/report/delete/{id}")
	public String confirmDeleteReportedResult(@PathVariable Integer id) {
		ReportForReportView report = reportedResultRepository.findForReportView(id)
				.orElseThrow(() -> new ResourceNotFoundException(ReportedResult.class, id));
		
		reportedResultRepository.deleteById(id);
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(
						AdminController.class, 
						"reportedResultsForExperiment", 
						report.getExperimentId(), null, null
						)
				.build()
				.toUriString();
	}
	
	@GetMapping("/users")
	public String users(Model model, Pageable pageable) {
		Page<LabVisionUserDetails> usersPage = userDetailsManager.findAllUsers(pageable);
		
		Page<LabVisionUserForAdminTable> users = new PageImpl<LabVisionUserForAdminTable>(
				usersPage.stream()
					.map(userDetails -> new LabVisionUserForAdminTable(
							userDetails.getLabVisionUser().getId(),
							userDetails.getUsername(),
							userDetails.getLabVisionUser().getDisplayName(),
							userDetails.getLabVisionUser().getRole(),
							userDetailsManager.isAdmin(userDetails)
							))
					.collect(Collectors.toList()),
				pageable,
				userDetailsManager.countUsers()
				);
		model.addAttribute("users", users.getContent());
		
		PaginationUtils.addPageModelAttributes(model, users, null, AdminController.class, "users", null, null);
		
		return "admin/users";
	}
	
	@GetMapping("/user/{id}")
	public String getUser(@PathVariable Integer id, Model model) {
		LabVisionUserDetails userDetails = userDetailsManager.loadUserById(id);
		
		model.addAttribute("user", userDetails.getLabVisionUser());
		
		model.addAttribute("admin", userDetailsManager.isAdmin(userDetails)
		);
		
		return "admin/user";
	}
	
	@GetMapping("/user/new")
	public String newUser(Model model) {
		model.addAttribute("actionUrl", MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "createUser",
						null, null, null, null, null, null, null, null, null, null)
				.replaceQuery(null)
				.build()
				.toUriString()
				);
		
		return "editors/edituser";
	}
	
	@PostMapping("/user/new")
	public String createUser(
			String username, String role, Boolean admin,
			String firstName, String lastName, String facultyOrStudentName, String facultyEmail,
			String adminEmail, String adminPhone, Model model) {
		
		UserBuilder userBuilder = User.withUsername(username)
				.passwordEncoder(rawPassword -> passwordEncoder.encode(rawPassword))
				.password("temp_" + username);
		
		UserRole userRole = UserRole.valueOf(role);
		
		String[] roles = (admin != null && admin) ? new String[] { userRole.toString(), UserRole.ADMIN.toString() } 
			: new String[] { userRole.toString() };
		
		userBuilder = userBuilder
				.roles(roles);
		
		UserDetails userDetails = userBuilder.build();
		
		try {
			switch (userRole) {
			case STUDENT:
				userDetailsManager.createStudent(
						userDetails,
						StringUtils.hasLength(facultyOrStudentName)
							? facultyOrStudentName
							: buildName(firstName, lastName)
						);
				break;
			case FACULTY:
				userDetailsManager.createInstructor(
						userDetails, 
						StringUtils.hasLength(facultyOrStudentName)
							? facultyOrStudentName
							: buildName(firstName, lastName)
						);
				break;
			case ADMIN:
				userDetailsManager.createAdminOnlyUser(userDetails, firstName, lastName, adminEmail, adminPhone);
			}
		} catch (ConstraintViolationException e) {
			return "redirect:" + MvcUriComponentsBuilder.fromMethodName(
					AdminController.class, 
					"newUser",
					e.getConstraintViolations().stream()
						.map(ConstraintViolation::getMessage)
						.toArray(String[]::new),
					null
					).build()
					.toUriString();
		}
		
		LabVisionUserDetails labVisionUserDetails = (LabVisionUserDetails) userDetailsManager.loadUserByUsername(username);
		String passwordResetToken = userDetailsManager.makePasswordResetToken(labVisionUserDetails.getLabVisionUser().getId());
		
		// change user's password to token
		UserDetails newUserDetails = User.withUserDetails(labVisionUserDetails)
				.passwordEncoder(pw -> passwordEncoder.encode(pw))
				.password(passwordResetToken)
				.build();
		userDetailsManager.updateUser(newUserDetails);
		
		model.addAttribute("newPasswordUrl", MvcUriComponentsBuilder
				.fromMethodName(ResetPasswordController.class, "beginPasswordResetWithToken", passwordResetToken, null)
				.toUriString()
				);
		
		if (admin != null && admin) {
			userDetailsManager.updateAdminInfo(
					labVisionUserDetails.getLabVisionUser().getId(),
					firstName, lastName,
					adminEmail,
					adminPhone, false
					);
		}
		
		return "admin/newpasswordlink";
	}

	// Helper for the above to fill in single "name" properties
	private String buildName(String firstName, String lastName) {
		return firstName 
			+ ((StringUtils.hasLength(firstName) && StringUtils.hasLength(lastName)) ? " " : "") 
			+ lastName;
	}
	
	@GetMapping("/user/edit/{id}")
	public String editUser(@PathVariable Integer id, 
			@RequestParam(required=false) String[] errors, Model model) {
		final LabVisionUserDetails userDetails = userDetailsManager.loadUserById(id);
		
		model.addAttribute("user", userDetails.getLabVisionUser());
		
		model.addAttribute("errors", errors);
		
		final boolean admin = userDetailsManager.isAdmin(userDetails);
		model.addAttribute("admin", admin);
		
		if (admin) {
			final AdminInfo adminInfo = userDetails.getLabVisionUser().getAdminInfo();
			if (Objects.nonNull(adminInfo)) {
				model.addAttribute("firstName", adminInfo.getFirstName());
				model.addAttribute("lastName", adminInfo.getLastName());
			}
		} else {
			switch (userDetails.getLabVisionUser().getRole()) {
			case FACULTY:
				final Instructor instructor = (Instructor) userDetails.getLabVisionUser();
				model.addAttribute("facultyEmail", instructor.getEmail());
				model.addAttribute("facultyOrStudentName", instructor.getName());
				break;
			case STUDENT:
				final Student student = (Student) userDetails.getLabVisionUser();
				model.addAttribute("facultyOrStudentName", student.getName());
				break;
			default:
				throw new IllegalStateException("Admin-only user does not have admin role granted");	
			}
		}
		
		model.addAttribute("actionUrl", MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "updateUser", 
						id, null, null, null, null, null, null, null)
				.replaceQuery(null)
				.build()
				.toUriString()
				);
		
		return "editors/edituser";
	}
	
	@PostMapping("/user/edit/{id}")
	public String updateUser(@PathVariable Integer id, Boolean admin,
			String firstName, String lastName, String facultyOrStudentName, String facultyEmail,
			String adminEmail, String adminPhone) {
		LabVisionUserDetails userDetails = userDetailsManager.loadUserById(id);
		
		LabVisionUser user = userDetails.getLabVisionUser();
		
		if (adminEmail != null && adminEmail.length() == 0) {
			adminEmail = null;
		}
		
		if (adminPhone != null && adminPhone.length() == 0) {
			adminPhone = null;
		}
		
		try {
			// ensure admin-only user info is updated even if admin parameter isn't sent
			if ((admin != null && admin) 
					|| userDetails.getLabVisionUser().getRole().equals(UserRole.ADMIN)) {
				
				if (!userDetailsManager.isAdmin(userDetails)) {
					userDetailsManager.grantAdminAuthority(userDetails.getUsername());
				}
				
				userDetailsManager.updateAdminInfo(id, firstName, lastName, adminEmail, adminPhone, true);
			} else {
				if (userDetailsManager.isAdmin(userDetails) 
						&& userDetails.getAuthorities().size() > 1) {
					
					userDetailsManager.revokeAdminAuthority(userDetails.getUsername());
				}
			}
		
			switch (user.getRole()) {
			case STUDENT:
				userDetailsManager.updateStudent(id, facultyOrStudentName, true);
				break;
			case FACULTY:
				userDetailsManager.updateInstructor(id, facultyOrStudentName, facultyEmail, true);
				break;
			case ADMIN:
				break;
			}
		} catch (ConstraintViolationException e) {
			return "redirect:" + MvcUriComponentsBuilder.fromMethodName(
					AdminController.class, 
					"editUser", 
					id, 
					e.getConstraintViolations().stream()
						.map(ConstraintViolation::getMessage)
						.toArray(String[]::new),
					null
					).encode()
					.build()
					.toUriString();
		}
		
		return "redirect:" + MvcUriComponentsBuilder.fromMethodName(
				AdminController.class,
				"getUser",
				id,
				null
				).build()
				.toUriString();
	}
	
	@GetMapping("/user/delete/{id}")
	public String deleteUser(@PathVariable Integer id, Model model) {
		LabVisionUser user = userDetailsManager.loadUserById(id).getLabVisionUser();
		model.addAttribute("user", user);
		
		return "admin/deleteuser";
	}
	
	@PostMapping("/user/delete/{id}")
	public String confirmDeleteUser(@PathVariable Integer id) {
		userDetailsManager.deleteUserById(id);
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "users", null, null)
				.build()
				.toUriString();
	}
	
	@GetMapping("/user/passwordresetlink/{id}")
	public String getPasswordResetLink(@PathVariable Integer id, Model model) {
		LabVisionUser user = userDetailsManager.loadUserById(id).getLabVisionUser();
		model.addAttribute("user", user);
		
		String token = userDetailsManager.makePasswordResetToken(id);
		model.addAttribute("resetPasswordUrl", MvcUriComponentsBuilder
				.fromMethodName(ResetPasswordController.class, "beginPasswordResetWithToken", token, null)
				.build()
				.toUriString()
				);
		
		return "admin/passwordresetlink";
	}
	
	@GetMapping("/user/forcepasswordresest/{id}")
	public String forcePasswordReset(@PathVariable Integer id, Model model) {
		LabVisionUser user = userDetailsManager.loadUserById(id).getLabVisionUser();
		model.addAttribute("user", user);
		
		return "admin/forcepasswordreset";
	}
	
	@PostMapping("/user/forcepasswordreset/{id}")
	public String confirmForcePasswordReset(@PathVariable Integer id, Model model) {
		final LabVisionUserDetails userDetails = userDetailsManager.loadUserById(id);
		model.addAttribute("user", userDetails.getLabVisionUser());
		
		userDetailsManager.forcePasswordReset(userDetails);
		String token = userDetailsManager.makePasswordResetToken(id);
		model.addAttribute("resetPasswordUrl", MvcUriComponentsBuilder
				.fromMethodName(ResetPasswordController.class, "beginPasswordResetWithToken", token, null)
				.build().toUriString()
				);
		
		return "admin/passwordresetlink";
	}
	
	@GetMapping("/profile")
	public String profile(@AuthenticationPrincipal LabVisionUserDetails userDetails, Model model) {
		model.addAttribute("user", userDetails.getLabVisionUser());
		
		return "admin/profile";
	}
	
	@GetMapping("/profile/edit")
	public String editProfile(@RequestParam(required=false) String[] errors, 
			@AuthenticationPrincipal LabVisionUserDetails userDetails, Model model) {
		model.addAttribute("user", userDetails.getLabVisionUser());
		
		model.addAttribute("errors", errors);
		
		model.addAttribute("actionUrl", MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "updateProfile", null, null, null, null, null)
				.replaceQuery(null)
				.build()
				);
		
		return "admin/editprofile";
	}
	
	@PostMapping("/profile/edit")
	public String updateProfile(
			String firstName, String lastName, String email, String phone,
			@AuthenticationPrincipal LabVisionUserDetails userDetails) {
		
		Integer id = userDetails.getLabVisionUser().getId();
		
		email = StringUtils.hasLength(email) ? email : null;
		phone = StringUtils.hasLength(phone) ? phone : null;
		
		try {
			userDetailsManager.updateAdminInfo(id, firstName, lastName, email, phone, false);
		} catch (ConstraintViolationException e) {
			return "redirect:" + MvcUriComponentsBuilder
					.fromMethodName(
							AdminController.class,
							"editProfile",
							e.getConstraintViolations().stream()
								.map(ConstraintViolation::getMessage)
								.toArray(String[]::new),
							null,
							null)
					.toUriString();
		}
		
		return "redirect:" + MvcUriComponentsBuilder
				.fromMethodName(AdminController.class, "profile", null, null)
				.build()
				.toUriString();
	}
	
	private NavbarModel buildAdminNavbar(LabVisionUserDetails userDetails) {
		NavbarModel navbarModel = new NavbarModel();
		
		navbarModel.addNavLink("Dashboard", AdminController.class, "dashboard", new Object(), new Object());
		navbarModel.addNavLink("Courses", AdminController.class, "courses", new Object(), new Object());
		navbarModel.addNavLink("Users", AdminController.class, "users", new Object(), new Object());
		
		ArrayList<NavbarModel.NavLink> accountLinks = new ArrayList<>(
				Arrays.asList(new NavbarModel.NavLink[] {
						navbarModel.createNavLink("Profile", AdminController.class, "profile", new Object(), new Object())
				})
		);
		
		// non-admin user roles, if any
		List<GrantedAuthority> nonAdminRoles = userDetails.getAuthorities().stream()
				.filter(auth -> !auth.getAuthority().equals("ROLE_ADMIN"))
				.collect(Collectors.toList());
		
		// add admin exit link
		if (!nonAdminRoles.isEmpty()) {
			accountLinks.add(navbarModel.createNavLink(
					"Exit Admin", dashboardUrlService.getDashboardUrl(nonAdminRoles)));
		}
		
		navbarModel.addNavLink(navbarModel.new NavLink(
				"Account",
				"#",
				accountLinks.toArray(new NavbarModel.NavLink[accountLinks.size()])
		));
		
		navbarModel.setLogoutLink("/logout");
		
		return navbarModel;
	}
	
}
