package labvision.viewmodels;

import labvision.entities.Experiment;
import labvision.entities.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import labvision.entities.Course;

/**
 * Represents a set of objects to be displayed in the student dashboard
 * @author davidnisson
 *
 */
public class Dashboard {
	// currently logged in student
	private Student student;
	
	// experiments which are currently available for the student to enter values into
	private final HashMap<Course, Experiment> currentExperiments = new HashMap<>();
	
	// most recent experiments which the student has completed
	private final ArrayList<Experiment> recentExperiments = new ArrayList<>();
	
	// current and most recent courses
	private final ArrayList<Course> recentCourses = new ArrayList<>();
	
	// maximum number of recent experiments to list
	private int maxRecentExperiments = 5;
	
	// maximum number of recent courses to list
	private int maxRecentCourses = 5;
	
	/**
	 * Get a map view of courses to current experiments
	 * @return the map of courses to current experiments
	 */
	public Map<Course, Experiment> getCurrentExperiments() {
		return currentExperiments;
	}

	/**
	 * Set the map of courses to current experiments from a given map
	 * @param currentExperiments the map of courses to current experiments
	 */
	public void setCurrentExperiments(Map<? extends Course, ? extends Experiment> currentExperiments) {
		this.currentExperiments.putAll(currentExperiments);
	}
	
	/**
	 * Get the current experiment for a course
	 * @param course the course
	 * @return the current experiment
	 */
	public Experiment getCurrentExperiment(Course course) {
		return currentExperiments.get(course);
	}
	
	/**
	 * Set the current experiment for a course
	 * @param course the course
	 * @param experiment the experiment
	 */
	public void setCurrentExperiment(Course course, Experiment experiment) {
		this.currentExperiments.put(course, experiment);
	}

	/**
	 * Get a list of the most recent experiments
	 * @return the list of most recent experiments, with the set maximum size
	 */
	public List<Experiment> getRecentExperiments() {
		return recentExperiments.subList(0, 
				Math.min(getMaxRecentExperiments(), recentExperiments.size()));
	}

	/**
	 * Set the list of most recent experiments from the given list
	 * @param recentExperiments the most recent experiments
	 */
	public void setRecentExperiments(List<? extends Experiment> recentExperiments) {
		this.recentExperiments.clear();
		this.recentExperiments.addAll(recentExperiments);
	}

	/**
	 * Add a new experiment to the list of most recent ones
	 * @param experiment the experiment to add
	 */
	public void addRecentExperiment(Experiment experiment) {
		this.recentExperiments.add(0, experiment);
	}
	
	/**
	 * Remove old experiments that are stored in the list but are not displayed
	 * because of the maximum size.
	 */
	public void pruneOldExperiments() {
		this.recentExperiments.retainAll(this.getRecentExperiments());
	}
	
	/**
	 * Get a list of the most recent courses
	 * @return the list of most recent courses
	 */
	public List<Course> getRecentCourses() {
		return recentCourses.subList(0, 
				Math.min(getMaxRecentCourses(), recentCourses.size()));
	}
	
	/**
	 * Set the list of most recent courses from the given list
	 * @param recentCourses the most recent courses
	 */
	public void setRecentCourses(List<? extends Course> recentCourses) {
		this.recentCourses.clear();
		this.recentCourses.addAll(recentCourses);
	}
	
	/**
	 * Add a new course to the list of most recent ones
	 * @param course the course to add
	 */
	public void addRecentCourse(Course course) {
		this.recentCourses.add(0, course);
	}
	
	/**
	 * Remove old courses that are stored in the list but are not displayed
	 * because of the maximum size.
	 */
	void pruneOldCourses() {
		this.recentCourses.retainAll(this.getRecentCourses());
	}

	/**
	 * Get the logged-in student
	 * @return student
	 */
	public Student getStudent() {
		return student;
	}

	/**
	 * Set the logged-in student
	 * @param studentId new student
	 */
	public void setStudent(Student student) {
		this.student = student;
	}

	public int getMaxRecentExperiments() {
		return maxRecentExperiments;
	}

	public void setMaxRecentExperiments(int maxRecentExperiments) {
		this.maxRecentExperiments = maxRecentExperiments;
	}

	public int getMaxRecentCourses() {
		return maxRecentCourses;
	}

	public void setMaxRecentCourses(int maxRecentCourses) {
		this.maxRecentCourses = maxRecentCourses;
	}
}
