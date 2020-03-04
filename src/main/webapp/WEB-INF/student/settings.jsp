<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "s" %>
<%@ taglib tagdir = "/WEB-INF/tags" prefix = "t" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<t:userpage title="Account Settings">

<div class="container-fluid p-lg-5 userpage-container">
  <div class="row">
    <div class="col">
      <h1>Account Settings</h1>
    </div>
  </div>
  <div class="row">
    <div class="col">
		  <form method="POST" action="${s:mvcUrl('SC#updateSettings').build()}">
		    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		    <p>
		      <button type="submit" class="btn btn-primary">Save Settings</button>
		    </p>
			  <ul class="nav nav-tabs" id="accountSettingsTabs" role="tablist">
	        <li class="nav-item">
	          <a class="nav-link active" href="#" data-toggle="tab" role="tab" aria-controls="dashboard" aria-selected="true">Dashboard</a>
	        </li>
	      </ul>
			  <div class="tab-content">
			    <div class="tab-pane show active py-2">
		        <div class="row form-group">
		          <div class="col-md-6">
		            <label for="maxCurrentExperiments">Maximum number of current experiments to display</label>
		          </div>
		          <div class="col-md-6">
		            <input
		              type="number"
		              class="form-control"
		              id="maxCurrentExperiments"
		              name="maxCurrentExperiments"
		              value="${prefs.maxCurrentExperiments}"
		              placeholder="${defaults.maxCurrentExperiments}"
		              min="1"
		            />
		          </div>
		        </div>
		        <div class="row form-group">
              <div class="col-md-6">
                <label for="maxRecentExperiments">Maximum number of recent experiments to display</label>
              </div>
              <div class="col-md-6">
                <input
                  type="number"
                  class="form-control"
                  id="maxRecentExperiments"
                  name="maxRecentExperiments"
                  value="${prefs.maxRecentExperiments}"
                  placeholder="${defaults.maxRecentExperiments}"
                  min="1"
                />
              </div>
            </div>
            <div class="row form-group">
              <div class="col-md-6">
                <label for="maxRecentCourses">Maximum number of recent courses to display</label>
              </div>
              <div class="col-md-6">
                <input
                  type="number"
                  class="form-control"
                  id="maxCurrentCourses"
                  name="maxCurrentCourses"
                  value="${prefs.maxRecentCourses}"
                  placeholder="${defaults.maxRecentCourses}"
                  min="1"
                />
              </div>
            </div>
			    </div>
		    </div>
		  </form>
    </div>
  </div>
</div>

</t:userpage>