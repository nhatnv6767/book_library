<%--
  Created by IntelliJ IDEA.
  User: bhnone
  Date: 2024/11/24
  Time: 12:35
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="bg-base-100 p-4 rounded-lg shadow mb-6">
    <form action="/admin/borrows/search" method="GET" class="space-y-4">
        <div class="grid grid-cols-1 md:grid-cols-5 gap-4">
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

            <!-- Date Range -->
            <div class="form-control">
                <input type="datetime-local" name="startDate"
                       class="input input-bordered"
                       value="${startDate}"
                       placeholder="Start Date"/>
            </div>

            <div class="form-control">
                <input type="datetime-local" name="endDate"
                       class="input input-bordered"
                       value="${endDate}"
                       placeholder="End Date"/>
            </div>

            <!-- Search Button -->
            <div class="form-control">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-search mr-2"></i> Search
                </button>
            </div>

            <!-- Add New Button -->
            <div class="form-control">
                <a href="/admin/borrows/add" class="btn btn-accent">
                    <i class="fas fa-plus mr-2"></i> New Borrow
                </a>
            </div>
        </div>
    </form>
</div>

<!-- Borrow Records Table -->
<div class="overflow-x-auto bg-base-100 rounded-lg shadow">
    <table class="table table-zebra w-full">
        <thead>
        <tr>
            <th>ID</th>
            <th>Member</th>
            <th>Book</th>
            <th>Dates</th>
            <th>Status</th>
            <th>Fine</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${borrows}" var="borrow">
            <tr>
                <td>${borrow.borrowId}</td>
                <td>
                    <div class="flex items-center space-x-3">
                        <div class="avatar">
                            <div class="mask mask-squircle w-12 h-12">
                                <!-- <img src="${borrow.member.avatar != null ? borrow.member.avatar : '/static/images/default-avatar.png'}"
                                     alt="${borrow.member.fullName}"/> -->
                                <img src="${empty borrow.member.avatar ? '/static/images/default-avatar.png' : borrow.member.avatar}"
                                     alt="${borrow.member.fullName}"/>
                            </div>
                        </div>
                        <div>
                            <div class="font-bold">${borrow.member.fullName}</div>
                            <div class="text-sm opacity-50">${borrow.member.memberCode}</div>
                        </div>
                    </div>
                </td>
                <td>
                    <div class="flex items-center space-x-3">
                        <div class="avatar">
                            <div class="mask mask-squircle w-12 h-12">
                                <img src="${borrow.book.coverImage != null ? borrow.book.coverImage : '/static/images/default-book.png'}"
                                     alt="${borrow.book.title}"/>
                            </div>
                        </div>
                        <div>
                            <div class="font-bold">${borrow.book.title}</div>
                            <div class="text-sm opacity-50">ISBN: ${borrow.book.isbn}</div>
                        </div>
                    </div>
                </td>
                <td>
                    <div class="text-sm">
                        <div><i class="fas fa-arrow-right"></i> ${borrow.borrowDate}</div>
                        <div><i class="fas fa-clock"></i> ${borrow.dueDate}</div>
                        <c:if test="${borrow.returnDate != null}">
                            <div><i class="fas fa-arrow-left"></i> ${borrow.returnDate}</div>
                        </c:if>
                    </div>
                </td>
                <td>
                    <div class="badge ${
                            borrow.status == 'BORROWING' ? 'badge-primary' :
                            borrow.status == 'RETURNED' ? 'badge-success' :
                            borrow.status == 'OVERDUE' ? 'badge-warning' :
                            'badge-error'
                        }">
                            ${borrow.status.displayValue}
                    </div>
                    <c:if test="${borrow.extensionCount > 0}">
                        <div class="badge badge-ghost mt-1">
                            Extended ${borrow.extensionCount}x
                        </div>
                    </c:if>
                </td>
                <td>
                    <c:if test="${not empty borrow.fine && borrow.fine gt 0}">
                        <div class="text-error font-semibold">
                                ${borrow.fine} đ
                        </div>
                    </c:if>
                </td>
                <td>
                    <div class="dropdown dropdown-end">
                        <label tabindex="0" class="btn btn-ghost btn-xs">
                            <i class="fas fa-ellipsis-v"></i>
                        </label>
                        <ul tabindex="0" class="dropdown-content menu p-2 shadow bg-base-100 rounded-box w-52">
                            <li>
                                <a href="/admin/borrows/view/${borrow.borrowId}" class="text-info">
                                    <i class="fas fa-eye"></i> View Details
                                </a>
                            </li>

                            <c:if test="${borrow.status == 'BORROWING'}">
                                <li>
                                    <a href="#" onclick="showReturnModal(${borrow.borrowId})" class="text-success">
                                        <i class="fas fa-undo"></i> Return Book
                                    </a>
                                </li>

                                <c:if test="${borrow.extensionCount < 2}">
                                    <li>
                                        <a href="#" onclick="showExtendModal(${borrow.borrowId})" class="text-warning">
                                            <i class="fas fa-clock"></i> Extend Period
                                        </a>
                                    </li>
                                </c:if>

                                <li>
                                    <a href="#" onclick="showLostModal(${borrow.borrowId})" class="text-error">
                                        <i class="fas fa-exclamation-triangle"></i> Report Lost
                                    </a>
                                </li>
                            </c:if>
                        </ul>
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<!-- Return Book Modal -->
<dialog id="return-modal" class="modal">
    <form method="dialog" class="modal-box">
        <h3 class="font-bold text-lg">Return Book</h3>
        <div class="py-4">
            <div class="form-control">
                <label class="label">
                    <span class="label-text">Book Condition</span>
                </label>
                <textarea id="returnCondition" class="textarea textarea-bordered"
                          placeholder="Enter book condition on return"></textarea>
            </div>
        </div>
        <div class="modal-action">
            <button class="btn" onclick="closeReturnModal()">Cancel</button>
            <button class="btn btn-success" onclick="returnBook()">Return Book</button>
        </div>
    </form>
