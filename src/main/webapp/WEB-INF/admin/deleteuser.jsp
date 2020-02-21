<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<t:confirmpage
  title = "Confirm Delete User ${fn:escapeXml(user.username)}"
  heading = "Confirm Delete"
  question = "Are you sure you want to delete ${fn:escapeXml(user.username)}'s account?"
  actionurl = "${s:mvcUrl('AC#confirmDeleteUser').arg(0, user.id).build()}"
  nourl = "${s:mvcUrl('AC#getUser').arg(0, user.id).build()}"
  nobtncontext = "primary"
  yesbtncontext = "danger"
/>