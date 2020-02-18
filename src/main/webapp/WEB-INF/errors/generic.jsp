<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:errorpage
  title="Error"
  errorcontext="danger"
  iconclass="far fa-frown"
  mainmessage="Something went wrong!"
  helpmessage="Please contact your system administrator."
  user="${user}"
  dashboardUrl="${dashboardUrl}"
/>
