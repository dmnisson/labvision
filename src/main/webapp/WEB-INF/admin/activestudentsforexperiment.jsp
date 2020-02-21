<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:userpage title="Students active in ${fn:escapeXml(experiment.name)} – ${fn:escapeXml(course.name)}">

<div class="container-fluid p-lg-5 userpage-container">
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb">
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#courses').build()}">Courses</a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#getCourse').arg(0, course.id).build()}"><c:out value="${course.name}" /></a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#experiments').arg(0, course.id).build()}"><c:out value="${course.name}" /> Classes</a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#getExperiment').arg(0, experiment.id).build()}"><c:out value="${experiment.name}" /></a></li>
      <li class="breadcrumb-item active" aria-current="page">Students active in <c:out value="${experiment.name}" /></li>
    </ol>
  </nav>
  <div class="row">
    <div class="col">
      <h1>Students active in <c:out value="${experiment.name}" /></h1>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <c:if test="${empty students.content}">
        No active students.
      </c:if>
      <c:if test="${not empty students.content}">
      <div class="table-responsive">
        <table class="table">
          <thead>
            <tr>
              <th scope="col">Username</th>
              <th scope="col">Student Name</th>
              <th scope="col"></th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="student" items="${students.content}">
            <tr>
              <td><a href="${s:mvcUrl('AC#getUser').arg(0, instructor.id).build()}"><c:out value="${student.username}" /></a></td>
              <td><c:out value="${student.displayName}" /></td>
              <td>
                <a class="btn btn-primary" href="${s:mvcUrl('AC#deactivateStudent').arg(0, experiment.id).arg(1, student.id).build()}">
                  <i class="fas fa-minus"></i> Deactivate
                </a>
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
      <form class="form-inline" method="POST" action="${activateStudentActionUrl}">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        <label class="sr-only" for="studentUsername">Username</label>
        <input type="text" class="form-control mb-2 mr-sm-2" id="studentUsername" name="studentUsername" placeholder="Student username" />
        
        <button type="submit" class="btn btn-primary mb-2"><i class="fas fa-plus"></i> Activate</button>
      </form>
    </div>
  </div>
</div>

</t:userpage>