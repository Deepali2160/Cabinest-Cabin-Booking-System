<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - Cabin Booking System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body class="bg-light">
    <div class="container d-flex justify-content-center align-items-center min-vh-100">
        <div class="row w-100">
            <div class="col-md-8 col-lg-6 mx-auto">
                <div class="card shadow">
                    <div class="card-body p-4">
                        <div class="text-center mb-4">
                            <h3 class="card-title">🏢 Create Account</h3>
                            <p class="text-muted">Join the Cabin Booking System</p>
                        </div>

                        <!-- Display Error Messages -->
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger" role="alert">
                                <i class="fas fa-exclamation-circle"></i> ${error}
                            </div>
                        </c:if>

                        <!-- Registration Form -->
                        <form action="${pageContext.request.contextPath}/register" method="post">
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="name" class="form-label">Full Name</label>
                                    <input type="text" class="form-control" id="name" name="name"
                                           value="${name}" required>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label for="email" class="form-label">Email Address</label>
                                    <input type="email" class="form-control" id="email" name="email"
                                           value="${email}" required>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="companyId" class="form-label">Company</label>
                                <select class="form-select" id="companyId" name="companyId" required>
                                    <option value="">Select your company</option>
                                    <c:forEach var="company" items="${companies}">
                                        <option value="${company.companyId}">${company.name} - ${company.location}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="password" class="form-label">Password</label>
                                    <input type="password" class="form-control" id="password" name="password"
                                           minlength="6" required>
                                    <div class="form-text">Password must be at least 6 characters long</div>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label for="confirmPassword" class="form-label">Confirm Password</label>
                                    <input type="password" class="form-control" id="confirmPassword"
                                           name="confirmPassword" required>
                                </div>
                            </div>

                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-user-plus"></i> Create Account
                                </button>
                            </div>
                        </form>

                        <div class="text-center mt-3">
                            <p class="mb-0">Already have an account?
                                <a href="${pageContext.request.contextPath}/login" class="text-decoration-none">
                                    Login here
                                </a>
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://kit.fontawesome.com/your-fontawesome-kit.js"></script>

    <script>
        // Password confirmation validation
        document.getElementById('confirmPassword').addEventListener('input', function() {
            const password = document.getElementById('password').value;
            const confirmPassword = this.value;

            if (password !== confirmPassword) {
                this.setCustomValidity('Passwords do not match');
            } else {
                this.setCustomValidity('');
            }
        });
    </script>
</body>
</html>
