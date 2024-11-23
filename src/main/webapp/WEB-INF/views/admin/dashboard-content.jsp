<%--
  Created by IntelliJ IDEA.
  User: bhnone
  Date: 2024/11/23
  Time: 12:43
  To change this template use File | Settings | File Templates.
--%>
<!-- Stats Cards -->
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
    <!-- Total Books -->
    <div class="stats shadow">
        <div class="stat">
            <div class="stat-figure text-primary">
                <i class="fas fa-book fa-2x"></i>
            </div>
            <div class="stat-title">Total Books</div>
            <div class="stat-value text-primary">${totalBooks}</div>
            <div class="stat-desc">
                ${availableBooks} available
            </div>
        </div>
    </div>

    <!-- Active Members -->
    <div class="stats shadow">
        <div class="stat">
            <div class="stat-figure text-secondary">
                <i class="fas fa-users fa-2x"></i>
            </div>
            <div class="stat-title">Active Members</div>
            <div class="stat-value text-secondary">${activeMembers}</div>
            <div class="stat-desc">
                ${newMembersThisMonth} new this month
            </div>
        </div>
    </div>

    <!-- Current Borrows -->
    <div class="stats shadow">
        <div class="stat">
            <div class="stat-figure text-accent">
                <i class="fas fa-clipboard-list fa-2x"></i>
            </div>
            <div class="stat-title">Current Borrows</div>
            <div class="stat-value text-accent">${currentBorrows}</div>
            <div class="stat-desc">
                ${overdueBorrows} overdue
            </div>
        </div>
    </div>

    <!-- Total Fines -->
    <div class="stats shadow">
        <div class="stat">
            <div class="stat-figure text-info">
                <i class="fas fa-money-bill fa-2x"></i>
            </div>
            <div class="stat-title">Total Fines</div>
            <div class="stat-value text-info">
                <fmt:formatNumber value="${totalFines}" type="currency" currencySymbol="₫"/>
            </div>
            <div class="stat-desc">
                This month
            </div>
        </div>
    </div>
</div>

<!-- Charts Section -->
<div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
    <!-- Borrow Trends -->
    <div class="card bg-base-100 shadow-xl">
        <div class="card-body">
            <h2 class="card-title">Borrow Trends</h2>
            <canvas id="borrowTrendsChart"></canvas>
        </div>
    </div>

    <!-- Popular Books -->
    <div class="card bg-base-100 shadow-xl">
        <div class="card-body">
            <h2 class="card-title">Most Popular Books</h2>
            <canvas id="popularBooksChart"></canvas>
        </div>
    </div>
</div>

<!-- Recent Activities & Alerts -->
<div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
    <!-- Recent Activities -->
    <div class="card bg-base-100 shadow-xl">
        <div class="card-body">
            <h2 class="card-title">Recent Activities</h2>
            <div class="overflow-x-auto">
                <table class="table table-zebra">
                    <tbody>
                    <c:forEach items="${recentActivities}" var="activity">
                        <tr>
                            <td>
                                <div class="flex items-center space-x-3">
                                    <div class="avatar ${activity.type == 'BORROW' ? 'placeholder' : ''}">
                                        <div class="mask mask-squircle bg-base-300 w-12">
                                            <span class="text-xl">${activity.type.charAt(0)}</span>
                                        </div>
                                    </div>
                                    <div>
                                        <div class="font-bold">${activity.description}</div>
                                        <div class="text-sm opacity-50">
                                            <fmt:formatDate value="${activity.timestamp}" pattern="dd/MM/yyyy HH:mm"/>
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Alerts & Notifications -->
    <div class="card bg-base-100 shadow-xl">
        <div class="card-body">
            <h2 class="card-title">Alerts & Notifications</h2>
            <div class="space-y-4">
                <c:forEach items="${alerts}" var="alert">
                    <div class="alert ${alert.type == 'OVERDUE' ? 'alert-error' : 'alert-warning'}">
                        <i class="fas ${alert.type == 'OVERDUE' ? 'fa-exclamation-circle' : 'fa-bell'}"></i>
                        <span>${alert.message}</span>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</div>

<!-- Charts Initialization -->
<script>
    // Borrow Trends Chart
    const borrowTrendsCtx = document.getElementById('borrowTrendsChart').getContext('2d');
    new Chart(borrowTrendsCtx, {
        type: 'line',
        data: {
            labels: ${borrowTrendsLabels},
            datasets: [{
                label: 'Borrows',
                data: ${borrowTrendsData},
                borderColor: 'rgb(75, 192, 192)',
                tension: 0.1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'top',
                },
                title: {
                    display: true,
                    text: 'Monthly Borrow Trends'
                }
            }
        }
    });

    // Popular Books Chart
    const popularBooksCtx = document.getElementById('popularBooksChart').getContext('2d');
    new Chart(popularBooksCtx, {
        type: 'bar',
        data: {
            labels: ${popularBooksLabels},
            datasets: [{
                label: 'Times Borrowed',
                data: ${popularBooksData},
                backgroundColor: 'rgba(153, 102, 255, 0.2)',
                borderColor: 'rgb(153, 102, 255)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'top',
                },
                title: {
                    display: true,
                    text: 'Most Borrowed Books'
                }
            }
        }
    });
</script>
