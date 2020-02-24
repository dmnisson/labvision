<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<t:userpage title="Reset Password">

<div class="reset-password-div m-5">
  <form class="reset-password-form" method="POST" action="${actionUrl}">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
    <c:if test="${ param.error eq 'unauthorized' }">
      <div class="alert alert-warning">
        Your username or password was incorrect, please try again.
      </div>
    </c:if>
    <c:if test="${ param.error eq 'tooshort' }">
      <div class="alert alert-warning">
        Password must be at least ${minPasswordLength} characters in length.
      </div>
    </c:if>
    <c:if test="${ param.error eq 'blacklisted' }">
      <div class="alert alert-warning">
        Password must not contain a commonly used sequence, your username,
        your first or last name, your email address, or your phone number.
      </div>
    </c:if>
    <c:if test="${ param.error eq 'unmatched' }">
      <div class="alert alert-warning">
        Passwords do not match.
      </div>
    </c:if>
    
    <h1 class="h3 mb3 font-weight-normal">Reset your password</h1>
    
    <c:if test="${empty token}">
    <div class="form-group">
      <label for="username">Username</label>
      <input type="text" name="username" class="form-control" required autofocus />
    </div>
    
    <div class="form-group">
      <label for="password">Current password</label>
      <input type="password" name="password" class="form-control" required />
    </div>
    </c:if>
    
    <div class="form-group">
      <label for="newPassword">New password</label>
      <input type="password" name="newPassword" class="form-control" required />
    </div>
    
    <div class="form-group">
      <label for="confirmNewPassword">Verify new password</label>
      <input type="password" name="confirmNewPassword" class="form-control" required />
    </div>
    
    <button type="submit" class="btn btn-lg btn-primary btn-block">
      Reset Password
    </button>
  </form>
</div>

</t:userpage>