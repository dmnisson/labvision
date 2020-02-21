<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<t:userpage title="${fn:escapeXml(courseClass.name)} – ${fn:escapeXml(course.name)}">

<div class="container-fluid p-lg-5 userpage-container">
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb">
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#courses').build()}">Courses</a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#getCourse').arg(0, course.id).build()}"><c:out value="${course.name}" /></a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#classes').arg(0, course.id).build()}"><c:out value="${course.name}" /> Classes</a></li>
      <li class="breadcrumb-item active" aria-current="page"><c:out value="${courseClass.name}" /></li>
    </ol>
  </nav>
  <div class="row">
    <div class="col">
      <h1>
        ${fn:escapeXml(courseClass.name)} — ${fn:escapeXml(course.name)}&nbsp;<a
          class="btn btn-primary" href="${s:mvcUrl('AC#editCourseClass').arg(0, courseClass.id).build()}">
          <i class="far fa-edit"></i> Edit
        </a>&nbsp;<a
          class="btn btn-danger" href="${s:mvcUrl('AC#deleteCourseClass').arg(0, courseClass.id).build()}">
          <i class="fas fa-minus"></i> Delete
        </a>
      </h1>
    </div>
  </div>
  <div class="row">
    <div class="col-md-6">
      <div class="card">
        <div class="card-body">
          <h2 class="card-title">${courseClass.numOfStudents} student${courseClass.numOfStudents eq 1 ? '' : 's'} enrolled</h2>
          <a class="btn btn-primary" href="${s:mvcUrl('AC#studentsForCourseClass').arg(0, courseClass.id).build()}">View and Manage Students</a>
        </div>
      </div>
    </div>
    <div class="col-md-6">
      <div class="card">
        <div class="card-body">
          <h2 class="card-title">${courseClass.numOfInstructors} instructor${courseClass.numOfInstructors eq 1 ? '' : 's'} assigned</h2>
          <a class="btn btn-primary" href="${s:mvcUrl('AC#instructorsForCourseClass').arg(0, courseClass.id).build()}">View and Manage Instructors</a>
        </div>
      </div>
    </div>
  </div>
</div>

</t:userpage>