<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<%@ page import = "io.github.dmnisson.labvision.entities.ReportDocumentType" %>
<%@ page import = "io.github.dmnisson.labvision.entities.FileType" %>
<t:userpage title="${empty report.name ? 'New Report' : 'Editing '}${fn:escapeXml(report.name)} - ${fn:escapeXml(experiment.name)} - ${fn:escapeXml(experiment.courseName)}">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col">
      <h1>
        <c:choose>
        <c:when test="${empty report.name}">New Report for <c:out value="${experiment.name}" /></c:when>
        <c:otherwise>Editing <c:out value="${report.name}" /></c:otherwise>
        </c:choose>
      </h1>
    </div>
  </div>
  
  <div class="row">
    <div class="col">
      <h2>Accepted Results</h2>
      <table class="table">
        <thead>
	        <tr>
	          <th scope="col">Quantity</th>
	          <th scope="col">Value</th>
	        </tr>
        </thead>
        <tbody>
          <c:forEach var="acceptedResult" items="${acceptedResults}">
          <tr>
            <th scope="row"><c:out value="${acceptedResult.name}" /></th>
            <td>${acceptedResult.value} ± ${acceptedResult.uncertainty} ${acceptedResult.quantityTypeId.unitString}</td>
          </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
  
  <div class="row">
    <div class="col">
      <h2>Report Document</h2>
      
      <c:set var="EXTERNAL" value="<%= ReportDocumentType.EXTERNAL %>" />
      <c:set var="FILESYSTEM" value="<%= ReportDocumentType.FILESYSTEM %>" />
      
      <c:set var="PDF" value="<%= FileType.PDF %>" />
      <c:set var="WORD" value="<%= FileType.WORD %>" />
      <c:set var="WORD_COMPAT" value="<%= FileType.WORD_COMPAT %>" />
      
      <form method="POST" action="${actionUrl}" enctype="multipart/form-data">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        <div class="form-group">
          <label for="reportName">Name of report</label>
          <input class="form-control" type="text" name="reportName" id="reportName" value="${fn:escapeXml(report.name)}" />
        </div>
	      <div class="form-group" role="radiogroup">
		      <div class="form-check form-check-inline">
		        <input
		          class="form-check-input"
		          type="radio"
		          name="documentType"
		          id="documentTypeExternal"
		          value="EXTERNAL"
		          data-toggle="collapse"
		          data-target=".externalDocument:not(.show),.filesystemDocument.show"
		          aria-checked="${report.documentType eq EXTERNAL}"
		          <c:if test="${report.documentType eq EXTERNAL}">checked</c:if>
		        />
		        <label class="form-check-label" for="documentTypeExternal">URL</label>
		      </div>
		      <div class="form-check form-check-inline">
		        <input
		          class="form-check-input"
		          type="radio"
		          name="documentType"
		          id="documentTypeFilesystem"
		          value="FILESYSTEM"
		          data-toggle="collapse"
		          data-target=".filesystemDocument:not(.show),.externalDocument.show"
		          aria-checked="${report.documentType eq FILESYSTEM}"
		          <c:if test="${report.documentType eq FILESYSTEM}">checked</c:if>
		        />
		        <label class="form-check-label" for="documentTypeFilesystem">File</label>
		      </div>
	      </div>
	      <div class="form-group collapse externalDocument${report.documentType eq EXTERNAL ? ' show' : ''}">
	        <label for="externalDocumentURL">URL of document</label>
	        <input class="form-control" type="text" name="externalDocumentURL" value="${reportDocumentURL}" placeholder="Address of your report document, e.g. on Google Drive" />
	      </div>
	      <div class="form-group collapse filesystemDocument${report.documentType eq FILESYSTEM ? ' show' : ''}">
		      <c:choose>
	        <c:when test="${empty report.filename or uploadfile eq 'true' or report.documentType ne FILESYSTEM}">        
          <label for="filesystemDocumentFile">Upload your report</label>
          <input class="form-control-file" type="file" name="filesystemDocumentFile" />
          <p>Maximum file size allowed: <s:eval expression="@environment.getProperty('spring.servlet.multipart.max-file-size')" /></p>
	        </c:when>
          <c:otherwise>
          <t:reportdocumentlink
            documentType="FILESYSTEM"
		        documentFileType="${report.documentFileType}"
		        reportDocumentURL="${reportDocumentUrl}"
		        filename="${report.filename}"
            changeButtonPath="${s:mvcUrl('SC#editReport').arg(0, report.id).arg(1, true).build()}"
          />
	        </c:otherwise>
          </c:choose>
        </div>
        <button type="submit" class="btn btn-primary">Submit Report</button>
      </form>
    </div>
  </div>
</div>

</t:userpage>