<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/fmt" prefix = "fmt" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:userpage title="${experiment.name} – ${experiment.course.name}">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col-sm-9">
      <h1>${experiment.name}</h1>
    </div>
    <div class="col-sm-3">
      <button type="button" id="editBtn" class="btn btn-primary">Edit Experiment</button>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <p class="lead">${experiment.description}</p>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <h2>Student Measurements and Reports</h2>
    </div>
  </div>
  <div class="row">
    <div class="col-md-4">
      <div class="table-responsive">
        <table class="table table-fixed" role="treegrid">
          <thead>
            <tr>
              <th scope="col">Class</th>
              <th scope="col">Student</th>
              <th scope="col">Active in Experiment</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="courseClass" items="${experiment.course.courseClasses}">
            <tr id="courseClass${courseClass.id}" >
              <td colspan="3">
                <button class="btn btn-link" type="button" data-toggle="collapse" data-target=".students${courseClass.id}" role="treeitem" aria-level="1">
                  ${courseClass.name}
                </button>
              </td>
            </tr>
            
            <c:forEach var="student" items="${courseClass.students}">
            <tr class="collapse students${courseClass.id}" id="student${student.id}">
              <td></td>
              <td>
                <button class="btn btn-link p-2" type="button" data-toggle="collapse" data-target=".info-${student.id}, .info-unselected" role="treeitem" aria-level="2">
                  ${student.displayName}
                </button>
              </td>
              <td>
              <span class="d-inline-block p-2 tree-table-cell">
	              <c:choose>
	              <c:when test="${fn:contains(courseClass.students, student)}">
	                Yes
	              </c:when>
	              <c:otherwise>
	                No
	              </c:otherwise>
	              </c:choose>
              </span>
              </td>
            </tr>
            </c:forEach>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </div>
    <div class="col-md-8">
      <div class="row">
        <div class="col">
          <h3>Measurement Values</h3>
        </div>
      </div>
      <c:forEach var="measurement" items="${measurements}">
      <div class="row">
        <div class="col">
          <h4>${measurement.name}</h4>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <a class="btn btn-default" href="${editMeasurementPaths[measurement.id]}">Edit Measurement</a>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <div class="collapse show info-unselected" id="measurementValues-unselected">
            <p class="text-center">Select student to view measurement values</p>
          </div>
          <c:forEach var="courseClass" items="${experiment.course.courseClasses}">
          <c:forEach var="student" items="${courseClass.students}">
          <div class="collapse info-${student.id}" id="measurementValues-${courseClass.id}-${student.id}">
            <h5>${student.displayName} for ${courseClass.name}</h5>
	          <t:measurementvaluestable 
	            measurement="${measurement}"
	            measurementvalues="${measurementValues[measurement.id][courseClass.id][student.id]}"
	            parameters="${parameters[measurement.id]}"
              parametervalues="${parameterValues[measurement.id]}"
	            id="measurementValuesTable${measurement.id}-${courseClass.id}-${student.id}"
	            addnewform="false"
	          />
          </div>
          </c:forEach>
          </c:forEach>
        </div>
      </div>
      </c:forEach>
      <h3>Reports</h3>
      <div class="collapse show info-unselected" id="reports-unselected">
        <p class="text-center">Select student to view reports</p>
      </div>
      <c:forEach var="studentId" items="${studentIds}">
      <h5>${student.name}</h5>
      <div class="collapse info-${studentId}" id="reports-${studentId}">
      <t:reportstable
        id="reportsTable${studentId}"
        reports="${reports[studentId]}"
        reportPaths="${reportPaths}"
        reportScorePaths="${reportScorePaths}"
      />
      </div>
      </c:forEach>
    </div>
  </div>
</div>

</t:userpage>