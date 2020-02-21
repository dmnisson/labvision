<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:userpage title="${fn:escapeXml(course.name)} Classes">

<div class="container-fluid p-lg-5 userpage-container">
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb">
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#courses').build()}">Courses</a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#getCourse').arg(0, course.id).build()}"><c:out value="${course.name}" /></a></li>
      <li class="breadcrumb-item active" aria-current="page"><c:out value="${course.name}" /> Classes</li>
    </ol>
  </nav>
  <div class="row">
    <div class="col">
      <h1><c:out value="${course.name}" /> Classes</h1>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <c:if test="${empty classes}">
        No classes added yet.
      </c:if>
      <c:if test="${not empty classes}">
      <div class="table-responsive">
	      <table class="table">
	        <thead>
	          <tr>
	            <th scope="col">Class</th>
	            <th scope="col">Number of Students Enrolled</th>
	            <th scope="col">Number of Instructors Assigned</th>
	            <th scope="col"></th>
	          </tr>
	        </thead>
	        <tbody>
	          <c:forEach var="courseClass" items="${classes.content}">
	          <tr>
	            <td><a href="${s:mvcUrl('AC#getCourseClass').arg(0, courseClass.id).build()}"><c:out value="${courseClass.name}" /></a></td>
	            <td>${courseClass.numOfStudents}</td>
	            <td>${courseClass.numOfInstructors}</td>
	            <td>
	              <div class="btn-group" role="group" aria-label="Actions for ${fn:escapeXml(courseClass.name)}">
	                <a class="btn btn-primary" href="${s:mvcUrl('AC#editCourseClass').arg(0, courseClass.id).build()}">
	                  <i class="far fa-edit"></i> Edit
	                </a>
	                <a class="btn btn-primary" href="${s:mvcUrl('AC#deleteCourseClass').arg(0, courseClass.id).build()}">
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
      <a class="btn btn-primary" href="${s:mvcUrl('AC#newCourseClass').arg(0, course.id).build()}"><i class="fas fa-plus"></i> New Class</a>
    </div>
  </div>
</div>

</t:userpage>