<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/fmt" prefix = "fmt" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:userpage title="${fn:escapeXml(experiment.name)} — ${fn:escapeXml(experiment.course.name)}">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col">
      <h1><c:out value="${experiment.name}" /></h1>
      <h2><c:out value="${experiment.course.name}" /></h2>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <p class="experiment-description"><c:out value="${experiment.description}" /></p>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <h2>Measurements</h2>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <ul class="nav nav-tabs">
        <c:forEach var="measurement" items="${measurements}">
        <li class="nav-item">
          <a class="nav-link${activeMeasurementId eq measurement.id ? ' active' : ''}" id="measurement-${measurement.id}-tab" data-toggle="tab" href="#measurement-${measurement.id}" role="tab" aria-controls="measurement-${measurement.id}" aria-selected="false"><c:out value="${measurement.name}" /></a>
        </li>
        </c:forEach>
      </ul>
      <div class="tab-content">
        <c:forEach var="measurement" items="${measurements}">
        <div class="tab-pane${activeMeasurementId eq measurement.id ? ' active show' : ''}" id="measurement-${measurement.id}" role="tabpanel" aria-labelledby="measurement-${measurement.id}-tab">
          <form method="POST" action="${s:mvcUrl('SC#createMeasurementValue').arg(0, measurement.id).build()}">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	          <t:measurementvaluestable
	            measurement="${measurement}"
	            measurementvalues="${measurementValues[measurement.id]}"
	            parameters="${parameters[measurement.id]}"
	            parametervalues="${parameterValues}"
	            id="measurement-values-table"
	            addnewform="true"
	          />
		        <t:pagenav 
			        pages="${measurementValues_pages[measurement.id]}"
			        currentpage="${measurementValues_currentPage[measurement.id]}"
			        prevpageurl="${measurementValues_prevPageUrl[measurement.id]}"
			        nextpageurl="${measurementValues_nextPageUrl[measurement.id]}"
			        pageurls="${measurementValues_pageUrls[measurement.id]}"
			      />
          </form>
        </div>
        </c:forEach>
      </div>
    </div>
  </div>
  <div class="row" id="reports">
    <div class="col">
      <h2>Reports</h2>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <c:if test="${empty experiment.reportedResults}">
      <p>No reports submitted.</p>
      </c:if>
      <c:if test="${not empty experiment.reportedResults}">
      <div class="table-responsive">
        <table class="table table-fixed">
          <thead>
            <tr>
              <th scope="col" class="col-6">Report</th>
              <th scope="col" class="col-6">Submitted</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="report" items="${reportedResults}">
            <tr>
              <td class="col-5">
                <a href="${s:mvcUrl('SC#getReport').arg(0, report.id).build()}"><c:out value="${report.reportDisplay}" /></a>
              </td>
              <td class="col-5">
                <javatime:format value="${report.added}" style="SS" />
              </td>
              <td class="col-2">
                <a class="btn btn-primary" href="${s:mvcUrl('SC#editReport').arg(0, report.id).build()}">Edit</a>
              </td>
            </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
      </c:if>
    </div>
  </div>
  <div class="row">
    <div class="col-md-6">
      <a class="btn btn-primary" href="${s:mvcUrl('SC#newReport').arg(0, experiment.id).build()}">Submit Report</a>
    </div>
    <div class="col-md-6">
      <p>Report is due on <javatime:format value="${experiment.reportDueDate}" style="L-" />.</p>
    </div>
  </div>
</div>

</t:userpage>