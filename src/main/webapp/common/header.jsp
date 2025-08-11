<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Reusable Header Component for All Pages -->
<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">
            <i class="fas fa-building"></i> Cabin Booking System
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
            <!-- Check if user is logged in -->
            <c:if test="${not empty sessionScope.user}">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link ${pageContext.request.requestURI.contains('dashboard') ? 'active' : ''}"
                           href="${pageContext.request.contextPath}/dashboard">
                            <i class="fas fa-tachometer-alt"></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${pageContext.request.requestURI.contains('book') ? 'active' : ''}"
                           href="${pageContext.request.contextPath}/book">
                            <i class="fas fa-plus-circle"></i> New Booking
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${pageContext.request.requestURI.contains('mybookings') ? 'active' : ''}"
                           href="${pageContext.request.contextPath}/mybookings">
                            <i class="fas fa-calendar-check"></i> My Bookings
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${pageContext.request.requestURI.contains('company') ? 'active' : ''}"
                           href="${pageContext.request.contextPath}/company/browse">
                            <i class="fas fa-search"></i> Browse Companies
                        </a>
                    </li>
                </ul>

                <ul class="navbar-nav">
                    <!-- User Profile Dropdown -->
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button"
                           data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="fas fa-user-circle"></i> ${sessionScope.user.name}

                            <!-- User Type Badges -->
                            <c:if test="${sessionScope.user.userType == 'VIP'}">
                                <span class="badge badge-vip ms-1">VIP</span>
                            </c:if>
                            <c:if test="${sessionScope.user.admin}">
                                <span class="badge bg-danger ms-1">ADMIN</span>
                            </c:if>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                            <li>
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/profile">
                                    <i class="fas fa-user-edit"></i> My Profile
                                </a>
                            </li>
                            <li>
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/mybookings">
                                    <i class="fas fa-calendar-check"></i> My Bookings
                                </a>
                            </li>
                            <li><hr class="dropdown-divider"></li>

                            <!-- Admin Panel Access -->
                            <c:if test="${sessionScope.user.admin}">
                                <li>
                                    <a class="dropdown-item text-warning" href="${pageContext.request.contextPath}/admin/dashboard">
                                        <i class="fas fa-cogs"></i> Admin Panel
                                    </a>
                                </li>
                                <li><hr class="dropdown-divider"></li>
                            </c:if>

                            <li>
                                <a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout">
                                    <i class="fas fa-sign-out-alt"></i> Logout
                                </a>
                            </li>
                        </ul>
                    </li>
                </ul>
            </c:if>

            <!-- Login/Register Links for Non-logged Users -->
            <c:if test="${empty sessionScope.user}">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/login">
                            <i class="fas fa-sign-in-alt"></i> Login
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/register">
                            <i class="fas fa-user-plus"></i> Register
                        </a>
                    </li>
                </ul>
            </c:if>
        </div>
    </div>
</nav>

<!-- Breadcrumb Navigation (Optional) -->
<c:if test="${not empty sessionScope.user && not pageContext.request.requestURI.contains('dashboard')}">
    <div class="bg-light border-bottom">
        <div class="container">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-0 py-2">
                    <li class="breadcrumb-item">
                        <a href="${pageContext.request.contextPath}/dashboard" class="text-decoration-none">
                            <i class="fas fa-home"></i> Dashboard
                        </a>
                    </li>
                    <c:choose>
                        <c:when test="${pageContext.request.requestURI.contains('book')}">
                            <li class="breadcrumb-item active">New Booking</li>
                        </c:when>
                        <c:when test="${pageContext.request.requestURI.contains('mybookings')}">
                            <li class="breadcrumb-item active">My Bookings</li>
                        </c:when>
                        <c:when test="${pageContext.request.requestURI.contains('profile')}">
                            <li class="breadcrumb-item active">Profile</li>
                        </c:when>
                        <c:when test="${pageContext.request.requestURI.contains('company')}">
                            <li class="breadcrumb-item active">Browse Companies</li>
                        </c:when>
                        <c:when test="${pageContext.request.requestURI.contains('admin')}">
                            <li class="breadcrumb-item active">Admin Panel</li>
                        </c:when>
                        <c:otherwise>
                            <li class="breadcrumb-item active">Current Page</li>
                        </c:otherwise>
                    </c:choose>
                </ol>
            </nav>
        </div>
    </div>
</c:if>
