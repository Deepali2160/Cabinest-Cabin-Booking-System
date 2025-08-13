// ====================================
// ADMIN ANALYTICS - MINIMAL FUNCTIONALITY - ENHANCED VERSION
// ====================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('📈 Admin Analytics Dashboard initialized for Yash Technology');

    try {
        // Validate data first
        validateAnalyticsData();

        // Initialize components with delay to ensure proper loading
        setTimeout(() => {
            initializeCharts();
            initializeAnimations();
            initializeExportModal();
            initializeDateRange();
            initializeDropdowns();
            initializeAutoRefresh();
        }, 100);

        console.log('📊 Analytics data loaded:', window.analyticsData);

        // Perform health check after initialization
        setTimeout(() => {
            const healthCheck = performHealthCheck();
            if (!healthCheck.chartsLoaded) {
                console.warn('⚠️ Some charts failed to load');
                // Attempt recovery
                setTimeout(() => {
                    recoverFromError();
                }, 1000);
            }
        }, 2000);

    } catch (error) {
        console.error('❌ Error initializing analytics dashboard:', error);
        showNotification('Failed to initialize dashboard', 'error');
    }
});

// Chart instances - Global tracking
let bookingStatusChart = null;
let userTypeChart = null;
let timeSlotsChart = null;

// ✅ Enhanced data validation
function validateAnalyticsData() {
    if (!window.analyticsData) {
        console.warn('⚠️ Analytics data not found, using fallback data');
        window.analyticsData = {
            bookingStatus: { approved: 15, pending: 5, rejected: 2 },
            userTypes: { normal: 20, vip: 5, admin: 2 },
            timeSlots: ['9:00 AM', '10:00 AM', '11:00 AM', '2:00 PM', '3:00 PM'],
            contextPath: ''
        };
    }

    // Validate bookingStatus
    if (!window.analyticsData.bookingStatus) {
        window.analyticsData.bookingStatus = { approved: 0, pending: 0, rejected: 0 };
    }

    // Validate userTypes
    if (!window.analyticsData.userTypes) {
        window.analyticsData.userTypes = { normal: 0, vip: 0, admin: 0 };
    }

    // Ensure no negative values
    Object.keys(window.analyticsData.bookingStatus).forEach(key => {
        if (window.analyticsData.bookingStatus[key] < 0) {
            window.analyticsData.bookingStatus[key] = 0;
        }
    });

    Object.keys(window.analyticsData.userTypes).forEach(key => {
        if (window.analyticsData.userTypes[key] < 0) {
            window.analyticsData.userTypes[key] = 0;
        }
    });

    console.log('✅ Analytics data validated:', window.analyticsData);
}

// ✅ CRITICAL FIX: Enhanced chart cleanup function
function destroyAllCharts() {
    console.log('🧹 Destroying all existing charts...');

    if (bookingStatusChart) {
        try {
            bookingStatusChart.destroy();
            console.log('✅ Booking status chart destroyed');
        } catch (e) {
            console.warn('⚠️ Error destroying booking status chart:', e);
        }
        bookingStatusChart = null;
    }

    if (userTypeChart) {
        try {
            userTypeChart.destroy();
            console.log('✅ User type chart destroyed');
        } catch (e) {
            console.warn('⚠️ Error destroying user type chart:', e);
        }
        userTypeChart = null;
    }

    if (timeSlotsChart) {
        try {
            timeSlotsChart.destroy();
            console.log('✅ Time slots chart destroyed');
        } catch (e) {
            console.warn('⚠️ Error destroying time slots chart:', e);
        }
        timeSlotsChart = null;
    }
}

// ✅ FIXED: Initialize all charts with proper cleanup
function initializeCharts() {
    try {
        console.log('📊 Initializing all charts...');

        // ✅ CRITICAL: Destroy all existing charts first
        destroyAllCharts();

        // Small delay to ensure cleanup is complete
        setTimeout(() => {
            createBookingStatusChart();
            createUserTypeChart();
            createTimeSlotChart();

            console.log('✅ All charts initialized successfully');
        }, 100);

    } catch (error) {
        console.error('❌ Error initializing charts:', error);
        showNotification('Failed to load charts', 'error');
    }
}

