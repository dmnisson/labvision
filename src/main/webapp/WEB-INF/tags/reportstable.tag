<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="id" %>
<%@ attribute name="reports" type="java.util.List" %>

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
          <a href="${s:mvcUrl('FC#getReport').arg(0, report.id).build()}">${report.name}</a>
        </td>
        <td>
          <javatime:format value="${report.added}" style="SS" />
        </td>
        <td>
          ${report.score}
        </td>
        <td>
          <a class="btn btn-primary" href="${s:mvcUrl('FC#editReportScore').arg(0, report.id).build()}">View and Score</a>
        </td>
      </tr>
      </c:forEach>
    </tbody>
  </table>
</div>