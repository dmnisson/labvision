<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/fmt" prefix = "fmt" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
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
          <c:if test="${empty currentExperiments}">
          <div class="text-center">No current experiments.</div>
          </c:if>
          <c:if test="${not empty currentExperiments}">
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
	              <c:forEach var="experiment" items="${currentExperiments}">
	              <tr>
	                <td class="col-3">
	                  <a href="/student/experiment/${experiment.id}">${experiment.name}</a>
	                </td>
	                <td class="col-3">
	                  <javatime:format value="${experiment.reportDueDate}" style="S-" />
	                </td>
	                <td class="col-3">
	                  <c:if test="${not empty experiment.lastReportUpdated}">
	                    <a href="/student/experiment/${experiment.id}#reports">
	                      Last updated <javatime:format value="${experiment.lastReportUpdated}" style="S-" />
	                    </a>
	                  </c:if>
	                  <c:if test="${empty experiment.lastReportUpdated}">
	                    <a href="/student/report/new" class="btn btn-primary">Submit</a>
	                  </c:if>
	                </td>
	                <td class="col-3">
	                  <c:if test="${experiment.totalReportScore}">
                      ${experiment.totalReportScore}
                    </c:if>
                    <c:if test="${not experiment.totalReportScore}">
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
          <c:if test="${empty pastExperiments}">
          <div class="text-center">No past experiments.</div>
          </c:if>
          <c:if test="${not empty pastExperiments}">
          <div class="table-responsive">
            <table class="table table-fixed">
              <thead>
                <tr>
                  <th scope="col" class="col-3">Experiment</th>
                  <th scope="col" class="col-3">Reports</th>
                  <th scope="col" class="col-3">Submitted</th>
                  <th scope="col" class="col-3">Score</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="experiment" items="${pastExperiments}">
                <tr>
                  <td class="col-3">
                    <a href="/student/experiment/${experiment.id}">${experiment.name}</a>
                  </td>
                  <td class="col-3">
                    <a href="/student/experiment/${experiment.id}#reports">
                      ${experiment.reportCount}
                    </a>
                  </td>
                  <td class="col-3">
                    <c:choose>
                    <c:when test="${empty experiment.lastReportUpdated}">
                    –
                    </c:when>
                    <c:otherwise>
                    <fmt:formatDate value="${experiment.lastReportUpdated}"
                      dateStyle="short" />
                    </c:otherwise>
                    </c:choose>
                  </td>
                  <td class="col-3">
                   <c:if test="${not empty experiment.totalReportScore}">
                      ${experiment.totalReportScore}
                    </c:if>
                    <c:if test="${empty experiment.totalReportScore}">
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
      </div>
    </div>
  </div>
</div>

</t:userpage>