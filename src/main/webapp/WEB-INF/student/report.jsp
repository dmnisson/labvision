<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="labvision.entities.ReportDocumentType, labvision.entities.FileType" %>
<t:userpage title="${name} - ${experiment.name} - ${experiment.courseName}">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col">
      <h1>${name} - ${experiment.name} <a class="btn btn-primary" href="${editPath}">Edit</a></h1>
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
            <th scope="row">${acceptedResult.name}</th>
            <td>${acceptedResult.value} ± ${acceptedResult.uncertainty} ${acceptedResult.unitString}</td>
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
      
      <div class="row">
        <div class="col">
          <c:choose>
          <c:when test="${documentFileType eq PDF}">
          <i class="far fa-file-pdf"></i>
          </c:when>
          <c:when test="${documentFileType eq WORD or documentFileType eq WORD_COMPAT}">
          <i class="far fa-file-word"></i>
          </c:when>
          <c:otherwise>
          <i class="far fa-file"></i>
          </c:otherwise>
          </c:choose>
          <c:choose>
          <c:when test="${documentType eq EXTERNAL}">
          <a href="${reportDocumentURL}">${filename}</a>
          </c:when>
          <c:when test="${documentType eq FILESYSTEM}">
          <a href="${reportDocumentURL}">${filename}</a>
          </c:when>
          </c:choose>
        </div>
        <div class="col">
          <div class="col">
            Last updated <javatime:format value="${documentLastUpdated}" style="MM" /><br />
            <c:if test="${not empty score}">Score: ${score}</c:if>
          </div>
        </div>
      </div>  
    </div>
  </div>
</div>

</t:userpage>