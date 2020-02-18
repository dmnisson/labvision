<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:errorpage
  title="Access denied"
  iconclass="fas fa-ban"
  errorcontext="danger"
  mainmessage="Access denied"
  helpmessage="You are not allowed to access the specified resource."
  user="${user}"
  dashboardUrl="${dashboardUrl}"
/>