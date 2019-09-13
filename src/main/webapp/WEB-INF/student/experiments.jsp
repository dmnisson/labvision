<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/fmt" prefix = "fmt" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:userpage title="Experiments">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col">
      <h1>My Experiments</h1>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <ul class="nav nav-tabs" id="experimentsTabs" role="tablist">
        <li class="nav-item">
          <a class="nav-link active" id="current-tab" data-toggle="tab" href="#current" role="tab" aria-controls="current" aria-selected="true">Current</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" id="past-tab" href="#past" data-toggle="tab" role="tab" aria-controls="past" aria-selected="true">Past</a>
        </li>
      </ul>
      <div class="tab-content" id="experimentsTabContent">
        <div class="tab-pane show active" id="current" role="tabpanel" aria-labelledby="current-tab">
          <c:if test="${empty experimentsTableModel.currentExperiments}">
          <div class="text-center">No current experiments.</div>
          </c:if>
          <c:if test="${not empty experimentsTableModel.currentExperiments}">
          <div class="table-responsive">
	          <table class="table table-fixed">
	            <thead>
	              <tr>
	                <th scope="col" class="col-3">Experiment</th>
	                <th scope="col" class="col-3">Report Due</th>
	                <th scope="col" class="col-3">Report</th>
	                <th scope="col" class="col-3">Score</th>
	              </tr>
	            </thead>
	            <tbody>
	              <c:forEach var="experiment" items="${experimentsTableModel.currentExperiments}">
	              <tr>
	                <td class="col-3">
	                  <a href="/student/experiment/${experiment.id}">${experiment.name}</a>
	                </td>
	                <td class="col-3">
	                  <fmt:formatDate value="${experiment.reportDueDate}" dateStyle="short"></fmt:formatDate>
	                </td>
	                <td class="col-3">
	                  <c:if test="${experimentsTableModel.reportedResults[experiment]}">
	                    <a href="/student/report/${experimentsTableModel.reportedResults[experiment].id}">
	                      Last updated <fmt:formatDate value="${experimentsTableModel.reportedResults[experiment].lastUpdated}"
	                         dateStyle="short"></fmt:formatDate>
	                    </a>
	                  </c:if>
	                  <c:if test="${not experimentsTableModel.reportedResults[experiment]}">
	                    <a href="/student/report/new">Submit</a>
	                  </c:if>
	                </td>
	                <td class="col-3">
	                  <c:if test="${experimentsTableModel.reportedResults[experiment] and experimentsTableModel.reportedResults[experiment].score}">
                      ${experimentsTableModel.reportedResults[experiment].score}
                    </c:if>
                    <c:if test="${not experimentsTableModel.reportedResults[experiment] or not experimentsTableModel.reportedResults[experiment].score}">
                      —
                    </c:if>
	                </td>
	              </tr>
	              </c:forEach>
	            </tbody>
	          </table>
	        </div>
	        </c:if>
        </div>
        <div class="tab-pane" id="past" role="tabpanel" aria-labelledby="past-tab">
          <c:if test="${empty experimentsTableModel.pastExperiments}">
          <div class="text-center">No past experiments.</div>
          </c:if>
          <c:if test="${not empty experimentsTableModel.pastExperiments}">
          <div class="table-responsive">
            <table class="table table-fixed">
              <thead>
                <tr>
                  <th scope="col" class="col-3">Experiment</th>
                  <th scope="col" class="col-3">Report</th>
                  <th scope="col" class="col-3">Submitted</th>
                  <th scope="col" class="col-3">Score</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="experiment" items="${experimentsTableModel.pastExperiments}">
                <tr>
                  <td class="col-3">
                    <a href="/student/experiment/${experiment.id}">${experiment.name}</a>
                  </td>
                  <td class="col-3">
                    <a href="/student/report/${experimentsTableModel.reportedResults[experiment]}">
                      ${experimentsTableModel.reportStatus[experiment]}
                    </a>
                  </td>
                  <td class="col-3">
                    ${experimentsTableModel.reportedResults[experiment].added}
                  </td>
                  <td class="col-3">
                    ${experimentsTableModel.reportedResults[experiment].score}
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
  </div>
</div>

</t:userpage>