// ✅ UPDATED: Enhanced booking status chart with Chart.getChart() method
function createBookingStatusChart() {
    try {
        const ctx = document.getElementById('bookingStatusChart');
        if (!ctx) {
            console.error('❌ Booking status chart canvas not found');
            return;
        }

        const data = window.analyticsData.bookingStatus;

        // Check if all values are zero
        const total = data.approved + data.pending + data.rejected;
        if (total === 0) {
            console.warn('⚠️ All booking data is zero, creating empty chart');
            showEmptyChart(ctx, 'No booking data available');
            return;
        }

        // ✅ BETTER FIX: Use Chart.getChart() to check for existing chart
        const existingChart = Chart.getChart(ctx);
        if (existingChart) {
            console.log('🧹 Destroying existing booking status chart...');
            existingChart.destroy();
        }

        // Reset chart variable
        bookingStatusChart = null;

        console.log('📊 Creating booking status chart with data:', data);

        bookingStatusChart = new Chart(ctx.getContext('2d'), {
            type: 'doughnut',
            data: {
                labels: ['✅ Approved', '⏰ Pending', '❌ Rejected'],
                datasets: [{
                    data: [data.approved, data.pending, data.rejected],
                    backgroundColor: ['#27ae60', '#f39c12', '#e74c3c'],
                    borderColor: ['#2ecc71', '#e67e22', '#c0392b'],
                    borderWidth: 2,
                    hoverOffset: 10
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            padding: 20,
                            usePointStyle: true,
                            font: { size: 12, weight: 500 }
                        }
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        titleColor: 'white',
                        bodyColor: 'white',
                        cornerRadius: 8,
                        displayColors: false,
                        callbacks: {
                            label: function(context) {
                                const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                const percentage = ((context.parsed * 100) / total).toFixed(1);
                                return `${context.label}: ${context.parsed} (${percentage}%)`;
                            }
                        }
                    }
                },
                animation: {
                    animateRotate: true,
                    duration: 1500
                }
            }
        });

        updateLegend('statusLegend', bookingStatusChart);
        console.log('✅ Booking status chart created successfully');

    } catch (error) {
        console.error('❌ Error creating booking status chart:', error);
        showChartError('bookingStatusChart', 'Failed to load booking status chart');
    }
}

// ✅ UPDATED: Enhanced user type chart with Chart.getChart() method
function createUserTypeChart() {
    try {
        const ctx = document.getElementById('userTypeChart');
        if (!ctx) {
            console.error('❌ User type chart canvas not found');
            return;
        }

        const data = window.analyticsData.userTypes;

        // Check if all values are zero
        const total = data.normal + data.vip + data.admin;
        if (total === 0) {
            console.warn('⚠️ All user data is zero, creating empty chart');
            showEmptyChart(ctx, 'No user data available');
            return;
        }

        // ✅ BETTER FIX: Use Chart.getChart() to check for existing chart
        const existingChart = Chart.getChart(ctx);
        if (existingChart) {
            console.log('🧹 Destroying existing user type chart...');
            existingChart.destroy();
        }

        // Reset chart variable
        userTypeChart = null;

        console.log('👥 Creating user type chart with data:', data);

        userTypeChart = new Chart(ctx.getContext('2d'), {
            type: 'pie',
            data: {
                labels: ['👤 Normal', '⭐ VIP', '👨‍💼 Admin'],
                datasets: [{
                    data: [data.normal, data.vip, data.admin],
                    backgroundColor: ['#3498db', '#f39c12', '#e74c3c'],
                    borderColor: ['#2980b9', '#e67e22', '#c0392b'],
                    borderWidth: 2,
                    hoverOffset: 8
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            padding: 20,
                            usePointStyle: true,
                            font: { size: 12, weight: 500 }
                        }
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        titleColor: 'white',
                        bodyColor: 'white',
                        cornerRadius: 8,
                        displayColors: false,
                        callbacks: {
                            label: function(context) {
                                const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                const percentage = ((context.parsed * 100) / total).toFixed(1);
                                return `${context.label}: ${context.parsed} users (${percentage}%)`;
                            }
                        }
                    }
                },
                animation: {
                    animateRotate: true,
                    duration: 1500
                }
            }
        });

        updateLegend('userTypeLegend', userTypeChart);
        console.log('✅ User type chart created successfully');

    } catch (error) {
        console.error('❌ Error creating user type chart:', error);
        showChartError('userTypeChart', 'Failed to load user type chart');
    }
}

