<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<t:userpage title="${scoring ? 'Scoring ' : ''}${report.name}">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col">
      <h1>${report.name}</h1><br />
      <h2>${report.studentName}</h2>
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
      <h2>Document</h2>
      <t:reportdocumentlink
        documentType="${report.documentType}"
        documentFileType="${report.documentFileType}"
        reportDocumentURL="${reportDocumentURL}"
        filename="${report.filename}"
        score="${report.score}"
        documentLastUpdated="${report.documentLastUpdated}"
      /> 
    </div>
  </div>
  
  <div class="row">
    <div class="col">
      <c:choose>
      
		  <c:when test="${not scoring}">
		  <a class="btn btn-primary" href="${scorePath}">Score Report</a>
		  </c:when>
		  
		  <c:otherwise>
		  <form method="POST" action="${scorePath}">
		    <t:csrfsalt value="${csrfSalt}" />
		    <div class="form-group">
		      <label for="score">Score</label>
		      <input type="number" id="score" name="score" value="${score}" step="0.01" />
		    </div>
		    <button type="submit" class="btn btn-primary">Submit Score</button>
		  </form>
		  </c:otherwise>
		  
		  </c:choose>
    </div>
  </div>
</div>

</t:userpage>