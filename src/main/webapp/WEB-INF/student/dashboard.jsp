<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:userpage title="Dashboard">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col">
      <h1>Welcome, ${student.displayName}!</h1>
    </div>
  </div>
  <div class="row py-2">
    <div class="col">
      <div class="card">
        <div class="card-body">
          <h2 class="card-title">Current Experiments</h2>
          <c:if test="${empty currentExperiments}">
            <p class="card-text text-center">No current experiments.</p>
          </c:if>
          <c:if test="${not empty currentExperiments}">
          <table class="table">
            <thead>
              <tr>
                <th scope="col">Experiment</th>
                <th scope="col">Course</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="experiment" items="${currentExperiments}">
              <tr>
                <td>
                  <a href="/student/experiment/${experiment.id}">${experiment.name}</a>
                </td>
                <td>
                  <a href="/student/course/${experiment.courseId}">${experiment.courseName}</a>
                </td>
              </tr>
              </c:forEach>
            </tbody>
          </table>
          </c:if>
        </div>
      </div>
    </div>
  </div>
  
  <div class="row py-4">
    <div class="col-md-6 py-2 py-md-0">
      <div class="card">
	      <div class="card-body">
	        <div class="card-title">
	          <h2>Recent Experiments</h2>
	        </div>
	        <c:if test="${empty recentExperiments}">
	        <p class="card-text text-center">No recent experiments.</p>
	        </c:if>
        </div>
        <c:if test="${not empty recentExperiments}">
        <ul class="list-group list-group-flush">
          <c:forEach var="experiment" items="${recentExperiments}">
          <li class="list-group-item">
            <a href="/student/experiment/${experiment.id}">${experiment.name}</a>
          </li>
          </c:forEach>
        </ul>
        </c:if>
      </div>
    </div>
    <div class="col-md-6 py-2 py-md-0">
      <div class="card">
        <div class="card-body">
	        <div class="card-title">
	          <h2>Recent Courses</h2>
	        </div>
	        <c:if test="${empty recentCourses}">
          <p class="card-text text-center">No recent courses.</p>
          </c:if>
        </div>
        <c:if test="${not empty recentCourses}">
        <ul class="list-group list-group-flush">
          <c:forEach var="course" items="${recentCourses}">
            <li class="list-group-item">
              <a href="/student/course/${course.id}">${course.name}</a>
            </li>
          </c:forEach>
        </ul>
        </c:if>
      </div>
    </div>
  </div>
</div>

</t:userpage>