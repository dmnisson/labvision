<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ tag
  language="java"
  pageEncoding="UTF-8" 
  import="io.github.dmnisson.labvision.entities.ReportDocumentType, io.github.dmnisson.labvision.entities.FileType"
%>
<%@ attribute name="documentType" type="ReportDocumentType" %>
<%@ attribute name="documentFileType" type="FileType" %>
<%@ attribute name="filename" %>
<%@ attribute name="reportDocumentURL" %>
<%@ attribute name="score" type="java.math.BigDecimal" %>
<%@ attribute name="documentLastUpdated" type="java.time.LocalDateTime" %>
<%@ attribute name="changeButtonPath" %>

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
      Last updated <javatime:format value="${documentLastUpdated}" style="MM" />
      <c:choose>
      <c:when test="${not empty changeButtonPath}">
      <a class="btn btn-primary" href="${changeButtonPath}">
        Change
      </a>
      </c:when>
      <c:otherwise><br />
      <c:if test="${not empty score}">Score: ${score}</c:if>
      </c:otherwise>
      </c:choose>
    </div>
  </div>
</div> 