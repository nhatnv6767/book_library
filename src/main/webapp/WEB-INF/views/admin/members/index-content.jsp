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
                    <c:forEach items="${memberStatuses}" var="status">
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
            <th>Member Info</th>
            <th>Contact</th>
            <th>Type & Status</th>
            <th>Join Date</th>
            <th>Expiry Date</th>
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
                    <div class="font-bold">${member.fullName}</div>
                    <div class="text-sm opacity-50">
                        <div>ID: ${member.memberCode}</div>
                        <c:if test="${not empty member.identityCard}">
                            <div>IC: ${member.identityCard}</div>
                        </c:if>
                    </div>
                </td>
                <td>
                    <div class="text-sm">
                        <div><i class="fas fa-envelope"></i> ${member.email}</div>
                        <c:if test="${not empty member.phone}">
                            <div><i class="fas fa-phone"></i> ${member.phone}</div>
                        </c:if>
                        <c:if test="${not empty member.address}">
                            <div class="truncate max-w-xs"><i class="fas fa-map-marker-alt"></i> ${member.address}</div>
                        </c:if>
                    </div>
                </td>
                <td>
                    <div class="flex flex-col gap-2">
                        <!-- Member Type -->
                        <div class="flex items-center space-x-2">
                            <span class="w-16 text-xs text-gray-500">Type:</span>
                            <div class="badge ${
        member.memberType == 'REGULAR' ? 'bg-slate-100 text-slate-800 border border-slate-300' :
        member.memberType == 'VIP' ? 'bg-gradient-to-r from-violet-600 to-indigo-600 text-white font-semibold shadow-md' :
        'bg-gradient-to-r from-sky-400 to-teal-500 text-white shadow-sm'
    }">
                                <c:choose>
                                    <c:when test="${member.memberType == 'VIP'}">
                                        <i class="fas fa-crown text-yellow-300 mr-1"></i>
                                    </c:when>
                                    <c:when test="${member.memberType == 'STUDENT'}">
                                        <i class="fas fa-graduation-cap mr-1"></i>
                                    </c:when>
                                    <c:otherwise>
                                        <i class="fas fa-user mr-1"></i>
                                    </c:otherwise>
                                </c:choose>
                                    ${member.memberType.displayValue}
                            </div>
                        </div>
                        <!-- Status -->
                        <div class="flex items-center space-x-2">
                            <span class="w-16 text-xs text-gray-500">Status:</span>
                            <div class="badge ${member.status == 'ACTIVE' ? 'badge-success' :
                              member.status == 'SUSPENDED' ? 'badge-warning' :
                              'badge-error'}">
                                    ${member.status.displayValue}
                            </div>
                        </div>
                    </div>
                </td>
                <td>${member.joinDate}</td>
                <td>
                    <c:if test="${not empty member.expiryDate}">
                        ${member.expiryDate}
                    </c:if>
                </td>
                <td>
                    <div class="dropdown dropdown-end">
                        <label tabindex="0" class="btn btn-ghost btn-xs">
                            <i class="fas fa-ellipsis-v"></i>
                        </label>
                        <ul tabindex="0" class="dropdown-content menu p-2 shadow bg-base-100 rounded-box w-52">
                            <!-- <li>
                                <a href="/admin/members/view/${member.memberId}" class="text-info">
                                    <i class="fas fa-eye"></i> View Details
                                </a>
                            </li> -->
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
                            <c:if test="${member.status != 'ACTIVE'}">
                                <li>
                                    <a href="/admin/members/activate/${member.memberId}" class="text-success">
                                        <i class="fas fa-check-circle"></i> Activate
                                    </a>
                                </li>
                            </c:if>
                            <c:if test="${member.status == 'ACTIVE'}">
                                <li>
                                    <a href="#" onclick="showSuspendModal(${member.memberId})" class="text-warning">
                                        <i class="fas fa-pause-circle"></i> Suspend
                                    </a>
                                </li>
                            </c:if>
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
<!-- Add modal and script at the end of file -->
<dialog id="suspend-modal" class="modal">
    <form method="POST" action="" id="suspend-form" class="modal-box">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <h3 class="font-bold text-lg">Suspend Member</h3>
        <p class="py-4">Are you sure you want to suspend this member? This will prevent them from borrowing books.</p>
        <div class="form-control">
            <label class="label">
                <span class="label-text">Reason for Suspension</span>
            </label>
            <textarea name="suspensionReason" class="textarea textarea-bordered"
                      placeholder="Enter reason for suspension" required></textarea>
        </div>
        <div class="modal-action">
            <button type="button" class="btn" onclick="closeSuspendModal()">Cancel</button>
            <button type="submit" class="btn btn-error">Suspend Member</button>
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
            try {
                window.location.href = "/admin/members/delete/" + memberIdToDelete;
            } catch (e) {
                console.log(e);
            }
        }
    }

    function showSuspendModal(memberId) {
        // console.log("showSuspendModal", memberId);
        const modal = document.getElementById('suspend-modal');
        const form = document.getElementById('suspend-form');
        form.action = "/admin/members/suspend/" + memberId;
        modal.showModal();
    }

    function closeSuspendModal() {
        document.getElementById('suspend-modal').close();
    }
</script>