// ✅ FIXED: Enhanced time slots chart with proper cleanup
function createTimeSlotChart() {
    try {
        const ctx = document.getElementById('popularTimeSlotsChart');
        if (!ctx) {
            console.error('❌ Time slots chart canvas not found');
            return;
        }

        // ✅ BETTER FIX: Use Chart.getChart() to check for existing chart
        const existingChart = Chart.getChart(ctx);
        if (existingChart) {
            console.log('🧹 Destroying existing time slots chart...');
            existingChart.destroy();
        }

        // Reset chart variable
        timeSlotsChart = null;

        // Use data from analyticsData or fallback to sample data
        const timeSlots = window.analyticsData.timeSlots && window.analyticsData.timeSlots.length > 0
            ? window.analyticsData.timeSlots
            : ['9:00 AM', '10:00 AM', '11:00 AM', '12:00 PM', '1:00 PM', '2:00 PM', '3:00 PM', '4:00 PM'];

        // Generate sample booking counts
        const bookingCounts = timeSlots.map(() => Math.floor(Math.random() * 30) + 10);

        console.log('🕐 Creating time slots chart with slots:', timeSlots);

        timeSlotsChart = new Chart(ctx.getContext('2d'), {
            type: 'bar',
            data: {
                labels: timeSlots,
                datasets: [{
                    label: 'Bookings',
                    data: bookingCounts,
                    backgroundColor: 'rgba(52, 152, 219, 0.8)',
                    borderColor: 'rgba(52, 152, 219, 1)',
                    borderWidth: 2,
                    borderRadius: 8,
                    borderSkipped: false,
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        titleColor: 'white',
                        bodyColor: 'white',
                        cornerRadius: 8,
                        displayColors: false,
                        callbacks: {
                            title: function(context) {
                                return `Time: ${context[0].label}`;
                            },
                            label: function(context) {
                                return `Bookings: ${context.parsed.y}`;
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        grid: { display: false },
                        ticks: { font: { size: 11, weight: 500 } }
                    },
                    y: {
                        beginAtZero: true,
                        grid: { color: 'rgba(0, 0, 0, 0.1)' },
                        ticks: { font: { size: 11, weight: 500 } }
                    }
                },
                animation: {
                    duration: 1500,
                    easing: 'easeInOutQuart'
                }
            }
        });

        console.log('✅ Time slots chart created successfully');

    } catch (error) {
        console.error('❌ Error creating time slots chart:', error);
        showChartError('popularTimeSlotsChart', 'Failed to load time slots chart');
    }
}

// ✅ CRITICAL FIX: Show empty chart with proper canvas cleanup
function showEmptyChart(canvasElement, message) {
    try {
        const canvasId = canvasElement.id;
        console.log(`🔄 Creating empty chart for canvas: ${canvasId}`);

        // ✅ CRITICAL FIX: Check for existing chart and destroy it
        const existingChart = Chart.getChart(canvasElement);
        if (existingChart) {
            console.log(`🧹 Destroying existing chart on ${canvasId}`);
            existingChart.destroy();
        }

        const ctx = canvasElement.getContext('2d');

        const chart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['No Data'],
                datasets: [{
                    data: [1],
                    backgroundColor: ['#ecf0f1'],
                    borderColor: ['#bdc3c7'],
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: { enabled: false }
                }
            },
            plugins: [{
                afterDraw: function(chart) {
                    const ctx = chart.ctx;
                    const width = chart.width;
                    const height = chart.height;

                    ctx.save();
                    ctx.textAlign = 'center';
                    ctx.textBaseline = 'middle';
                    ctx.fillStyle = '#7f8c8d';
                    ctx.font = '16px Arial';
                    ctx.fillText(message, width / 2, height / 2);
                    ctx.restore();
                }
            }]
        });

        console.log(`✅ Empty chart created successfully for ${canvasId}`);
        return chart;

    } catch (error) {
        console.error('❌ Error showing empty chart:', error);

        // Fallback: Show error message instead
        const container = canvasElement.parentElement;
        if (container) {
            container.innerHTML = `
                <div style="
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    justify-content: center;
                    height: 300px;
                    color: #7f8c8d;
                    text-align: center;
                ">
                    <div style="font-size: 48px; margin-bottom: 15px;">📊</div>
                    <div style="font-size: 16px;">${message}</div>
                </div>
            `;
        }
    }
}

