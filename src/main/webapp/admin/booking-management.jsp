<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Booking Management - Admin Panel</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <style>
        .badge-pending {
            background-color: #ffc107;
            color: #212529;
        }
        .badge-approved {
            background-color: #28a745;
            color: white;
        }
        .badge-rejected {
            background-color: #dc3545;
            color: white;
        }
        .badge-vip {
            background-color: #6f42c1;
            color: white;
            font-weight: bold;
        }
        .booking-row:hover {
            background-color: #f8f9fa;
        }
        .table-responsive {
            border-radius: 0.375rem;
        }
        .dashboard-card {
            box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
            border: 1px solid rgba(0, 0, 0, 0.125);
        }
    </style>
</head>
<body>
    <!-- Admin Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/dashboard">
                <i class="fas fa-cogs"></i> Admin Panel - Booking Management
            </a>

            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">
                    <i class="fas fa-arrow-left"></i> Back to Dashboard
                </a>
                <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                    <i class="fas fa-sign-out-alt"></i> Logout
                </a>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container-fluid mt-4">

        <!-- Success/Error Messages -->
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i>${successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Page Header -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card dashboard-card">
                    <div class="card-body">
                        <div class="row align-items-center">
                            <div class="col-md-8">
                                <h2 class="mb-2">
                                    <i class="fas fa-calendar-check"></i> Flexible Duration Booking Management
                                </h2>
                                <p class="text-muted mb-0">
                                    Review, approve, and manage all cabin bookings with flexible duration support
                                </p>
                            </div>
                            <div class="col-md-4 text-end">
                                <div class="btn-group" role="group">
                                    <button type="button" class="btn btn-success" id="bulkApproveBtn" disabled>
                                        <i class="fas fa-check-double"></i> Bulk Approve
                                    </button>
                                    <button type="button" class="btn btn-danger" id="bulkRejectBtn" disabled>
                                        <i class="fas fa-times-circle"></i> Bulk Reject
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Filter Tabs and Statistics -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card dashboard-card">
                    <div class="card-header">
                        <ul class="nav nav-pills card-header-pills" role="tablist">
                            <li class="nav-item">
                                <a class="nav-link ${param.filter == 'pending' || empty param.filter ? 'active' : ''}"
                                   href="${pageContext.request.contextPath}/admin/bookings?filter=pending">
                                    <i class="fas fa-clock"></i> Pending
                                    <span class="badge bg-warning text-dark ms-1">${pendingCount > 0 ? pendingCount : '0'}</span>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link ${param.filter == 'vip' ? 'active' : ''}"
                                   href="${pageContext.request.contextPath}/admin/bookings?filter=vip">
                                    <i class="fas fa-star"></i> VIP Priority
                                    <span class="badge badge-vip ms-1">${vipCount > 0 ? vipCount : '0'}</span>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link ${param.filter == 'approved' ? 'active' : ''}"
                                   href="${pageContext.request.contextPath}/admin/bookings?filter=approved">
                                    <i class="fas fa-check"></i> Approved
                                    <span class="badge bg-success ms-1">${approvedCount > 0 ? approvedCount : '0'}</span>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link ${param.filter == 'rejected' ? 'active' : ''}"
                                   href="${pageContext.request.contextPath}/admin/bookings?filter=rejected">
                                    <i class="fas fa-times"></i> Rejected
                                    <span class="badge bg-danger ms-1">${rejectedCount > 0 ? rejectedCount : '0'}</span>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link ${param.filter == 'all' ? 'active' : ''}"
                                   href="${pageContext.request.contextPath}/admin/bookings?filter=all">
                                    <i class="fas fa-list"></i> All Bookings
                                    <span class="badge bg-primary ms-1">${totalCount > 0 ? totalCount : '0'}</span>
                                </a>
                            </li>
                        </ul>
                    </div>

                    <div class="card-body">
                        <!-- Search and Filter Controls -->
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <div class="input-group">
                                    <span class="input-group-text">
                                        <i class="fas fa-search"></i>
                                    </span>
                                    <input type="text" class="form-control" id="searchInput"
                                           placeholder="Search by user name, cabin name, or purpose...">
                                </div>
                            </div>
                            <div class="col-md-6 text-end">
                                <small class="text-muted">
                                    Showing <span id="visibleCount">${not empty bookings ? bookings.size() : '0'}</span> bookings
                                    <c:if test="${not empty param.filter && param.filter != 'all'}">
                                        (${param.filter} filter active)
                                    </c:if>
                                </small>
                            </div>
                        </div>

                        <!-- Bookings Table -->
                        <c:choose>
                            <c:when test="${empty bookings}">
                                <div class="text-center py-5">
                                    <i class="fas fa-calendar-times fa-3x text-muted mb-3"></i>
                                    <h5 class="text-muted">No bookings found</h5>
                                    <p class="text-muted">
                                        <c:choose>
                                            <c:when test="${param.filter == 'pending'}">
                                                No pending bookings require approval.
                                            </c:when>
                                            <c:when test="${param.filter == 'vip'}">
                                                No VIP priority bookings found.
                                            </c:when>
                                            <c:otherwise>
                                                Try adjusting your filters or search terms.
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                    <a href="${pageContext.request.contextPath}/admin/bookings" class="btn btn-primary">
                                        <i class="fas fa-refresh"></i> View All Bookings
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <form id="bulkActionForm" method="post">
                                    <div class="table-responsive">
                                        <table class="table table-hover">
                                            <thead class="table-dark">
                                                <tr>
                                                    <th width="50">
                                                        <c:if test="${param.filter == 'pending' || empty param.filter}">
                                                            <input type="checkbox" id="selectAll" class="form-check-input">
                                                        </c:if>
                                                    </th>
                                                    <th>User Details</th>
                                                    <th>Cabin & Purpose</th>
                                                    <th>Date & Time</th>
                                                    <th>Duration</th>
                                                    <th>Status</th>
                                                    <th>Priority</th>
                                                    <th>Created</th>
                                                    <th width="120">Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody id="bookingsTableBody">
                                                <c:forEach var="booking" items="${bookings}" varStatus="status">
                                                    <tr class="booking-row"
                                                        data-user-name="${booking.userName}"
                                                        data-cabin-name="${booking.cabinName}"
                                                        data-purpose="${booking.purpose}"
                                                        data-booking-id="${booking.bookingId}">
                                                        <td>
                                                            <c:if test="${booking.status == 'PENDING'}">
                                                                <input type="checkbox" name="bookingIds" value="${booking.bookingId}"
                                                                       class="form-check-input booking-checkbox">
                                                            </c:if>
                                                        </td>
                                                        <td>
                                                            <div class="d-flex align-items-center">
                                                                <i class="fas fa-user-circle fa-2x text-muted me-2"></i>
                                                                <div>
                                                                    <div class="fw-bold">${booking.userName}</div>
                                                                    <small class="text-muted">ID: ${booking.userId}</small>
                                                                    <c:if test="${booking.priorityLevel == 'VIP'}">
                                                                        <br><span class="badge badge-vip badge-sm">VIP User</span>
                                                                    </c:if>
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="fw-bold">${booking.cabinName}</div>
                                                            <small class="text-muted text-truncate d-block" style="max-width: 200px;" title="${booking.purpose}">
                                                                ${booking.purpose}
                                                            </small>
                                                        </td>
                                                        <td>
                                                            <div class="fw-bold">
                                                                <fmt:formatDate value="${booking.bookingDate}" pattern="MMM dd, yyyy"/>
                                                            </div>
                                                            <small class="text-muted">${booking.timeSlot}</small>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${not empty booking.durationMinutes}">
                                                                    <span class="badge bg-info">${booking.durationDisplay}</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <small class="text-muted">Standard</small>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${booking.status == 'PENDING'}">
                                                                    <span class="badge badge-pending">
                                                                        <i class="fas fa-clock me-1"></i>Pending
                                                                    </span>
                                                                </c:when>
                                                                <c:when test="${booking.status == 'APPROVED'}">
                                                                    <span class="badge badge-approved">
                                                                        <i class="fas fa-check me-1"></i>Approved
                                                                    </span>
                                                                </c:when>
                                                                <c:when test="${booking.status == 'REJECTED'}">
                                                                    <span class="badge badge-rejected">
                                                                        <i class="fas fa-times me-1"></i>Rejected
                                                                    </span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="badge bg-secondary">${booking.status}</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${booking.priorityLevel == 'VIP'}">
                                                                    <span class="badge badge-vip">
                                                                        <i class="fas fa-star me-1"></i>VIP
                                                                    </span>
                                                                </c:when>
                                                                <c:when test="${booking.priorityLevel == 'HIGH'}">
                                                                    <span class="badge bg-warning text-dark">
                                                                        <i class="fas fa-exclamation me-1"></i>High
                                                                    </span>
                                                                </c:when>
                                                                <c:when test="${booking.priorityLevel == 'EMERGENCY'}">
                                                                    <span class="badge bg-danger">
                                                                        <i class="fas fa-fire me-1"></i>Emergency
                                                                    </span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="badge bg-secondary">Normal</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <small class="text-muted">
                                                                <fmt:formatDate value="${booking.createdAt}" pattern="MMM dd, HH:mm"/>
                                                            </small>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${booking.status == 'PENDING'}">
                                                                    <div class="btn-group btn-group-sm" role="group">
                                                                        <button type="button" class="btn btn-success approve-single"
                                                                                data-booking-id="${booking.bookingId}"
                                                                                data-user-name="${booking.userName}"
                                                                                data-bs-toggle="tooltip" title="Approve Booking">
                                                                            <i class="fas fa-check"></i>
                                                                        </button>
                                                                        <button type="button" class="btn btn-danger reject-single"
                                                                                data-booking-id="${booking.bookingId}"
                                                                                data-user-name="${booking.userName}"
                                                                                data-bs-toggle="tooltip" title="Reject Booking">
                                                                            <i class="fas fa-times"></i>
                                                                        </button>
                                                                    </div>
                                                                </c:when>
                                                                <c:when test="${booking.status == 'APPROVED'}">
                                                                    <small class="text-success">
                                                                        <i class="fas fa-check-circle"></i>
                                                                        <c:if test="${booking.approvedAt != null}">
                                                                            <br><fmt:formatDate value="${booking.approvedAt}" pattern="MMM dd"/>
                                                                        </c:if>
                                                                    </small>
                                                                </c:when>
                                                                <c:when test="${booking.status == 'REJECTED'}">
                                                                    <small class="text-danger">
                                                                        <i class="fas fa-times-circle"></i>
                                                                        <c:if test="${booking.approvedAt != null}">
                                                                            <br><fmt:formatDate value="${booking.approvedAt}" pattern="MMM dd"/>
                                                                        </c:if>
                                                                    </small>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="text-muted">-</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </form>

                                <!-- Pagination (if needed) -->
                                <c:if test="${totalCount > 50}">
                                    <nav aria-label="Booking pagination" class="mt-3">
                                        <ul class="pagination justify-content-center">
                                            <!-- Add pagination logic here if needed -->
                                        </ul>
                                    </nav>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Confirmation Modals -->
    <!-- Single Action Modal -->
    <div class="modal fade" id="confirmModal" tabindex="-1" aria-labelledby="confirmModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="confirmModalTitle">Confirm Action</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body" id="confirmModalBody">
                    Are you sure you want to perform this action?
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary" id="confirmActionBtn">Confirm</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Bulk Action Modal -->
    <div class="modal fade" id="bulkConfirmModal" tabindex="-1" aria-labelledby="bulkModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="bulkModalTitle">Bulk Action Confirmation</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body" id="bulkModalBody">
                    <div class="alert alert-warning">
                        <i class="fas fa-exclamation-triangle"></i>
                        This action will affect multiple bookings and cannot be undone.
                    </div>
                    <p>Selected bookings: <span id="selectedCount" class="fw-bold">0</span></p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary" id="bulkConfirmBtn">Proceed</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        // ✅ CRITICAL FIX: Declare context path as a JavaScript variable
        const contextPath = '${pageContext.request.contextPath}';

        // ✅ ENHANCED: Search functionality with live count update
        document.getElementById('searchInput').addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            const rows = document.querySelectorAll('.booking-row');
            let visibleCount = 0;

            rows.forEach(row => {
                const userName = (row.dataset.userName || '').toLowerCase();
                const cabinName = (row.dataset.cabinName || '').toLowerCase();
                const purpose = (row.dataset.purpose || '').toLowerCase();

                if (userName.includes(searchTerm) || cabinName.includes(searchTerm) || purpose.includes(searchTerm)) {
                    row.style.display = '';
                    visibleCount++;
                } else {
                    row.style.display = 'none';
                }
            });

            document.getElementById('visibleCount').textContent = visibleCount;
        });

        // Select all functionality
        const selectAllCheckbox = document.getElementById('selectAll');
        const bookingCheckboxes = document.querySelectorAll('.booking-checkbox');
        const bulkApproveBtn = document.getElementById('bulkApproveBtn');
        const bulkRejectBtn = document.getElementById('bulkRejectBtn');

        if (selectAllCheckbox) {
            selectAllCheckbox.addEventListener('change', function() {
                bookingCheckboxes.forEach(checkbox => {
                    if (checkbox.closest('.booking-row').style.display !== 'none') {
                        checkbox.checked = this.checked;
                    }
                });
                updateBulkButtons();
            });
        }

        bookingCheckboxes.forEach(checkbox => {
            checkbox.addEventListener('change', updateBulkButtons);
        });

        function updateBulkButtons() {
            const checkedBoxes = document.querySelectorAll('.booking-checkbox:checked');
            const visibleCheckedBoxes = Array.from(checkedBoxes).filter(cb =>
                cb.closest('.booking-row').style.display !== 'none'
            );
            const hasSelection = visibleCheckedBoxes.length > 0;

            if (bulkApproveBtn) bulkApproveBtn.disabled = !hasSelection;
            if (bulkRejectBtn) bulkRejectBtn.disabled = !hasSelection;

            if (selectAllCheckbox) {
                const visibleCheckboxes = Array.from(bookingCheckboxes).filter(cb =>
                    cb.closest('.booking-row').style.display !== 'none'
                );
                selectAllCheckbox.indeterminate = visibleCheckedBoxes.length > 0 && visibleCheckedBoxes.length < visibleCheckboxes.length;
                selectAllCheckbox.checked = visibleCheckedBoxes.length === visibleCheckboxes.length && visibleCheckboxes.length > 0;
            }
        }

        // ✅ CRITICAL FIX: Use string concatenation instead of template literals
        document.querySelectorAll('.approve-single').forEach(button => {
            button.addEventListener('click', function() {
                const bookingId = this.dataset.bookingId;
                const userName = this.dataset.userName;

                console.log('Approve button clicked - Booking ID:', bookingId); // Debug log

                showConfirmModal(
                    'Approve Booking',
                    'Are you sure you want to approve the booking for ' + userName + '?',
                    'approve',
                    function() {
                        // ✅ FIXED: Use string concatenation instead of template literals
                        window.location.href = contextPath + '/admin/approve-booking?bookingId=' + bookingId;
                    }
                );
            });
        });

        // ✅ CRITICAL FIX: Fixed reject button with proper string concatenation
        document.querySelectorAll('.reject-single').forEach(button => {
            button.addEventListener('click', function() {
                const bookingId = this.dataset.bookingId;
                const userName = this.dataset.userName;

                console.log('Reject button clicked - Booking ID:', bookingId); // Debug log

                if (!bookingId) {
                    console.error('❌ No booking ID found!');
                    alert('Error: Booking ID not found. Please refresh the page and try again.');
                    return;
                }

                showConfirmModal(
                    'Reject Booking',
                    'Are you sure you want to reject the booking for ' + userName + '?',
                    'reject',
                    function() {
                        // ✅ FIXED: Use string concatenation instead of template literals
                        console.log('Redirecting to reject URL with booking ID:', bookingId);
                        window.location.href = contextPath + '/admin/reject-booking?bookingId=' + bookingId;
                    }
                );
            });
        });

        // Bulk actions
        if (bulkApproveBtn) {
            bulkApproveBtn.addEventListener('click', function() {
                const checkedBoxes = document.querySelectorAll('.booking-checkbox:checked');
                const visibleCheckedBoxes = Array.from(checkedBoxes).filter(cb =>
                    cb.closest('.booking-row').style.display !== 'none'
                );
                showBulkConfirmModal('Bulk Approve', visibleCheckedBoxes.length, 'approve');
            });
        }

        if (bulkRejectBtn) {
            bulkRejectBtn.addEventListener('click', function() {
                const checkedBoxes = document.querySelectorAll('.booking-checkbox:checked');
                const visibleCheckedBoxes = Array.from(checkedBoxes).filter(cb =>
                    cb.closest('.booking-row').style.display !== 'none'
                );
                showBulkConfirmModal('Bulk Reject', visibleCheckedBoxes.length, 'reject');
            });
        }

        // ✅ FIXED: Modal functions with proper string concatenation
        function showConfirmModal(title, message, action, callback) {
            document.getElementById('confirmModalTitle').textContent = title;
            document.getElementById('confirmModalBody').textContent = message;

            const confirmBtn = document.getElementById('confirmActionBtn');
            confirmBtn.className = 'btn btn-' + (action == 'approve' ? 'success' : 'danger');
            confirmBtn.textContent = action == 'approve' ? 'Approve' : 'Reject';

            confirmBtn.onclick = function() {
                const modal = bootstrap.Modal.getInstance(document.getElementById('confirmModal'));
                if (modal) {
                    modal.hide();
                }
                if (callback) {
                    callback();
                }
            };

            new bootstrap.Modal(document.getElementById('confirmModal')).show();
        }

        function showBulkConfirmModal(title, count, type) {
            document.getElementById('bulkModalTitle').textContent = title;
            document.getElementById('selectedCount').textContent = count;

            const confirmBtn = document.getElementById('bulkConfirmBtn');
            confirmBtn.className = 'btn btn-' + (type == 'approve' ? 'success' : 'danger');
            confirmBtn.textContent = type == 'approve' ? 'Approve All Selected' : 'Reject All Selected';

            confirmBtn.onclick = function() {
                const form = document.getElementById('bulkActionForm');
                // ✅ FIXED: Use string concatenation
                form.action = contextPath + '/admin/bulk-' + type;
                form.submit();
            };

            new bootstrap.Modal(document.getElementById('bulkConfirmModal')).show();
        }

        // Initialize tooltips
        const tooltips = document.querySelectorAll('[data-bs-toggle="tooltip"]');
        tooltips.forEach(tooltip => {
            new bootstrap.Tooltip(tooltip);
        });

        // Auto-refresh pending count every 30 seconds
        setInterval(function() {
            if (window.location.search.includes('filter=pending') || window.location.search === '') {
                console.log('Checking for new pending bookings...');
                // You can add AJAX call here to refresh counts
            }
        }, 30000);

        // Initialize page
        document.addEventListener('DOMContentLoaded', function() {
            console.log('Admin booking management page initialized');
            console.log('Context path:', contextPath);
            updateBulkButtons();

            // ✅ DEBUG: Check if booking IDs are properly set
            const rejectButtons = document.querySelectorAll('.reject-single');
            console.log('Found reject buttons:', rejectButtons.length);
            rejectButtons.forEach((btn, index) => {
                console.log('Button ' + index + ' booking ID:', btn.dataset.bookingId);
            });
        });
    </script>
</body>
</html>
