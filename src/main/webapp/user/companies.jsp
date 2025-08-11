<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Browse Companies - Cabin Booking System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">
                <i class="fas fa-building"></i> Cabin Booking System
            </a>

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">
                            <i class="fas fa-tachometer-alt"></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/book">
                            <i class="fas fa-plus-circle"></i> New Booking
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/company/browse">
                            <i class="fas fa-search"></i> Browse Companies
                        </a>
                    </li>
                </ul>

                <ul class="navbar-nav">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
                            <i class="fas fa-user-circle"></i> ${user.name}
                        </a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile">Profile</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">Logout</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container mt-4">

        <!-- Page Header -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card dashboard-card">
                    <div class="card-body">
                        <h2 class="mb-2">
                            <i class="fas fa-search"></i> Browse Companies & Cabins
                        </h2>
                        <p class="text-muted mb-0">
                            Explore available meeting spaces across all partner companies
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Search Bar -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card dashboard-card">
                    <div class="card-body">
                        <div class="row align-items-center">
                            <div class="col-md-8">
                                <div class="input-group">
                                    <span class="input-group-text">
                                        <i class="fas fa-search"></i>
                                    </span>
                                    <input type="text" class="form-control" id="searchInput"
                                           placeholder="Search companies by name or location...">
                                </div>
                            </div>
                            <div class="col-md-4 text-end">
                                <div class="text-muted small">
                                    <i class="fas fa-info-circle"></i> ${companies.size()} companies available
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Companies Grid -->
        <div class="row" id="companiesContainer">
            <c:choose>
                <c:when test="${empty companies}">
                    <div class="col-12">
                        <div class="text-center py-5">
                            <i class="fas fa-building fa-3x text-muted mb-3"></i>
                            <h5 class="text-muted">No Companies Available</h5>
                            <p class="text-muted">Please check back later for available companies.</p>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="company" items="${companies}">
                        <div class="col-md-6 col-lg-4 mb-4 company-card" data-company-name="${company.name}" data-company-location="${company.location}">
                            <div class="card dashboard-card h-100">
                                <div class="card-header bg-gradient text-white"
                                     style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <h6 class="mb-0">
                                            <i class="fas fa-building"></i> ${company.name}
                                        </h6>
                                        <c:if test="${company.status == 'ACTIVE'}">
                                            <span class="badge bg-light text-dark">Active</span>
                                        </c:if>
                                    </div>
                                </div>
                                <div class="card-body">
                                    <div class="mb-3">
                                        <p class="card-text">
                                            <i class="fas fa-map-marker-alt text-danger"></i> ${company.location}
                                        </p>

                                        <c:if test="${not empty company.contactInfo}">
                                            <p class="card-text small text-muted">
                                                <i class="fas fa-phone"></i> ${company.contactInfo}
                                            </p>
                                        </c:if>

                                        <c:if test="${not empty company.description}">
                                            <p class="card-text small">
                                                ${company.description}
                                            </p>
                                        </c:if>
                                    </div>

                                    <!-- Company Stats -->
                                    <div class="row text-center mb-3">
                                        <div class="col-6">
                                            <div class="stat-number text-primary">
                                                <c:choose>
                                                    <c:when test="${not empty companyCabinCounts[company.companyId]}">
                                                        ${companyCabinCounts[company.companyId]}
                                                    </c:when>
                                                    <c:otherwise>
                                                        <i class="fas fa-home"></i>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="stat-label small">Meeting Rooms</div>
                                        </div>
                                        <div class="col-6">
                                            <div class="stat-number text-success">
                                                <i class="fas fa-check-circle"></i>
                                            </div>
                                            <div class="stat-label small">Available</div>
                                        </div>
                                    </div>
                                </div>

                                <div class="card-footer bg-transparent">
                                    <div class="d-grid gap-2">
                                        <a href="${pageContext.request.contextPath}/company/${company.companyId}"
                                           class="btn btn-primary">
                                            <i class="fas fa-eye"></i> View Cabins
                                        </a>

                                        <c:if test="${user.defaultCompanyId != company.companyId}">
                                            <a href="${pageContext.request.contextPath}/book?companyId=${company.companyId}"
                                               class="btn btn-outline-primary btn-sm">
                                                <i class="fas fa-calendar-plus"></i> Quick Book
                                            </a>
                                        </c:if>

                                        <c:if test="${user.defaultCompanyId == company.companyId}">
                                            <div class="text-center">
                                                <small class="text-success">
                                                    <i class="fas fa-star"></i> Your Default Company
                                                </small>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- No Results Message (Initially Hidden) -->
        <div class="row" id="noResultsMessage" style="display: none;">
            <div class="col-12">
                <div class="text-center py-5">
                    <i class="fas fa-search fa-3x text-muted mb-3"></i>
                    <h5 class="text-muted">No Companies Found</h5>
                    <p class="text-muted">Try adjusting your search terms.</p>
                </div>
            </div>
        </div>

        <!-- Popular Companies Section -->
        <div class="row mt-5">
            <div class="col-12">
                <div class="card dashboard-card">
                    <div class="card-header">
                        <h5 class="mb-0">
                            <i class="fas fa-star"></i> Popular Companies
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <c:forEach var="company" items="${companies}" varStatus="status" begin="0" end="2">
                                <div class="col-md-4 mb-3">
                                    <div class="card border-warning">
                                        <div class="card-body p-3">
                                            <h6 class="card-title mb-2">
                                                <i class="fas fa-building text-warning"></i> ${company.name}
                                            </h6>
                                            <p class="card-text small mb-2">
                                                <i class="fas fa-map-marker-alt"></i> ${company.location}
                                            </p>
                                            <a href="${pageContext.request.contextPath}/company/${company.companyId}"
                                               class="btn btn-sm btn-outline-warning">
                                                <i class="fas fa-arrow-right"></i> Explore
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
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
        // Search functionality
        document.getElementById('searchInput').addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase().trim();
            const companyCards = document.querySelectorAll('.company-card');
            const noResultsMessage = document.getElementById('noResultsMessage');
            let visibleCards = 0;

            companyCards.forEach(card => {
                const companyName = card.dataset.companyName.toLowerCase();
                const companyLocation = card.dataset.companyLocation.toLowerCase();

                if (companyName.includes(searchTerm) || companyLocation.includes(searchTerm)) {
                    card.style.display = 'block';
                    visibleCards++;
                } else {
                    card.style.display = 'none';
                }
            });

            // Show/hide no results message
            if (visibleCards === 0 && searchTerm !== '') {
                noResultsMessage.style.display = 'block';
            } else {
                noResultsMessage.style.display = 'none';
            }
        });

        // Add hover effects to company cards
        document.querySelectorAll('.company-card .card').forEach(card => {
            card.addEventListener('mouseenter', function() {
                this.style.transform = 'translateY(-5px)';
                this.style.boxShadow = '0 0.5rem 1rem rgba(0, 0, 0, 0.15)';
                this.style.transition = 'all 0.3s ease';
            });

            card.addEventListener('mouseleave', function() {
                this.style.transform = 'translateY(0)';
                this.style.boxShadow = '0 0.125rem 0.25rem rgba(0, 0, 0, 0.075)';
            });
        });

        // Loading states for buttons
        document.querySelectorAll('a[href*="company/"]').forEach(link => {
            link.addEventListener('click', function() {
                if (!this.classList.contains('btn-sm')) {
                    this.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Loading...';
                }
            });
        });

        // Highlight user's default company
        document.addEventListener('DOMContentLoaded', function() {
            const defaultCompanyCards = document.querySelectorAll('.company-card');
            defaultCompanyCards.forEach(card => {
                const quickBookBtn = card.querySelector('a[href*="book?companyId"]');
                const defaultText = card.querySelector('small:contains("Your Default Company")');

                if (defaultText) {
                    card.querySelector('.card').classList.add('border-success');
                }
            });
        });
    </script>
</body>
</html>