// ✅ Show chart error
function showChartError(canvasId, errorMessage) {
    const canvas = document.getElementById(canvasId);
    if (canvas) {
        const container = canvas.parentElement;
        container.innerHTML = `
            <div class="chart-error" style="
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                height: 300px;
                color: #7f8c8d;
                text-align: center;
            ">
                <div class="error-icon" style="font-size: 48px; margin-bottom: 15px;">⚠️</div>
                <div class="error-message" style="font-size: 16px; margin-bottom: 20px;">${errorMessage}</div>
                <button class="retry-btn" onclick="initializeCharts()" style="
                    background: #3498db;
                    color: white;
                    border: none;
                    padding: 10px 20px;
                    border-radius: 6px;
                    cursor: pointer;
                    font-size: 14px;
                ">🔄 Retry</button>
            </div>
        `;
    }
}

// Update chart legend
function updateLegend(legendId, chart) {
    const legendContainer = document.getElementById(legendId);
    if (!legendContainer || !chart) return;

    const legendItems = chart.data.labels.map((label, index) => {
        const color = chart.data.datasets[0].backgroundColor[index];
        return `
            <div class="legend-item">
                <div class="legend-color" style="background-color: ${color}"></div>
                <span>${label}</span>
            </div>
        `;
    }).join('');

    legendContainer.innerHTML = legendItems;
}

// ✅ Enhanced animation counter with validation
function animateCounter(element, start, end, duration, suffix = '') {
    if (!element || isNaN(start) || isNaN(end) || end < 0) {
        console.warn('⚠️ Invalid parameters for counter animation');
        return;
    }

    const range = end - start;
    const increment = range / (duration / 50);
    let current = start;

    const timer = setInterval(() => {
        current += increment;
        if (current >= end) {
            current = end;
            clearInterval(timer);
        }
        element.textContent = Math.floor(current) + suffix;
    }, 50);
}

// Initialize count-up animations
function initializeAnimations() {
    try {
        const metricNumbers = document.querySelectorAll('.metric-number');

        // Animate metric numbers
        metricNumbers.forEach(element => {
            const target = parseInt(element.dataset.target) || parseInt(element.textContent.replace(/[^0-9]/g, ''));
            const suffix = element.textContent.includes('%') ? '%' : '';

            animateCounter(element, 0, target, 2000, suffix);
        });

        // Animate activity stats
        const activityStats = document.querySelectorAll('.activity-stat .stat-number');
        activityStats.forEach(element => {
            const target = parseInt(element.textContent);
            animateCounter(element, 0, target, 1500);
        });
    } catch (error) {
        console.error('❌ Error initializing animations:', error);
    }
}

