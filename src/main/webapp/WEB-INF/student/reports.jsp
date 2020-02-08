<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<t:userpage title="Reports">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col">
      <h1>Reports</h1>
    </div>
  </div>
  
  <c:choose>
  <c:when test="${empty reports}">
  <div class="row">
    <div class="col">
      <p class="text-center">No reports submitted.</p>
    </div>
  </div>
  </c:when>
  <c:otherwise>
  <table class="table">
    <thead>
      <tr>
        <th scope="col" class="col-2">Report</th>
        <th scope="col" class="col-2">Experiment</th>
        <th scope="col" class="col-2">Course</th>
        <th scope="col" class="col-2">Score</th>
        <th scope="col" class="col-2">Submitted</th>
        <th scope="col" class="col-2"></th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="report" items="${reports}">
        <tr>
          <td class="col-2">
            <a href="${s:mvcUrl('SC#getReport').arg(0, report.id).build()}">${report.name}</a>
          </td>
          <td class="col-2">
            <a href="${s:mvcUrl('SC#getExperiment').arg(0, report.experimentId).build()}">${report.experimentName}</a>
          </td>
          <td class="col-2">
            <a href="${s:mvcUrl('SC#getCourse').arg(0, report.courseId).build()}">${report.courseName}</a>
          </td>
          <td class="col-2">
            <c:if test="${empty report.score}">—</c:if>
            <c:if test="${not empty report.score}">${report.score}</c:if>
          </td>
          <td class="col-2">
            <javatime:format value="${report.added}" style="SS" />
          </td>
          <td>
            <c:if test="${report.editAllowed}">
            <a class="btn btn-primary" href="${s:mvcUrl('SC#editReport').arg(0, report.id).build()}">Edit</a>
            </c:if>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
  </c:otherwise>
  </c:choose>
</div>

</t:userpage>