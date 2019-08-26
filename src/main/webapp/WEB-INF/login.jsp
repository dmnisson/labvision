<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html5>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
  <title>Login</title>
</head>
<body class="text-center">
  <form class="signin-form" method="post">
    <c:if test="${ param.error eq 401 }">
      <div class="alert alert-warning">
        Invalid email or password.
      </div>
    </c:if>
    <h1 class="h3 mb3 font-weight-normal">Log in to access your account.</h1>
    
    <label for="email" class="sr-only">Email</label>
    <input name="email" id="email" type="email" class="form-control" placeholder="Email" required autofocus />
    
    <label for="password" class="sr-only">Password</label>
    <input name="password" id="password" type="password" class="form-control" placeholder="Password" required />
    <div class="checkbox mb-3">
      <label>
        <input type="checkbox" name="rememberMe" />
      </label>
    </div>
    <button class="btn btn-lg btn-primary btn-block">
      Log In
    </button>
  </form>
</body>
</html>