// Initialize export modal
function initializeExportModal() {
    const exportBtn = document.getElementById('exportMetrics');
    const printBtn = document.getElementById('printReport');
    const exportModal = document.getElementById('exportModal');
    const exportModalClose = document.getElementById('exportModalClose');
    const cancelExport = document.getElementById('cancelExport');
    const confirmExport = document.getElementById('confirmExport');

    // Show export modal
    if (exportBtn) {
        exportBtn.addEventListener('click', function() {
            showExportModal();
        });
    }

    // Print report
    if (printBtn) {
        printBtn.addEventListener('click', function() {
            printReport();
        });
    }

    // Close export modal
    [exportModalClose, cancelExport].forEach(btn => {
        if (btn) {
            btn.addEventListener('click', function() {
                hideExportModal();
            });
        }
    });

    // Confirm export
    if (confirmExport) {
        confirmExport.addEventListener('click', function() {
            processExport();
        });
    }

    // Close modal on overlay click
    if (exportModal) {
        exportModal.addEventListener('click', function(e) {
            if (e.target === exportModal) {
                hideExportModal();
            }
        });
    }
}

// Show export modal
function showExportModal() {
    const modal = document.getElementById('exportModal');
    if (modal) {
        modal.classList.add('show');
        document.body.style.overflow = 'hidden';
        console.log('📊 Export modal shown');
    }
}

// Hide export modal
function hideExportModal() {
    const modal = document.getElementById('exportModal');
    if (modal) {
        modal.classList.remove('show');
        document.body.style.overflow = '';
        console.log('❌ Export modal hidden');
    }
}

// Process export
function processExport() {
    const formatElement = document.querySelector('input[name="format"]:checked');
    const rangeElement = document.querySelector('.range-select');

    const format = formatElement ? formatElement.value : 'pdf';
    const range = rangeElement ? rangeElement.value : 'month';
    const sections = Array.from(document.querySelectorAll('.section-option input:checked')).map(cb => cb.parentElement.textContent.trim());

    console.log('📊 Processing export:', { format, range, sections });

    // Show loading state
    const confirmBtn = document.getElementById('confirmExport');
    if (confirmBtn) {
        confirmBtn.innerHTML = '⏳ Generating...';
        confirmBtn.disabled = true;

        // Simulate export process
        setTimeout(() => {
            hideExportModal();
            showNotification('📊 Analytics report exported successfully!', 'success');

            // Reset button
            confirmBtn.innerHTML = '📊 Generate Report';
            confirmBtn.disabled = false;

            // In real app, this would trigger actual export
            downloadReport(format, range);
        }, 2000);
    }
}

// Download report (simulation)
function downloadReport(format, range) {
    const filename = `yash-technology-analytics-${range}.${format}`;
    console.log('📥 Downloading report:', filename);

    // In real implementation, this would generate and download the actual file
    showNotification(`📥 Report "${filename}" downloaded`, 'info');
}

// Print report
function printReport() {
    console.log('🖨️ Printing analytics report');

    // Hide non-printable elements
    const nonPrintable = document.querySelectorAll('.nav-menu, .header-actions, .modal-overlay');
    nonPrintable.forEach(el => el.style.display = 'none');

    // Print
    window.print();

    // Restore elements
    setTimeout(() => {
        nonPrintable.forEach(el => el.style.display = '');
    }, 1000);
}

// ✅ Debounce utility function
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Update data for selected date range
function updateDataForRange(range) {
    console.log('📅 Updating data for range:', range);

    // Show loading state
    showNotification('🔄 Updating analytics data...', 'info');

    // Simulate data update
    setTimeout(() => {
        // In real app, this would fetch new data from server
        refreshCharts();
        updateMetrics();
        showNotification('✅ Data updated successfully!', 'success');
    }, 1500);
}

// ✅ Apply debounce to date range updates
const debouncedUpdateDataForRange = debounce(updateDataForRange, 300);

// Initialize date range selector
function initializeDateRange() {
    const dateRangeSelect = document.getElementById('dateRange');

    if (dateRangeSelect) {
        dateRangeSelect.addEventListener('change', function() {
            const selectedRange = this.value;
            debouncedUpdateDataForRange(selectedRange);
        });
    }
}

// ✅ UPDATED: Enhanced refresh function with proper cleanup
function refreshCharts() {
    try {
        console.log('🔄 Refreshing all charts...');
        validateAnalyticsData();
        initializeCharts(); // This will handle destroy + recreate
        console.log('📊 Charts refreshed successfully');
    } catch (error) {
        console.error('❌ Error refreshing charts:', error);
        showNotification('Failed to refresh charts', 'error');
    }
}

