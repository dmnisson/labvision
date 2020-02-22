<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib uri = "http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<t:userpage title="${fn:escapeXml(report.name)} – ${fn:escapeXml(report.experiment.name)}">

<div class="container-fluid p-lg-5 userpage-container">
  <nav aria-label="breadcrumbs">
    <ol class="breadcrumb">
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#courses').build()}">Courses</a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#getCourse').arg(0, report.experiment.course.id).build()}"><c:out value="${report.experiment.course.name}" /></a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#experiments').arg(0, report.experiment.course.id).build()}"><c:out value="${report.experiment.course.name}" /> Experiments</a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#getExperiment').arg(0, report.experiment.id).build()}"><c:out value="${report.experiment.name}" /></a></li>
      <li class="breadcrumb-item"><a href="${s:mvcUrl('AC#reportedResultsForExperiment').arg(0, report.experiment.id).build()}">Reports for <c:out value="${report.experiment.name}" /></a></li>
      <li class="breadcrumb-item active" aria-current="page"><c:out value="${report.name}" /></li>
    </ol>
  </nav>
  <div class="row">
    <div class="col">
      <h1><c:out value="${report.name}" /> – <c:out value="${report.experiment.name}" /></h1>
      <p>Submitted <javatime:format value="${report.added}" style="LL" /> by <c:out value="${report.student.displayName}" /></p>
      <p>
        <a class="btn btn-primary" href="${s:mvcUrl('AC#editReportedResult').arg(0, report.id).build()}">
          <i class="far fa-edit"></i> Edit
        </a>
        <a class="btn btn-danger" href="${s:mvcUrl('AC#deleteReportedResult').arg(0, report.id).build()}">
          <i class="fas fa-minus"></i> Delete
        </a>
      </p>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <h2>Document</h2>
      <c:if test="${empty report.reportDocument}">
      <p>
        No document submitted.<c:if test="${not empty report.score}"> Score: ${report.score}</c:if>
      </p>
      </c:if>
      <c:if test="${not empty report.reportDocument}">
      <t:reportdocumentlink
        documentType="${report.reportDocument.documentType}"
        documentFileType="${report.reportDocument.fileType}"
        reportDocumentURL="${reportDocumentUrl}"
        filename="${report.reportDocument.filename}"
        score="${report.score}"
        documentLastUpdated="${report.reportDocument.lastUpdated}"
      />
      </c:if>
    </div>
  </div>
</div>

</t:userpage>