<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name = "title" %>
<%@ attribute name = "heading" %>
<%@ attribute name = "question" %>
<%@ attribute name = "actionurl" required = "true" %>
<%@ attribute name = "nourl" required = "true" %>
<%@ attribute name = "alertcontext" %>
<%@ attribute name = "nobtncontext" %>
<%@ attribute name = "yesbtncontext" %>

<c:set var="alertcontext" value="${(not empty alertcontext) ? alertcontext : 'info'}" />
<c:set var="nobtncontext" value="${(not empty nobtncontext) ? nobtncontext : 'secondary'}" />
<c:set var="yesbtncontext" value="${(not empty yesbtncontext) ? yesbtncontext : 'primary'}" />

<t:userpage title="${title}">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="alert alert-${alertcontext} confirm-page-alert text-center">
    <h1>${heading}</h1>
    <h2>${question}</h2>
    <form method="POST" action="${actionurl}">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
      <a class="btn btn-${nobtncontext}" href="${nourl}">No</a>
      <button type="submit" class="btn btn-${yesbtncontext}">Yes</button>
    </form>
  </div>
</div>

</t:userpage>

