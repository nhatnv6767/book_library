<%--
  Created by IntelliJ IDEA.
  User: bhnone
  Date: 2024/11/23
  Time: 22:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Search and Filter Section -->
<div class="bg-base-100 p-4 rounded-lg shadow mb-6">
    <form action="/admin/members/search" method="GET" class="space-y-4">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
            <!-- Search Input -->
            <div class="form-control">
                <div class="input-group">
                    <input type="text" name="keyword"
                           placeholder="Search by Member Code, Name, Email..."
                           class="input input-bordered w-full"
                           value="${param.keyword}"/>
                    <button class="btn btn-square">
                        <i class="fas fa-search"></i>
                    </button>
                </div>
            </div>

            <!-- Member Type Filter -->
            <div class="form-control">
                <select name="memberType" class="select select-bordered w-full">
                    <option value="">All Member Types</option>
                    <c:forEach items="${memberTypes}" var="type">
                        <option value="${type}" ${param.memberType == type ? 'selected' : ''}>
                                ${type.displayValue}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <!-- Status Filter -->
            <div class="form-control">
                <select name="status" class="select select-bordered w-full">
                    <option value="">All Statuses</option>
                    <c:forEach items="${statuses}" var="status">
                        <option value="${status}" ${param.status == status ? 'selected' : ''}>
                                ${status.displayValue}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <!-- Add New Member Button -->
            <div class="form-control">
                <a href="/admin/members/add" class="btn btn-primary">
                    <i class="fas fa-plus"></i> Add New Member
                </a>
            </div>
        </div>
    </form>
</div>

<!-- Members Table -->
<div class="overflow-x-auto bg-base-100 rounded-lg shadow">
    <table class="table table-zebra w-full">
        <thead>
        <tr>
            <th>Avatar</th>
            <th>Member Code</th>
            <th>Name & Contact</th>
            <th>Member Type</th>
            <th>Join Date</th>
            <th>Status</th>
            <th>Active Borrows</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${members}" var="member">
            <tr>
                <td>
                    <div class="avatar">
                        <div class="mask mask-squircle w-12 h-12">
                            <img src="${member.avatar != null ? member.avatar : '/static/images/default-avatar.png'}"
                                 alt="${member.fullName}"/>
                        </div>
                    </div>
                </td>
                <td>
                    <div class="font-bold">${member.memberCode}</div>
                    <c:if test="${not empty member.identityCard}">
                        <div class="text-sm opacity-50">${member.identityCard}</div>
                    </c:if>
                </td>
                <td>
                    <div class="font-bold">${member.fullName}</div>
                    <div class="text-sm opacity-50">
                        <div>${member.email}</div>
                        <div>${member.phone}</div>
                    </div>
                </td>
                <td>
                    <div class="badge badge-ghost">${member.memberType.displayValue}</div>
                </td>
                <td>${member.joinDate}</td>
                <td>
                    <div class="badge
                            ${member.status == 'ACTIVE' ? 'badge-success' :
                              member.status == 'SUSPENDED' ? 'badge-warning' :
                              'badge-error'}">
                            ${member.status.displayValue}
                    </div>
                </td>
                <td class="text-center">${member.borrowRecords.size()}</td>
                <td>
                    <div class="dropdown dropdown-end">
                        <label tabindex="0" class="btn btn-ghost btn-xs">
                            <i class="fas fa-ellipsis-v"></i>
                        </label>
                        <ul tabindex="0" class="dropdown-content menu p-2 shadow bg-base-100 rounded-box w-52">
                            <li>
                                <a href="/admin/members/view/${member.memberId}" class="text-info">
                                    <i class="fas fa-eye"></i> View Details
                                </a>
                            </li>
                            <li>
                                <a href="/admin/members/edit/${member.memberId}" class="text-warning">
                                    <i class="fas fa-edit"></i> Edit
                                </a>
                            </li>
                            <li>
                                <a href="#" onclick="confirmDelete(${member.memberId})" class="text-error">
                                    <i class="fas fa-trash"></i> Delete
                                </a>
                            </li>
                        </ul>
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<!-- Delete Confirmation Modal -->
<dialog id="delete-modal" class="modal">
    <form method="dialog" class="modal-box">
        <h3 class="font-bold text-lg">Confirm Delete</h3>
        <p class="py-4">Are you sure you want to delete this member? This action cannot be undone.</p>
        <div class="modal-action">
            <button class="btn" onclick="closeDeleteModal()">Cancel</button>
            <button class="btn btn-error" onclick="deleteMember()">Delete</button>
        </div>
    </form>
</dialog>

<!-- JavaScript -->
<script>
    let memberIdToDelete = null;

    function confirmDelete(memberId) {
        memberIdToDelete = memberId;
        document.getElementById('delete-modal').showModal();
    }

    function closeDeleteModal() {
        document.getElementById('delete-modal').close();
        memberIdToDelete = null;
    }

    function deleteMember() {
        if (memberIdToDelete) {
            window.location.href = `/admin/members/delete/${memberIdToDelete}`;
        }
    }
</script>