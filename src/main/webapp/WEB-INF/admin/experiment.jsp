<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib uri = "http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<t:userpage title="${fn:escapeXml(experiment.name)} – ${fn:escapeXml(course.name)}">

<div class="container-fluid p-lg-5 userpage-container">
  <nav aria-label="breadcrumbs">
    <ol class="breadcrumb">
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#courses').build()}">Courses</a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#getCourse').arg(0, course.id).build()}"><c:out value="${course.name}" /></a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#experiments').arg(0, course.id).build()}"><c:out value="${course.name}" /> Experiments</a></li>
      <li class="breadcrumb-item active" aria-current="page"><c:out value="${experiment.name}" /></li>
    </ol>
  </nav>
  <div class="row">
    <div class="col">
      <h1><c:out value="${experiment.name}" /> – <c:out value="${course.name}" />&nbsp;<a
          class="btn btn-primary" href="${s:mvcUrl('AC#editExperiment').arg(0, experiment.id).build()}">
          <i class="fas fa-user-edit"></i> Edit
        </a>&nbsp;<a
          class="btn btn-danger" href="${s:mvcUrl('AC#deleteExperiment').arg(0, experiment.id).build()}">
          <i class="fas fa-user-minus"></i> Delete
        </a>
       </h1>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <h2>Description</h2>
      <p class="lead">
        <c:out value="${experiment.description}" />
      </p>
    </div>
  </div>
  <div class="row mb-4">
    <div class="col-md-6">
      <div class="card">
        <div class="card-body">
          <h2 class="card-title">${experiment.numOfInstructors} instructor${experiment.numOfInstructors eq 1 ? '' : 's'}</h2>
          <a class="btn btn-primary" href="${s:mvcUrl('AC#instructorsForExperiment').arg(0, experiment.id).build()}">View and Manage Instructors</a>
        </div>
      </div>
    </div>
    <div class="col-md-6">
      <div class="card">
        <div class="card-body">
          <h2 class="card-title">${experiment.numOfActiveStudents} active student${experiment.numOfActiveStudents eq 1 ? '' : 's'}</h2>
          <a class="btn btn-primary" href="${s:mvcUrl('AC#activeStudentsForExperiment').arg(0, experiment.id).build()}">View and Manage Active Students</a>
        </div>
      </div>
    </div>
  </div>
  <div class="row">
    <div class="col-md-6">
      <div class="card">
        <div class="card-body">
          <h2 class="card-title">${experiment.numOfMeasurements} measurement${experiment.numOfMeasurements eq 1 ? '' : 's'}</h2>
          <c:if test="${empty measurements}">
          <p class="card-text">No measurements added.</p>
          </c:if>
          <c:if test="${not empty measurements}">
          <table class="table">
            <thead>
              <tr>
                <th scope="col">Name</th>
                <th scope="col">Quantity Type</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="measurement" items="${measurements}">
              <tr>
                <td><c:out value="${measurement.name}" /></td>
                <td>${measurement.quantityTypeId}</td>
              </tr>
              </c:forEach>
            </tbody>
          </table>
          </c:if>
        </div>
      </div>
    </div>
    <div class="col-md-6">
      <div class="card">
        <div class="card-body">
          <h2 class="card-title">${experiment.numOfReports} report${experiment.numOfReports eq 1 ? '' : 's'} submitted</h2>
          <a class="btn btn-primary" href="${s:mvcUrl('AC#reportedResultsForExperiment').arg(0, experiment.id).build()}">View and Manage Reports</a>
        </div>
      </div>
    </div>
  </div>
</div>

</t:userpage>