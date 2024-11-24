<%--
  Created by IntelliJ IDEA.
  User: bhnone
  Date: 2024/11/23
  Time: 12:41
  To change this template use File | Settings | File Templates.
--%>
<!-- /WEB-INF/views/shared/layout.jsp -->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html data-theme="light">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.title} - Library Management</title>
    <!-- Tailwind CSS -->
    <script src="https://cdn.tailwindcss.com"></script>
    <!-- DaisyUI -->
    <link href="https://cdn.jsdelivr.net/npm/daisyui@4.4.19/dist/full.min.css" rel="stylesheet" type="text/css"/>
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css"/>

    <!-- Tom Select CSS -->
    <link href="https://cdn.jsdelivr.net/npm/tom-select@2.2.2/dist/css/tom-select.css" rel="stylesheet">
    <!-- Tom Select JS -->
    <script src="https://cdn.jsdelivr.net/npm/tom-select@2.2.2/dist/js/tom-select.complete.min.js"></script>
    

    <!-- Custom CSS -->
    <style>
        @media (max-width: 768px) {
            .responsive-table {
                display: block;
                overflow-x: auto;
            }
        }
    </style>
</head>
<body>
<div class="drawer lg:drawer-open">
    <input id="my-drawer-2" type="checkbox" class="drawer-toggle"/>
    <div class="drawer-content flex flex-col">
        <!-- Navbar -->
        <div class="navbar bg-base-100 shadow-lg">
            <div class="flex-none lg:hidden">
                <label for="my-drawer-2" class="btn btn-square btn-ghost">
                    <i class="fas fa-bars"></i>
                </label>
            </div>
            <div class="flex-1">
                <span class="text-xl font-bold">${param.title}</span>
            </div>
            <div class="flex-none">
                <div class="dropdown dropdown-end">
                    <label tabindex="0" class="btn btn-ghost btn-circle avatar">
                        <div class="w-10 rounded-full">
                            <%--                            <img src="/uploads/avatars/admin.jpg"/>--%>
                            <img src="https://cdn.pixabay.com/photo/2016/04/23/20/21/smart-1348189_1280.jpg"/>
                        </div>
                    </label>
                    <ul tabindex="0"
                        class="menu menu-sm dropdown-content mt-3 z-[1] p-2 shadow bg-base-100 rounded-box w-52">
                        <li><a href="/admin/profile">Profile</a></li>
                        <li><a href="/admin/settings">Settings</a></li>
                        <li><a href="/logout">Logout</a></li>
                    </ul>
                </div>
            </div>
        </div>

        <!-- Main Content -->
        <div class="p-4">
            <!-- Alert Messages -->
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success mb-4">
                    <i class="fas fa-check-circle"></i>
                    <span>${successMessage}</span>
                </div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-error mb-4">
                    <i class="fas fa-exclamation-circle"></i>
                    <span>${errorMessage}</span>
                </div>
            </c:if>

            <!-- Page Content -->
            <jsp:include page="${param.content}"/>
        </div>
    </div>

    <!-- Sidebar -->
    <div class="drawer-side">
        <label for="my-drawer-2" class="drawer-overlay"></label>
        <ul class="menu p-4 w-80 min-h-full bg-base-200 text-base-content">
            <li class="mb-2">
                <h2 class="text-xl font-bold mb-4">Library Management</h2>
            </li>
            <li>
                <a href="/admin/dashboard" class="${param.active == 'dashboard' ? 'active' : ''}">
                    <i class="fas fa-chart-line"></i> Dashboard
                </a>
            </li>
            <li>
                <a href="/admin/books" class="${param.active == 'books' ? 'active' : ''}">
                    <i class="fas fa-book"></i> Books
                </a>
            </li>
            <li>
                <a href="/admin/members" class="${param.active == 'members' ? 'active' : ''}">
                    <i class="fas fa-users"></i> Members
                </a>
            </li>
            <li>
                <a href="/admin/borrows" class="${param.active == 'borrows' ? 'active' : ''}">
                    <i class="fas fa-clipboard-list"></i> Borrow Records
                </a>
            </li>
            <li>
                <a href="/admin/reservations" class="${param.active == 'reservations' ? 'active' : ''}">
                    <i class="fas fa-bookmark"></i> Reservations
                </a>
            </li>
            <li>
                <a href="/admin/reports" class="${param.active == 'reports' ? 'active' : ''}">
                    <i class="fas fa-chart-bar"></i> Reports
                </a>
            </li>
        </ul>
    </div>
</div>

<!-- Modal Container -->
<div id="modal-container"></div>



<!-- Scripts -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>


<script>
    // Theme Switcher
    function toggleTheme() {
        const html = document.querySelector('html');
        const currentTheme = html.getAttribute('data-theme');
        const newTheme = currentTheme === 'light' ? 'dark' : 'light';
        html.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
    }

    // Load saved theme
    const savedTheme = localStorage.getItem('theme') || 'light';
    document.querySelector('html').setAttribute('data-theme', savedTheme);

    // Auto-close alerts
    setTimeout(() => {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(alert => {
            alert.style.display = 'none';
        });
    }, 5000);
</script>
</body>
</html>
