<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - Cabin Booking System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">

    <style>
        .error-container {
            min-height: 70vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .error-icon {
            font-size: 5rem;
            color: #dc3545;
            margin-bottom: 2rem;
        }

        .error-code {
            font-size: 3rem;
            font-weight: bold;
            color: #495057;
            margin-bottom: 1rem;
        }

        .error-message {
            font-size: 1.2rem;
            color: #6c757d;
            margin-bottom: 2rem;
        }

        .error-details {
            background-color: #f8f9fa;
            border-left: 4px solid #dc3545;
            padding: 1rem;
            margin-bottom: 2rem;
            border-radius: 4px;
        }
    </style>
</head>
<body>
    <!-- Include Header -->
    <jsp:include page="header.jsp" />

    <!-- Main Error Content -->
    <div class="container error-container">
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6 text-center">
                <div class="card dashboard-card shadow">
                    <div class="card-body p-5">

                        <!-- Error Icon -->
                        <div class="error-icon">
                            <c:choose>
                                <c:when test="${pageContext.errorData.statusCode == 404}">
                                    <i class="fas fa-search"></i>
                                </c:when>
                                <c:when test="${pageContext.errorData.statusCode == 403}">
                                    <i class="fas fa-lock"></i>
                                </c:when>
                                <c:when test="${pageContext.errorData.statusCode == 500}">
                                    <i class="fas fa-server"></i>
                                </c:when>
                                <c:otherwise>
                                    <i class="fas fa-exclamation-triangle"></i>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Error Code -->
                        <div class="error-code">
                            <c:choose>
                                <c:when test="${not empty pageContext.errorData.statusCode}">
                                    Error ${pageContext.errorData.statusCode}
                                </c:when>
                                <c:otherwise>
                                    System Error
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Error Message -->
                        <div class="error-message">
                            <c:choose>
                                <c:when test="${pageContext.errorData.statusCode == 404}">
                                    <h4>Page Not Found</h4>
                                    <p>The page you're looking for doesn't exist or has been moved.</p>
                                </c:when>
                                <c:when test="${pageContext.errorData.statusCode == 403}">
                                    <h4>Access Denied</h4>
                                    <p>You don't have permission to access this resource.</p>
                                </c:when>
                                <c:when test="${pageContext.errorData.statusCode == 500}">
                                    <h4>Internal Server Error</h4>
                                    <p>Something went wrong on our end. Please try again later.</p>
                                </c:when>
                                <c:when test="${not empty error}">
                                    <h4>Application Error</h4>
                                    <p>${error}</p>
                                </c:when>
                                <c:otherwise>
                                    <h4>Unexpected Error</h4>
                                    <p>An unexpected error occurred. Please try again.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Error Details (Development Mode) -->
                        <c:if test="${not empty pageContext.exception || not empty pageContext.errorData.throwable}">
                            <div class="error-details text-start">
                                <h6><i class="fas fa-bug"></i> Technical Details:</h6>
                                <c:if test="${not empty pageContext.errorData.requestURI}">
                                    <p class="mb-1"><strong>Request URI:</strong> ${pageContext.errorData.requestURI}</p>
                                </c:if>
                                <c:if test="${not empty pageContext.errorData.servletName}">
                                    <p class="mb-1"><strong>Servlet:</strong> ${pageContext.errorData.servletName}</p>
                                </c:if>
                                <c:if test="${not empty pageContext.exception}">
                                    <p class="mb-1"><strong>Exception:</strong> ${pageContext.exception.class.simpleName}</p>
                                    <p class="mb-0"><strong>Message:</strong> ${pageContext.exception.message}</p>
                                </c:if>
                            </div>
                        </c:if>

                        <!-- Action Buttons -->
                        <div class="d-grid gap-2 d-md-flex justify-content-md-center">
                            <button onclick="history.back()" class="btn btn-outline-secondary">
                                <i class="fas fa-arrow-left"></i> Go Back
                            </button>

                            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">
                                <i class="fas fa-home"></i> Dashboard
                            </a>

                            <c:if test="${pageContext.errorData.statusCode == 500}">
                                <button onclick="location.reload()" class="btn btn-outline-primary">
                                    <i class="fas fa-redo"></i> Try Again
                                </button>
                            </c:if>
                        </div>

                        <!-- Contact Support -->
                        <div class="mt-4 pt-3 border-top">
                            <p class="text-muted small mb-0">
                                <i class="fas fa-info-circle"></i>
                                If this error persists, please contact the system administrator.
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="bg-dark text-light text-center py-3 mt-5">
        <div class="container">
            <p class="mb-0">&copy; 2024 Cabin Booking System</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        // Auto-refresh for server errors (after 30 seconds)
        <c:if test="${pageContext.errorData.statusCode == 500}">
            let refreshTimer = 30;
            const refreshBtn = document.querySelector('button[onclick="location.reload()"]');

            if (refreshBtn) {
                const updateTimer = setInterval(() => {
                    refreshTimer--;
                    refreshBtn.innerHTML = `<i class="fas fa-redo"></i> Try Again (${refreshTimer}s)`;

                    if (refreshTimer <= 0) {
                        clearInterval(updateTimer);
                        refreshBtn.innerHTML = '<i class="fas fa-redo"></i> Try Again';
                        refreshBtn.click();
                    }
                }, 1000);

                // Stop timer if user clicks button
                refreshBtn.addEventListener('click', () => {
                    clearInterval(updateTimer);
                });
            }
        </c:if>

        // Add animation effects
        document.addEventListener('DOMContentLoaded', function() {
            const errorIcon = document.querySelector('.error-icon i');

            // Pulse animation for error icon
            setInterval(() => {
                errorIcon.style.transform = 'scale(1.1)';
                setTimeout(() => {
                    errorIcon.style.transform = 'scale(1)';
                }, 500);
            }, 2000);

            // Add transition
            errorIcon.style.transition = 'transform 0.5s ease';
        });

        // Console logging for debugging
        console.log('Error Page Loaded:', {
            statusCode: '${pageContext.errorData.statusCode}',
            requestURI: '${pageContext.errorData.requestURI}',
            timestamp: new Date().toISOString()
        });
    </script>
</body>
</html>
