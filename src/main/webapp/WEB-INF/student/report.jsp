<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page
  language="java"
  contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"
%>
<t:userpage title="${fn:escapeXml(report.name)} - ${fn:escapeXml(experiment.name)} - ${fn:escapeXml(experiment.courseName)}">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col">
      <h1><c:out value="${report.name}" /> - <c:out value="${experiment.name}" /> <a
        class="btn btn-primary"
        href="${s:mvcUrl('SC#editReport').arg(0, report.id).build()}"
      >Edit</a></h1>
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
      <t:reportdocumentlink
        documentType="${report.documentType}"
        documentFileType="${report.documentFileType}"
        reportDocumentURL="${reportDocumentUrl}"
        filename="${report.filename}"
        score="${report.score}"
        documentLastUpdated="${report.documentLastUpdated}"
      /> 
    </div>
  </div>
</div>

</t:userpage>