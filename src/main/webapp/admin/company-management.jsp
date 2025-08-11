<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Company Management - Admin Panel</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
    <!-- Admin Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/dashboard">
                <i class="fas fa-cogs"></i> Admin Panel
            </a>
            <div class="navbar-nav ms-auto">
                <span class="navbar-text text-light me-3">
                    <i class="fas fa-user-shield"></i> ${admin.name}
                </span>
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">
                    <i class="fas fa-tachometer-alt"></i> Dashboard
                </a>
                <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                    <i class="fas fa-sign-out-alt"></i> Logout
                </a>
            </div>
        </div>
    </nav>

    <div class="container-fluid mt-4">
        <!-- Page Header -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h2 class="mb-1">
                            <i class="fas fa-building text-primary"></i> Company Management
                        </h2>
                        <p class="text-muted mb-0">Manage all companies in the cabin booking system</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Success/Error Messages -->
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle"></i> ${sessionScope.successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="successMessage" scope="session"/>
        </c:if>

        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle"></i> ${sessionScope.errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>

        <!-- Statistics Cards -->
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="card bg-primary text-white">
                    <div class="card-body text-center">
                        <i class="fas fa-building fa-2x mb-2"></i>
                        <h3>${totalCompanies}</h3>
                        <p class="mb-0">Total Companies</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card bg-success text-white">
                    <div class="card-body text-center">
                        <i class="fas fa-check-circle fa-2x mb-2"></i>
                        <h3>${allCompanies.size()}</h3>
                        <p class="mb-0">Active Companies</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card bg-warning text-white">
                    <div class="card-body text-center">
                        <i class="fas fa-home fa-2x mb-2"></i>
                        <h3>
                            <c:set var="totalCabins" value="0"/>
                            <c:forEach var="company" items="${allCompanies}">
                                <c:set var="cabinCount" value="${companyCabinCounts[company.companyId]}"/>
                                <c:set var="totalCabins" value="${totalCabins + (cabinCount != null ? cabinCount : 0)}"/>
                            </c:forEach>
                            ${totalCabins}
                        </h3>
                        <p class="mb-0">Total Cabins</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card bg-info text-white">
                    <div class="card-body text-center">
                        <i class="fas fa-crown fa-2x mb-2"></i>
                        <h3>${vipCompanies.size()}</h3>
                        <p class="mb-0">VIP Companies</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Companies List -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">
                    <i class="fas fa-list"></i> Company Directory
                    <span class="badge bg-primary ms-2">${totalCompanies} companies</span>
                </h5>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty allCompanies}">
                        <div class="text-center py-5">
                            <i class="fas fa-building fa-3x text-muted mb-3"></i>
                            <h4 class="text-muted">No Companies Found</h4>
                            <p class="text-muted">There are no companies registered in the system.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead class="table-dark">
                                    <tr>
                                        <th>Company Details</th>
                                        <th>Location</th>
                                        <th>Cabins</th>
                                        <th>Status</th>
                                        <th>Created</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="company" items="${allCompanies}">
                                        <tr>
                                            <td>
                                                <div class="d-flex align-items-center">
                                                    <div class="bg-light rounded p-2 me-3">
                                                        <i class="fas fa-building text-primary fa-lg"></i>
                                                    </div>
                                                    <div>
                                                        <strong>${company.name}</strong><br>
                                                        <small class="text-muted">ID: ${company.companyId}</small>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>
                                                <i class="fas fa-map-marker-alt text-muted me-1"></i>
                                                ${not empty company.location ? company.location : 'Not specified'}
                                            </td>
                                            <td>
                                                <span class="badge bg-info">
                                                    ${companyCabinCounts[company.companyId] != null ? companyCabinCounts[company.companyId] : 0} cabins
                                                </span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${company.status == 'ACTIVE'}">
                                                        <span class="badge bg-success">Active</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">Inactive</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty company.createdAt}">
                                                        <fmt:formatDate value="${company.createdAt}" pattern="MMM dd, yyyy"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <small class="text-muted">N/A</small>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="btn-group btn-group-sm">
                                                    <button class="btn btn-outline-primary" title="View Details">
                                                        <i class="fas fa-eye"></i>
                                                    </button>
                                                    <button class="btn btn-outline-warning" title="Edit">
                                                        <i class="fas fa-edit"></i>
                                                    </button>
                                                    <button class="btn btn-outline-danger" title="Delete">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Quick Actions -->
        <div class="row mt-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0"><i class="fas fa-bolt"></i> Quick Actions</h5>
                    </div>
                    <div class="card-body">
                        <div class="row text-center">
                            <div class="col-md-3 mb-3">
                                <a href="${pageContext.request.contextPath}/admin/manage-cabins" class="btn btn-outline-primary btn-lg w-100">
                                    <i class="fas fa-home d-block fa-2x mb-2"></i>
                                    Manage Cabins
                                </a>
                            </div>
                            <div class="col-md-3 mb-3">
                                <a href="${pageContext.request.contextPath}/admin/bookings" class="btn btn-outline-success btn-lg w-100">
                                    <i class="fas fa-calendar-check d-block fa-2x mb-2"></i>
                                    View Bookings
                                </a>
                            </div>
                            <div class="col-md-3 mb-3">
                                <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-info btn-lg w-100">
                                    <i class="fas fa-users d-block fa-2x mb-2"></i>
                                    Manage Users
                                </a>
                            </div>
                            <div class="col-md-3 mb-3">
                                <a href="${pageContext.request.contextPath}/admin/analytics" class="btn btn-outline-warning btn-lg w-100">
                                    <i class="fas fa-chart-bar d-block fa-2x mb-2"></i>
                                    View Analytics
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="bg-dark text-light text-center py-3 mt-5">
        <div class="container">
            <p class="mb-0">&copy; 2024 Cabin Booking System - Admin Panel</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        console.log('Company management page loaded successfully');
    </script>
</body>
</html>
