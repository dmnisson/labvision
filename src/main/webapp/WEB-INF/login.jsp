<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:genericpage title="Login">
  <div class="signin-page-div">
	  <form class="signin-form" method="post">
	    <c:if test="${ param.error eq 401 }">
	      <div class="alert alert-warning">
	        Invalid email or password.
	      </div>
	    </c:if>
	    <h1 class="h3 mb3 font-weight-normal">Log in to access your account.</h1>
	    
	    <div class="form-group">
	      <label for="email" class="sr-only">Email</label>
	      <input name="email" id="email" type="email" class="form-control" placeholder="Email" required autofocus />
	    </div>
	    
	    <div class="form-group">
	      <label for="password" class="sr-only">Password</label>
	      <input name="password" id="password" type="password" class="form-control" placeholder="Password" required />
	    </div>
	    
	    <div class="checkbox mb-3">
	      <label>
	        <input type="checkbox" name="rememberMe" />
	        <label for="rememberMe">Remember Me</label>
	      </label>
	    </div>
	    <button class="btn btn-lg btn-primary btn-block">
	      Log In
	    </button>
	  </form>
  </div>
</t:genericpage>