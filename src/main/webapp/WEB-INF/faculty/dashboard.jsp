<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:userpage title="Dashboard">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col">
      <h1>Welcome, ${dashboardModel.instructor.displayName}!</h1>
    </div>
  </div>
</div>

</t:userpage>