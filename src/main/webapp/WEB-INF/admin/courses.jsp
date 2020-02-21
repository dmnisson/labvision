<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:userpage title="Courses">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col">
      <h1>Courses</h1>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <c:if test="${empty courses}">
        No courses added yet.
      </c:if>
      <c:if test="${not empty courses}">
      <div class="table-responsive">
	      <table class="table">
	        <thead>
	          <tr>
	            <th scope="col">Course</th>
	            <th scope="col">Number of Classes</th>
	            <th scope="col">Number of Experiments</th>
	            <th scope="col"></th>
	          </tr>
	        </thead>
	        <tbody>
	          <c:forEach var="course" items="${courses.content}">
	          <tr>
	            <td><a href="${s:mvcUrl('AC#getCourse').arg(0, course.id).build()}"><c:out value="${course.name}" /></a></td>
	            <td>${course.numOfCourseClasses}</td>
	            <td>${course.numOfExperiments}</td>
	            <td>
	              <div class="btn-group" role="group" aria-label="Actions for ${fn:escapeXml(course.name)}">
	                <a class="btn btn-primary" href="${s:mvcUrl('AC#editCourse').arg(0, course.id).build()}">
	                  <i class="far fa-edit"></i> Edit
	                </a>
	                <a class="btn btn-primary" href="${s:mvcUrl('AC#deleteCourse').arg(0, course.id).build()}">
	                  <i class="fas fa-minus"></i> Delete
	                </a>
	              </div>
	            </td>
	          </tr>
	          </c:forEach>
	        </tbody>
	      </table>
      </div>
      <t:pagenav 
        pages="${pages}"
        currentpage="${currentPage}"
        prevpageurl="${prevPageUrl}"
        nextpageurl="${nextPageUrl}"
        pageurls="${pageUrls}"
      />
      </c:if>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <a class="btn btn-primary" href="${s:mvcUrl('AC#newCourse').build()}"><i class="fas fa-plus"></i> New Course</a>
    </div>
  </div>
</div>

</t:userpage>