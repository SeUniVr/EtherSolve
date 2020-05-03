<%--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <script src="https://code.jquery.com/jquery-3.5.0.min.js" type="text/javascript"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <script src="${pageContext.request.contextPath}/js/app-ajax.js" type="text/javascript"></script>
    <title>EtherSolve</title>
</head>
<body>
    <p>Context: ${pageContext.request.contextPath}</p>
    <h1>Hello ${name}!</h1>

    <label for="name">Name: </label><input type="text" name="name" id="name"/>
    <button onclick="buttonHandler();">Click me</button><br>

    <h2>Response:</h2>
    <div id="ajax-response">

    </div>
</body>
</html>