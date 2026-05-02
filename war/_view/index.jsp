<!DOCTYPE html>
<html>
<head>
	<title>The TBAG</title>
	<style>
		body { font-family: sans-serif; text-align: center; margin-top: 100px; background-color: #111; color: white;}
		button { padding: 15px 30px; font-size: 18px; margin: 10px; cursor: pointer; background-color: #444; color: white; border: 1px solid white;}
		button:hover { background-color: #666; }
	</style>
</head>
<body>
<h1>Welcome to The Hollow</h1>

<form action="${pageContext.request.contextPath}/index" method="post">
	<button type="submit" name="action" value="continue">Continue Game</button>
	<button type="submit" name="action" value="new">New Game</button>
</form>
</body>
</html>