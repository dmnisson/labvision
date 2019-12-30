<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page
  language="java"
  contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"
%>
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
        documentType="${documentType}"
        documentFileType="${documentFileType}"
        reportDocumentURL="${reportDocumentURL}"
        filename="${filename}"
        score="${score}"
        documentLastUpdated="${documentLastUpdated}"
      /> 
    </div>
  </div>
</div>

</t:userpage>