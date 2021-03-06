<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ tag description="Generic Page Layout" language="java" pageEncoding="UTF-8"%>
<%@ attribute name="title" %>
<%@ attribute name="style" fragment="true" %>
<%@ attribute name="script" fragment="true" %>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/css/tempusdominus-bootstrap-4.min.css" integrity="sha384-8wYGNo4TwC9xzqNRdt7OUN789eBPzNQlO/sxIKaJR1gkX0+Ok1kXxhHR4pZU+gP2" crossorigin="anonymous">
  <link rel="stylesheet" href="/css/site.css?v=1">
  <link rel="stylesheet" href="/css/fa-all.min.css">
  <title>${title}</title>
  
  <c:if test="${not empty style}">
  <style>
    <jsp:invoke fragment="style" />
  </style>
  </c:if>
</head>
<body>
  <jsp:doBody />
  
  <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
  <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.24.0/moment.min.js" integrity="sha384-fYxN7HsDOBRo1wT/NSZ0LkoNlcXvpDpFy6WzB42LxuKAX7sBwgo7vuins+E1HCaw" crossorigin="anonymous"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/js/tempusdominus-bootstrap-4.min.js" integrity="sha384-hzexvprs0k2Q/IHSJOfegsjdg6kTcTTVxQdgHiB4+I/915hcvse9v42LLVVM5K4e" crossorigin="anonymous"></script>
  <c:if test="${not empty script}">
  <script>
    <jsp:invoke fragment="script" />
  </script>
  </c:if>
</body>
</html>
