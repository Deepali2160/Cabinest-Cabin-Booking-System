<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Yash Technology Cabin Booking</title>
    <link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/login.css" rel="stylesheet">
</head>
<body>
    <div class="login-container">
        <div class="login-card">
            <!-- Header -->
            <div class="login-header">
                <h1>🏢 Yash Technology</h1>
                <p>Cabin Booking System</p>
            </div>

            <!-- Messages -->
            <div id="message-container">
                <c:if test="${not empty error}">
                    <div class="message error-message">
                        ❌ ${error}
                    </div>
                </c:if>

                <c:if test="${not empty message}">
                    <div class="message success-message">
                        ✅ ${message}
                    </div>
                </c:if>
            </div>

            <!-- Login Form -->
            <form id="loginForm" action="${pageContext.request.contextPath}/login" method="post" class="login-form">
                <div class="form-group">
                    <label for="email">Email Address</label>
                    <input type="email" id="email" name="email" value="${email}" required>
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required>
                </div>

                <button type="submit" class="login-btn">
                    🔐 Login
                </button>
            </form>

            <!-- Footer -->
            <div class="login-footer">
                <p>Don't have an account?
                    <a href="${pageContext.request.contextPath}/register">Register here</a>
                </p>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/js/login.js"></script>
</body>
</html>
