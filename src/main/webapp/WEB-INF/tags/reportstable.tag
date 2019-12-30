<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="id" %>
<%@ attribute name="reports" type="java.util.List" %>
<%@ attribute name="reportPaths" type="java.util.Map" %>
<%@ attribute name="reportScorePaths" type="java.util.Map" %>

<div class="table-responsive" id="${id}">
  <table class="table table-fixed w-auto">
    <thead>
      <tr>
        <th scope="col">Name</th>
        <th scope="col">Submitted</th>
        <th scope="col">Score</th>
        <th scope="col"></th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="report" items="${reports}">
      <tr>
        <td>
          <a href="${reportPaths[report.id]}">${report.name}</a>
        </td>
        <td>
          <javatime:format value="${report.added}" style="SS" />
        </td>
        <td>
          ${report.score}
        </td>
        <td>
          <a class="btn btn-primary" href="${reportScorePaths[report.id]}">View and Score</a>
        </td>
      </tr>
      </c:forEach>
    </tbody>
  </table>
</div>