// Update metric values
function updateMetrics() {
    const metricNumbers = document.querySelectorAll('.metric-number');

    metricNumbers.forEach(element => {
        // Simulate new values (in real app, this would come from server)
        const currentValue = parseInt(element.textContent.replace(/[^0-9]/g, ''));
        const variation = Math.floor(Math.random() * 20) - 10; // ±10%
        const newValue = Math.max(0, currentValue + variation);
        const suffix = element.textContent.includes('%') ? '%' : '';

        // Animate to new value
        animateCounter(element, currentValue, newValue, 1000, suffix);
    });

    console.log('📈 Metrics updated');
}

// Initialize dropdown functionality
function initializeDropdowns() {
    const userDropdown = document.getElementById('userDropdown');
    const userDropdownMenu = document.getElementById('userDropdownMenu');

    if (userDropdown && userDropdownMenu) {
        userDropdown.addEventListener('click', function(e) {
            e.stopPropagation();
            userDropdownMenu.classList.toggle('show');
        });

        // Close user dropdown when clicking outside
        document.addEventListener('click', function(e) {
            if (!userDropdown.contains(e.target) && !userDropdownMenu.contains(e.target)) {
                userDropdownMenu.classList.remove('show');
            }
        });
    }
}

// Initialize auto-refresh
function initializeAutoRefresh() {
    const refreshBtn = document.getElementById('refreshData');

    if (refreshBtn) {
        refreshBtn.addEventListener('click', function() {
            refreshData();
        });
    }

    // Auto-refresh every 10 minutes
    setInterval(() => {
        console.log('🔄 Auto-refreshing analytics data...');
        refreshData(true);
    }, 10 * 60 * 1000);
}

// Refresh data
function refreshData(auto = false) {
    const refreshBtn = document.getElementById('refreshData');

    if (!auto && refreshBtn) {
        refreshBtn.innerHTML = '⏳ Refreshing...';
        refreshBtn.disabled = true;
    }

    if (!auto) {
        showNotification('🔄 Refreshing analytics data...', 'info');
    }

    // Simulate data refresh
    setTimeout(() => {
        refreshCharts();
        updateMetrics();

        if (!auto) {
            showNotification('✅ Analytics data refreshed!', 'success');
        }

        if (refreshBtn) {
            refreshBtn.innerHTML = '🔄 Refresh Data';
            refreshBtn.disabled = false;
        }

        console.log('🔄 Data refresh completed');
    }, auto ? 500 : 2000);
}

