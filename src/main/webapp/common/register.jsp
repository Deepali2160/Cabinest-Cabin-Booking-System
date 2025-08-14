<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - Yash Technology Cabin Booking</title>
    <link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/register.css" rel="stylesheet">
</head>
<body>
    <div class="register-container">
        <div class="register-card">
            <!-- Header -->
            <div class="register-header">
                <h1>🏢 Yash Technology</h1>
                <p>Join Our Cabin Booking System</p>
            </div>

            <!-- Messages -->
            <div id="message-container">
                <c:if test="${not empty error}">
                    <div class="message error-message">
                        ❌ ${error}
                    </div>
                </c:if>
            </div>

            <!-- Registration Form -->
            <form id="registerForm" action="${pageContext.request.contextPath}/register" method="post" class="register-form">

                <!-- Name and Email Row -->
                <div class="form-row">
                    <div class="form-group">
                        <label for="name">Full Name</label>
                        <input type="text" id="name" name="name" value="${name}" required>
                    </div>

                    <div class="form-group">
                        <label for="email">Email Address</label>
                        <input type="email" id="email" name="email" value="${email}" required>
                    </div>
                </div>

                <!-- Company (Hidden for Single Company) -->
                <div class="form-group company-info">
                    <label>Company</label>
                    <div class="company-display">
                        🏢 Yash Technology - Indore
                    </div>
                    <input type="hidden" name="companyId" value="1">
                </div>

                <!-- ✅ UPDATED: Password Row with Enhanced Requirements -->
                <div class="form-row">
                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" minlength="8" required>
                        <!-- ✅ ENHANCED: Clear password requirements -->
                        <small class="form-help" style="color: #666; font-size: 0.85em;">
                            🔒 <strong>Password must contain:</strong><br>
                            • At least 8 characters<br>
                            • One uppercase letter (A-Z)<br>
                            • One lowercase letter (a-z)<br>
                            • One digit (0-9)<br>
                            • One special character (!@#$%^&*)
                        </small>
                    </div>

                    <div class="form-group">
                        <label for="confirmPassword">Confirm Password</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" required>
                        <!-- ✅ NEW: Password match helper -->
                        <small class="form-help">Re-enter the same password</small>
                    </div>
                </div>

                <!-- Submit Button -->
                <button type="submit" class="register-btn">
                    👤 Create Account
                </button>
            </form>

            <!-- Footer -->
            <div class="register-footer">
                <p>Already have an account?
                    <a href="${pageContext.request.contextPath}/login">Login here</a>
                </p>
                <!-- ✅ NEW: Security note -->
                <small style="color: #888; margin-top: 10px; display: block;">
                    🛡️ Your password is encrypted with enterprise-grade security
                </small>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/js/register.js"></script>
</body>
</html>
