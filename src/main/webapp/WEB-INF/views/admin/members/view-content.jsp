<%--
  Created by IntelliJ IDEA.
  User: bhnone
  Date: 2024/11/24
  Time: 0:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="max-w-4xl mx-auto">
    <!-- Back and Action Buttons -->
    <div class="flex justify-between items-center mb-6">
        <a href="/admin/members" class="btn btn-ghost">
            <i class="fas fa-arrow-left mr-2"></i> Back to List
        </a>
        <div class="space-x-2">
            <a href="/admin/members/edit/${member.memberId}" class="btn btn-warning">
                <i class="fas fa-edit mr-2"></i> Edit
            </a>
            <c:if test="${member.status == 'ACTIVE'}">
                <button class="btn btn-error" onclick="showSuspendModal()">
                    <i class="fas fa-ban mr-2"></i> Suspend
                </button>
            </c:if>
        </div>
    </div>

    <!-- Member Details Card -->
    <div class="card bg-base-100 shadow-xl">
        <div class="card-body">
            <!-- Header with Avatar -->
            <div class="flex items-center space-x-4">
                <div class="avatar">
                    <div class="w-24 rounded-full ring ring-primary ring-offset-base-100 ring-offset-2">
                        <img src="${member.avatar != null ? member.avatar : '/static/images/default-avatar.png'}"
                             alt="${member.fullName}"/>
                    </div>
                </div>
                <div>
                    <h2 class="text-2xl font-bold">${member.fullName}</h2>
                    <div class="flex items-center space-x-2 mt-1">
                        <!-- Member Type Badge -->
                        <div class="badge ${
                            member.memberType == 'REGULAR' ? 'bg-slate-100 text-slate-800 border border-slate-300' :
                            member.memberType == 'VIP' ? 'bg-gradient-to-r from-violet-600 to-indigo-600 text-white font-semibold shadow-md' :
                            'bg-gradient-to-r from-sky-400 to-teal-500 text-white shadow-sm'}">
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
                        <!-- Status Badge -->
                        <div class="badge ${
                            member.status == 'ACTIVE' ? 'badge-success' :
                            member.status == 'SUSPENDED' ? 'badge-warning' :
                            'badge-error'}">
                            ${member.status.displayValue}
                        </div>
                    </div>
                </div>
            </div>

            <!-- Member Information -->
            <div class="divider">Basic Information</div>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                    <h3 class="font-semibold">Member Code</h3>
                    <p>${member.memberCode}</p>
                </div>
                <div>
                    <h3 class="font-semibold">Identity Card</h3>
                    <p>${member.identityCard != null ? member.identityCard : 'N/A'}</p>
                </div>
                <div>
                    <h3 class="font-semibold">Email</h3>
                    <p><a href="mailto:${member.email}" class="link link-primary">${member.email}</a></p>
                </div>
                <div>
                    <h3 class="font-semibold">Phone</h3>
                    <p>${member.phone != null ? member.phone : 'N/A'}</p>
                </div>
                <div class="md:col-span-2">
                    <h3 class="font-semibold">Address</h3>
                    <p>${member.address != null ? member.address : 'N/A'}</p>
                </div>
            </div>

            <!-- Membership Information -->
            <div class="divider">Membership Information</div>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                    <h3 class="font-semibold">Join Date</h3>
                    <p>${member.joinDate}</p>
                </div>
                <div>
                    <h3 class="font-semibold">Expiry Date</h3>
                    <p>${member.expiryDate != null ? member.expiryDate : 'N/A'}</p>
                </div>
                <div class="md:col-span-2">
                    <h3 class="font-semibold">Notes</h3>
                    <p>${member.note != null ? member.note : 'No notes available.'}</p>
                </div>
            </div>

            <!-- Borrowing History -->
            <div class="divider">Borrowing History</div>
            <div class="overflow-x-auto">
                <table class="table table-zebra w-full">
                    <thead>
                    <tr>
                        <th>Book Title</th>
                        <th>Borrow Date</th>
                        <th>Due Date</th>
                        <th>Return Date</th>
                        <th>Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${member.borrowRecord}" var="record">
                        <tr>
                            <td>${record.book.title}</td>
                            <td>${record.borrowDate}</td>
                            <td>${record.dueDate}</td>
                            <td>${record.returnDate != null ? record.returnDate : '-'}</td>
                            <td>
                                <div class="badge ${
                                        record.status == 'BORROWING' ? 'badge-primary' :
                                        record.status == 'RETURNED' ? 'badge-success' :
                                        record.status == 'OVERDUE' ? 'badge-warning' :
                                        'badge-error'}">
                                        ${record.status}
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty member.borrowRecord}">
                        <tr>
                            <td colspan="5" class="text-center">No borrowing history available.</td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Suspend Modal -->
<dialog id="suspend-modal" class="modal">
    <form method="POST" action="/admin/members/suspend/${member.memberId}" class="modal-box">
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

<!-- JavaScript for modals -->
<script>
    function showSuspendModal() {
        document.getElementById('suspend-modal').showModal();
    }

    function closeSuspendModal() {
        document.getElementById('suspend-modal').close();
    }
</script>
