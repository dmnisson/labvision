<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ attribute name = "title" %>
<%@ attribute name = "errorcontext" %>
<%@ attribute name = "iconclass" %>
<%@ attribute name = "mainmessage" %>
<%@ attribute name = "helpmessage" %>
<%@ attribute name = "user" %>
<%@ attribute name = "dashboardUrl" %>

<t:genericpage title="${title}">
<div class="container-fluid p-lg-5">
  <div class="alert alert-${errorcontext} error-page-alert text-center" role="alert">
    <h1><c:if test="${not empty iconclass}"><i class="${iconclass}"></i> </c:if>${mainmessage}</h1>
    <h2>${helpmessage}</h2>
    <p>
      <c:if test="${not empty user}">
      <a class="btn btn-primary mx-2" href="${dashboardUrl}">Dashboard</a>
      </c:if>
      <a class="btn btn-primary mx-2" href="/">Home</a>
    </p>
  </div>
</div>
</t:genericpage>