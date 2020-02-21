<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<t:userpage title="${fn:escapeXml(course.name)}">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col">
      <h1>
        ${fn:escapeXml(course.name)}&nbsp;<a
          class="btn btn-primary" href="${s:mvcUrl('AC#editCourse').arg(0, course.id).build()}">
          <i class="far fa-edit"></i> Edit
        </a>&nbsp;<a
          class="btn btn-danger" href="${s:mvcUrl('AC#deleteCourse').arg(0, course.id).build()}">
          <i class="fas fa-minus"></i> Delete
        </a>
      </h1>
    </div>
  </div>
  <div class="row">
    <div class="col-md-6">
      <div class="card">
        <div class="card-body">
          <h2 class="card-title">${course.numOfCourseClasses} class${course.numOfCourseClasses eq 1 ? '' : 'es'}</h2>
          <a class="btn btn-primary" href="${s:mvcUrl('AC#classes').arg(0, course.id).build()}">View and Manage Classes</a>
        </div>
      </div>
    </div>
    <div class="col-md-6">
      <div class="card">
        <div class="card-body">
          <h2 class="card-title">${course.numOfExperiments} experiment${course.numOfExperiments eq 1 ? '' : 's'}</h2>
          <a class="btn btn-primary" href="${s:mvcUrl('AC#experiments').arg(0, course.id).build()}">View and Manage Experiments</a>
        </div>
      </div>
    </div>
  </div>
</div>

</t:userpage>