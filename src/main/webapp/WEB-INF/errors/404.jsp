<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:errorpage
  title="Resource not found"
  errorcontext="info"
  mainmessage="Resource not found"
  helpmessage="The specified resource was not found. Please check the spelling of the address in your browser. If the problem persists, please contact your system administrator."
  user="${user}"
  dashboardUrl="${dashboardUrl}"
/>