<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html5>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Welcome to LabVision</title>
</head>
<body>
<div class="container">
	<div class="jumbotron">
		<h1>Welcome to LabVision!</h1>
		<p class="lead">Record and track lab results in your academic courses.</p>
	</div>
	<div class="card">
		<div class="card-body">
			<h3 class="card-title">Students</h3>
			<p class="card-text">Sign in to view and manage your results.</p>
			<form method="POST" action="/login/student">
				<div class="form-group">
					<label for="email">Email address</label>
					<input type="email" id="email" name="email" class="form-control" />
				</div>
				<div class="form-group">
					<label for="password">Password</label>
					<input type="password" id="password" name="password" class="form-control" />
				</div>
				<button type="submit" class="btn btn-primary">Submit</button>
			</form>
		</div>
	</div>
</div>
</body>
</html>