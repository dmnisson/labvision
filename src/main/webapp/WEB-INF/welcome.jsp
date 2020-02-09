<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<t:genericpage title="Welcome to LabVision">
	<div class="container">
	  <div class="row">
		  <div class="col">
				<div class="jumbotron">
					<h1>Welcome to LabVision!</h1>
					<p class="lead">Record and track lab results in your academic courses.</p>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-6">
				<div class="card">
					<div class="card-body">
					  <c:if test="${empty user}">
						<h3 class="card-title">Students and Faculty</h3>
						<p class="card-text">Sign in to view and manage lab results.</p>
						<form method="POST" action="/login">
						  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							<div class="form-group">
								<label for="username">Username</label>
								<input type="text" id="username" name="username" class="form-control" />
							</div>
							<div class="form-group">
								<label for="password">Password</label>
								<input type="password" id="password" name="password" class="form-control" />
							</div>
							<div class="form-check mb-3">
                <input class="form-check-input" type="checkbox" name="remember-me" />
                <label class="form-check-label" for="remember-me">Remember Me</label>
				      </div>
							<button type="submit" class="btn btn-primary">Submit</button>
						</form>
						</c:if>
						<c:if test="${not empty user}">
						  <h3 class="card-title">Welcome back, <c:out value="${user.displayName}" />!</h3>
						  <a class="btn btn-primary" href="${dashboardUrl}">Go To Dashboard</a>
						  <form method="POST" action="/logout">
						    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						    Not <c:out value="${user.displayName}" />? <button type="submit" class="btn btn-link">Log Out</button>
						  </form>
						</c:if>
					</div>
				</div>
			</div>
			<div class="col-md-6">
			  <div class="card">
			    <div class="card-body">
			      <h3 class="card-title">Don't have an account?</h3>
			      <p class="card-text">Contact your institution to have an account created.</p>
			    </div> 
			  </div>
			</div>
		</div>
  </div>
</t:genericpage>