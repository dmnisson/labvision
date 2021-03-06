<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/fmt" prefix = "fmt" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:userpage title="Experiments">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col">
      <h1>Experiments</h1>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <c:if test="${empty experiments}">
      <p>No experiments to display.</p>
      </c:if>
      <c:if test="${not empty experiments}">
      <div class="table-responsive">
        <table class="table table-fixed">
          <thead>
            <tr>
              <th scope="col">Experiment</th>
              <th scope="col">Reports Submitted</th>
              <th scope="col">Average Score</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="experiment" items="${experiments}">
            <tr>
              <td>
                <a href="${s:mvcUrl('FC#getExperiment').arg(0, experiment.id).build()}">${fn:escapeXml(experiment.name)}</a>
              </td>
              <td>
                ${experiment.reportedResultsCount}
              </td>
              <td>
                <c:choose>
	                <c:when test="${experiment.averageStudentScore eq null}">
	                  —
	                </c:when>
	                <c:otherwise>
	                  ${experiment.averageStudentScore}
	                </c:otherwise>
                </c:choose>
              </td>
            </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
      </c:if>
    </div>
  </div>
</div>

</t:userpage>