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
      <h2>Student Measurements</h2>
    </div>
  </div>
  <div class="row">
    <div class="col-sm-6">
      <div class="table-responsive">
        <table class="table table-fixed" role="treegrid">
          <thead>
            <tr>
              <th scope="col">Class</th>
              <th scope="col">Student</th>
              <th scope="col">Active</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="courseClass" items="${experiment.course.courseClasses}">
            <tr data-toggle="collapse" id="courseClass${courseClass.id}" data-target=".students${courseClass.id}" role="treeitem" aria-level="1">
              <td colspan="3">${courseClass.name}</td>
            </tr>
            
            <c:forEach var="student" items="${courseClass.students}">
            <tr data-toggle="collapse" id="student${student.id}">
              <td></td>
              <td>${student.name}</td>
              <td>
              <c:choose>
              <c:when test="${fn:contains(courseClass.students, student)}">
                Yes
              </c:when>
              <c:otherwise>
                No
              </c:otherwise>
              </c:choose>
              </td>
            </tr>
            </c:forEach>
            
            </c:forEach>
          </tbody>
        </table>
      </div>
    </div>
    <div class="col-sm-6">
      <div class="row">
        <div class="col">
          <h3>Measurement Values</h3>
        </div>
      </div>
      <c:forEach var="measurement" items="${experiment.measurements}">
      <div class="row">
        <div class="col">
          <h4>${measurement.name}</h4>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <a class="btn btn-default" href="/faculty/measurement/${measurement.id}/edit">Edit Measurement</a>
        </div>
      </div>
      <div class="row">
        <div class="col">
          <p class="text-center">Loading measurement values...</p>
        </div>
      </div>
      </c:forEach>
    </div>
  </div>
</div>

</t:userpage>