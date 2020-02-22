<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib uri = "http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:userpage title="Reports for ${fn:escapeXml(experiment.name)} – ${fn:escapeXml(course.name)}">

<div class="container-fluid p-lg-5 userpage-container">
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb">
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#courses').build()}">Courses</a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#getCourse').arg(0, course.id).build()}"><c:out value="${course.name}" /></a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#experiments').arg(0, course.id).build()}"><c:out value="${course.name}" /> Classes</a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#getExperiment').arg(0, experiment.id).build()}"><c:out value="${experiment.name}" /></a></li>
      <li class="breadcrumb-item active" aria-current="page">Reports for <c:out value="${experiment.name}" /></li>
    </ol>
  </nav>
  <div class="row">
    <div class="col">
      <h1>Reports for <c:out value="${experiment.name}" /></h1>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <c:if test="${empty reports.content}">
        No reports submitted yet.
      </c:if>
      <c:if test="${not empty reports.content}">
      <div class="table-responsive">
        <table class="table">
          <thead>
            <tr>
              <th scope="col">Report</th>
              <th scope="col">Student</th>
              <th scope="col">Submitted</th>
              <th scope="col">Score</th>
              <th scope="col"></th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="report" items="${reports.content}">
            <tr>
              <td><a href="${s:mvcUrl('AC#getReportedResult').arg(0, report.id).build()}"><c:out value="${report.name}" /></a></td>
              <td><a href="${s:mvcUrl('AC#getUser').arg(0, report.studentId).build()}"><c:out value="${report.studentUsername}" /></a></td>
              <td><javatime:format value="${report.added}" style="SS" /></td>
              <td>${report.score}</td>
              <td>
                <div class="btn-group" role="group" aria-label="Actions for ${fn:escapeXml(report.name)}">
	                <a class="btn btn-primary" href="${s:mvcUrl('AC#editReportedResult').arg(0, report.id).build()}">
                    <i class="far fa-edit"></i> Edit
                  </a>
	                <a class="btn btn-primary" href="${s:mvcUrl('AC#deleteReportedResult').arg(0, report.id).build()}">
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
      <a class="btn btn-primary" href="${s:mvcUrl('AC#newReportedResult').arg(0, experiment.id).build()}">
        <i class="fas fa-plus"></i> New Report
      </a>
    </div>
  </div>
</div>

</t:userpage>