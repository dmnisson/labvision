<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="arialabel" %>
<%@ attribute name="pages" type="java.util.List" %>
<%@ attribute name="currentpage" %>
<%@ attribute name="prevpageurl" %>
<%@ attribute name="nextpageurl" %>
<%@ attribute name="pageurls" type="java.util.Map" %>

<c:set var="arialabel" value="${(empty arialabel) ? 'Page Navigation' : arialabel}" />

<nav aria-label="${arialabel}">
  <ul class="pagination">
   <li class="page-item${not empty prevpageurl ? '' : ' disabled'}">
     <a class="page-link" href="${not empty prevpageurl ? prevpageurl : '#'}">Previous</a>
   </li>
   <c:forEach var="page" items="${pages}">
   <li class="page-item${currentpage eq page ? ' active' : ''}">
     <a class="page-link" href="${pageurls[page]}">${page}</a>
   </li>
   </c:forEach>
   <li class="page-item${not empty nextpageurl ? '' : ' disabled'}">
     <a class="page-link" href="${not empty nextpageurl ? nextpageurl : '#'}">Next</a>
    </li>
  </ul>
</nav>