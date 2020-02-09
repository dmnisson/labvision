<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:userpage title="My Courses">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col">
      <h1>My Courses</h1>
    </div>
  </div>
  <div class="row">
    <div class="col">
      <table class="table">
        <thead>
          <tr>
            <th scope="col">Course</th>
            <th scope="col">Enrolled Class</th>
            <th scope="col">Experiments</th>
            <th scope="col">Next Report Due</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="course" items="${courses}">
          <tr>
            <td>
              <a href="${s:mvcUrl('SC#getCourse').arg(0, course.id).build()}"><c:out value="${course.name}" /></a>
            </td>
            <td>
              <c:out value="${course.enrolledClass}" />
            </td>
            <td>
              ${course.numOfExperiments}
            </td>
            <td>
              <c:if test="${empty course.nextReportDue}">—</c:if>
              <c:if test="${not empty course.nextReportDue}">
                <javatime:format value="${course.nextReportDue}" style="M-" />
              </c:if>
            </td>
          </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
</div>

</t:userpage>