</dialog>

<!-- Extend Period Modal -->
<dialog id="extend-modal" class="modal">
    <form method="dialog" class="modal-box">
        <h3 class="font-bold text-lg">Extend Borrow Period</h3>
        <p class="py-4">Are you sure you want to extend the borrow period for this book?</p>
        <div class="modal-action">
            <button class="btn" onclick="closeExtendModal()">Cancel</button>
            <button class="btn btn-warning" onclick="extendPeriod()">Extend Period</button>
        </div>
    </form>
</dialog>

<!-- Report Lost Modal -->
<dialog id="lost-modal" class="modal">
    <form method="dialog" class="modal-box">
        <h3 class="font-bold text-lg text-error">Report Book as Lost</h3>
        <div class="py-4">
            <div class="alert alert-warning mb-4">
                <i class="fas fa-exclamation-triangle"></i>
                <span>Reporting a book as lost will incur a fine.</span>
            </div>
            <div class="form-control">
                <label class="label">
                    <span class="label-text">Additional Notes</span>
                </label>
                <textarea id="lostNotes" class="textarea textarea-bordered"
                          placeholder="Enter any additional notes"></textarea>
            </div>
        </div>
        <div class="modal-action">
            <button class="btn" onclick="closeLostModal()">Cancel</button>
            <button class="btn btn-error" onclick="reportLost()">Report Lost</button>
        </div>
    </form>
</dialog>

<!-- JavaScript for modals -->
<script>
    let currentBorrowId = null;

    function showReturnModal(borrowId) {
        currentBorrowId = borrowId;
        document.getElementById('return-modal').showModal();
    }

    function closeReturnModal() {
        document.getElementById('return-modal').close();
        currentBorrowId = null;
    }

    function returnBook() {
        if (currentBorrowId) {
            const condition = document.getElementById('returnCondition').value;
            window.location.href = `/admin/borrows/return/${currentBorrowId}?condition=${encodeURIComponent(condition)}`;
        }
    }

    function showExtendModal(borrowId) {
        currentBorrowId = borrowId;
        document.getElementById('extend-modal').showModal();
    }

    function closeExtendModal() {
        document.getElementById('extend-modal').close();
        currentBorrowId = null;
    }

    function extendPeriod() {
        if (currentBorrowId) {
            window.location.href = `/admin/borrows/extend/${currentBorrowId}`;
        }
    }

    function showLostModal(borrowId) {
        currentBorrowId = borrowId;
        document.getElementById('lost-modal').showModal();
    }

    function closeLostModal() {
        document.getElementById('lost-modal').close();
        currentBorrowId = null;
    }

    function reportLost() {
        if (currentBorrowId) {
            const notes = document.getElementById('lostNotes').value;
            window.location.href = `/admin/borrows/lost/${currentBorrowId}?notes=${encodeURIComponent(notes)}`;
        }
    }
</script>
