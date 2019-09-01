<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ tag description="Generic User Page Layout" language="java" pageEncoding="UTF-8"%>
<%@ attribute name="title" %>
<%@ attribute name="activenavitem" %>
<%@ attribute name="navbarModel" %>
<t:genericpage title="${title}">

<nav class="navbar fixed-top navbar-expand-md navbar-light bg-light">
  <a class="navbar-brand" href="/">LabVision</a>
  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#labVisionNavbarContent" aria-controls="labVisionNavbarContent" aria-expanded="false" aria-label="Toggle navigation links">
    <span class="navbar-toggler-icon"></span>
  </button>
  
  <div class="collapse navbar-collapse" id="labVisionNavbarContent">
    <ul class="navbar-nav">
      <c:forEach var="link" items="${navbarModel.navLinks}">
      <c:if test="${link.isDropdown()}">
      <li class="nav-item dropdown">
        <a href="${link.url}" class="nav-link dropdown-toggle" id="dropdown_${link.hashCode()}" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">${link.pageName}</a>
        <div class="dropdown-menu" aria-labelledby="dropdown_${link.hashCode()}">
          <c:forEach var="dropLink" items="${link.dropdownMenu}">
          <a class="dropdown-item" href="${dropLink.url}">${dropLink.pageName}</a>
          </c:forEach>
        </div>
      </li>
      </c:if>
      <c:if test="${!link.isDropdown()}">
      <li class="nav-item${activenavitem == link.pageName ? ' active' : ''}">
        <a class="nav-link" href="${link.url}">${link.pageName}</a>
      </li>
      </c:if>
      </c:forEach>
    </ul>
    <form class="form-inline ml-auto my-0" method="POST" action="${navbarModel.logoutLink}">
      <button class="btn btn-outline-info my-2" type="submit">Log Out</button>
    </form>
  </div>
</nav>

<jsp:doBody />

</t:genericpage>