// Show notification
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;

    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        background: ${type === 'success' ? '#27ae60' : type === 'error' ? '#e74c3c' : '#3498db'};
        color: white;
        border-radius: 8px;
        font-weight: 600;
        z-index: 9999;
        opacity: 0;
        transform: translateX(100%);
        transition: all 0.3s ease;
        box-shadow: 0 4px 15px rgba(0,0,0,0.2);
    `;

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.style.opacity = '1';
        notification.style.transform = 'translateX(0)';
    }, 100);

    setTimeout(() => {
        notification.style.opacity = '0';
        notification.style.transform = 'translateX(100%)';

        setTimeout(() => {
            if (document.body.contains(notification)) {
                document.body.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

// Keyboard shortcuts
document.addEventListener('keydown', function(e) {
    // Ctrl/Cmd + R = Refresh data
    if ((e.ctrlKey || e.metaKey) && e.key === 'r') {
        e.preventDefault();
        refreshData();
    }

    // Ctrl/Cmd + E = Export
    if ((e.ctrlKey || e.metaKey) && e.key === 'e') {
        e.preventDefault();
        showExportModal();
    }

    // Ctrl/Cmd + P = Print
    if ((e.ctrlKey || e.metaKey) && e.key === 'p') {
        e.preventDefault();
        printReport();
    }
});

// ✅ UPDATED: Enhanced Analytics utilities with cleanup
const AnalyticsUtils = {
    // Get current metrics
    getCurrentMetrics: function() {
        const metrics = {};
        document.querySelectorAll('.metric-number').forEach(element => {
            const nextElement = element.nextElementSibling;
            if (nextElement) {
                const label = nextElement.textContent.toLowerCase().replace(/\s+/g, '_');
                metrics[label] = parseInt(element.textContent.replace(/[^0-9]/g, ''));
            }
        });
        return metrics;
    },

    // Export chart as image
    exportChartAsImage: function(chartId, filename) {
        const canvas = document.getElementById(chartId);
        if (canvas) {
            const link = document.createElement('a');
            link.download = filename + '.png';
            link.href = canvas.toDataURL();
            link.click();
        }
    },

    // Calculate trend
    calculateTrend: function(current, previous) {
        if (previous === 0) return { change: 0, direction: 'stable' };

        const change = ((current - previous) / previous * 100).toFixed(1);
        const direction = change > 0 ? 'up' : change < 0 ? 'down' : 'stable';

        return { change: Math.abs(change), direction };
    },

    // ✅ ENHANCED: Force refresh charts with cleanup
    forceRefreshCharts: function() {
        console.log('🔄 Force refreshing charts...');
        destroyAllCharts();
        setTimeout(() => {
            initializeCharts();
        }, 200);
    },

    // ✅ ENHANCED: Reset analytics data with cleanup
    resetAnalyticsData: function() {
        destroyAllCharts();
        window.analyticsData = {
            bookingStatus: { approved: 15, pending: 5, rejected: 2 },
            userTypes: { normal: 20, vip: 5, admin: 2 },
            timeSlots: ['9:00 AM', '10:00 AM', '11:00 AM', '2:00 PM', '3:00 PM'],
            contextPath: ''
        };
        setTimeout(() => {
            initializeCharts();
        }, 200);
        console.log('🔄 Analytics data reset and charts refreshed');
    },

    // ✅ NEW: Clean up all charts
    destroyAllCharts: destroyAllCharts,

    // ✅ NEW: Clean up all Chart.js instances
    destroyAllChartInstances: function() {
        // Modern way to destroy all Chart.js instances
        Chart.helpers.each(Chart.instances, function (instance) {
            try {
                instance.destroy();
            } catch (e) {
                console.warn('Error destroying chart instance:', e);
            }
        });

        // Reset our chart variables
        bookingStatusChart = null;
        userTypeChart = null;
        timeSlotsChart = null;

        console.log('🧹 All Chart.js instances destroyed');
    }
};

// Make utilities available globally
window.AnalyticsUtils = AnalyticsUtils;

// Performance monitoring
function performHealthCheck() {
    const healthMetrics = {
        chartsLoaded: !!(bookingStatusChart && userTypeChart && timeSlotsChart),
        dataLoaded: !!window.analyticsData,
        pageLoadTime: performance.now(),
        timestamp: new Date().toISOString()
    };

    console.log('🏥 Analytics Health Check:', healthMetrics);
    return healthMetrics;
}

// ✅ ENHANCED: Error recovery functions
function recoverFromError() {
    console.log('🔧 Attempting error recovery...');

    try {
        validateAnalyticsData();

        // Use the comprehensive cleanup method
        AnalyticsUtils.destroyAllChartInstances();

        setTimeout(() => {
            initializeCharts();
            showNotification('✅ System recovered successfully', 'success');
        }, 500);
    } catch (error) {
        console.error('❌ Recovery failed:', error);
        showNotification('❌ Recovery failed, please refresh page', 'error');
    }
}

// Expose recovery function globally
window.recoverAnalytics = recoverFromError;

// Window error handler
window.addEventListener('error', function(e) {
    console.error('❌ Global error:', e.error);
    if (e.error && e.error.message && e.error.message.includes('Chart')) {
        setTimeout(() => {
            recoverFromError();
        }, 1000);
    }
});

// Perform initial health check after everything loads
setTimeout(() => {
    performHealthCheck();
}, 2000);

console.log('🎯 Enhanced Analytics Dashboard JavaScript loaded successfully');
