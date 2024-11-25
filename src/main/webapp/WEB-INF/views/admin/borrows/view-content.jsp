<%--
  Created by IntelliJ IDEA.
  User: bhnone
  Date: 2024/11/25
  Time: 19:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="max-w-4xl mx-auto">
    <!-- Back button and actions -->
    <div class="flex justify-between items-center mb-6">
        <a href="/admin/borrows" class="btn btn-ghost">
            <i class="fas fa-arrow-left mr-2"></i>Back to List
        </a>
        <div class="space-x-2">
            <c:if test="${borrow.status == 'BORROWING'}">
                <button onclick="showReturnModal()" class="btn btn-success">
                    <i class="fas fa-undo mr-2"></i>Return Book
                </button>

                <c:if test="${borrow.extensionCount < 2}">
                    <button onclick="showExtendModal()" class="btn btn-warning">
                        <i class="fas fa-clock mr-2"></i>Extend Period
                    </button>
                </c:if>

                <button onclick="showLostModal()" class="btn btn-error">
                    <i class="fas fa-exclamation-triangle mr-2"></i>Report Lost
                </button>
            </c:if>
        </div>
    </div>

    <!-- Main Content -->
    <div class="card bg-base-100 shadow-xl">
        <div class="card-body">
            <!-- Status Header -->
            <div class="flex items-center justify-between mb-6">
                <h2 class="card-title text-2xl">
                    Borrow Record #${borrow.borrowId}
                </h2>
                <div class="badge badge-lg ${
                    borrow.status == 'BORROWING' ? 'badge-primary' :
                    borrow.status == 'RETURNED' ? 'badge-success' :
                    borrow.status == 'OVERDUE' ? 'badge-warning' :
                    'badge-error'
                }">
                    ${borrow.status.displayValue}
                </div>
            </div>

            <!-- Member and Book Info -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <!-- Member Info -->
                <div class="card bg-base-200">
                    <div class="card-body">
                        <h3 class="card-title text-lg">
                            <i class="fas fa-user text-primary mr-2"></i>Member Information
                        </h3>
                        <div class="space-y-3">
                            <div class="flex items-center">
                                <div class="avatar mr-4">
                                    <div class="w-16 rounded-full ring ring-primary ring-offset-base-100 ring-offset-2">
                                        <img src="${empty borrow.member.avatar ? '/static/images/default-avatar.png' : borrow.member.avatar}"
                                             alt="${borrow.member.fullName}"/>
                                    </div>
                                </div>
                                <div>
                                    <h4 class="font-bold">${borrow.member.fullName}</h4>
                                    <p class="text-sm opacity-70">${borrow.member.memberCode}</p>
                                </div>
                            </div>
                            <div class="grid grid-cols-2 gap-2 text-sm">
                                <div>
                                    <i class="fas fa-id-card mr-2"></i>
                                    <span class="font-medium">Member Type:</span>
                                    <div class="badge badge-primary badge-sm ml-1">${borrow.member.memberType}</div>
                                </div>
                                <div>
                                    <i class="fas fa-bookmark mr-2"></i>
                                    <span class="font-medium">Status:</span>
                                    <div class="badge ${borrow.member.status == 'ACTIVE' ? 'badge-success' : 'badge-error'} badge-sm ml-1">
                                        ${borrow.member.status}
                                    </div>
                                </div>
                                <div>
                                    <i class="fas fa-envelope mr-2"></i>
                                    <span class="opacity-70">${borrow.member.email}</span>
                                </div>
                                <div>
                                    <i class="fas fa-phone mr-2"></i>
                                    <span class="opacity-70">${borrow.member.phone}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Book Info -->
                <div class="card bg-base-200">
                    <div class="card-body">
                        <h3 class="card-title text-lg">
                            <i class="fas fa-book text-primary mr-2"></i>Book Information
                        </h3>
                        <div class="space-y-3">
                            <div class="flex items-center">
                                <div class="avatar mr-4">
                                    <div class="w-16 rounded-lg">
                                        <img src="${empty borrow.book.coverImage ? '/static/images/default-book.png' : borrow.book.coverImage}"
                                             alt="${borrow.book.title}"/>
                                    </div>
                                </div>
                                <div>
                                    <h4 class="font-bold">${borrow.book.title}</h4>
                                    <p class="text-sm opacity-70">ISBN: ${borrow.book.isbn}</p>
                                </div>
                            </div>
                            <div class="grid grid-cols-2 gap-2 text-sm">
                                <div>
                                    <i class="fas fa-user-edit mr-2"></i>
                                    <span class="font-medium">Author:</span>
                                    <span class="ml-1 opacity-70">${borrow.book.author}</span>
                                </div>
                                <div>
                                    <i class="fas fa-tag mr-2"></i>
                                    <span class="font-medium">Category:</span>
                                    <div class="badge badge-ghost badge-sm ml-1">${borrow.book.category}</div>
                                </div>
                                <div>
                                    <i class="fas fa-building mr-2"></i>
                                    <span class="font-medium">Publisher:</span>
                                    <span class="ml-1 opacity-70">${borrow.book.publisher}</span>
                                </div>
                                <div>
                                    <i class="fas fa-layer-group mr-2"></i>
                                    <span class="font-medium">Available:</span>
                                    <span class="ml-1 ${borrow.book.quantity > 0 ? 'text-success' : 'text-error'} font-bold">
                                        ${borrow.book.quantity}
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Borrow Details -->
            <div class="divider"></div>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div class="stats shadow">
                    <div class="stat">
                        <div class="stat-title">Borrow Date</div>
                        <div class="stat-value text-primary text-2xl">
                            ${borrow.borrowDate.format(dateFormatter)}
                        </div>
                        <div class="stat-desc">
                            ${borrow.borrowDate.format(timeFormatter)}
                        </div>
                    </div>
                </div>

                <div class="stats shadow">
                    <div class="stat">
                        <div class="stat-title">Due Date</div>
                        <div class="stat-value text-warning text-2xl">
                            ${borrow.dueDate.format(dateFormatter)}
                        </div>
                        <div class="stat-desc">
                            ${borrow.dueDate.format(timeFormatter)}
                            <c:if test="${borrow.status == 'BORROWING'}">
                                <c:choose>
                                    <c:when test="${daysUntilDue > 0}">
                                        Due in ${daysUntilDue} days
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-error">Overdue by ${daysOverdue} days</span>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                            <c:if test="${borrow.extensionCount > 0}">
                                <div class="badge badge-ghost badge-sm">Extended ${borrow.extensionCount}x</div>
                            </c:if>
                        </div>
                    </div>
                </div>

                <div class="stats shadow">
                    <div class="stat">
                        <div class="stat-title">Return Date</div>
                        <div class="stat-value text-2xl">
                            <c:choose>
                                <c:when test="${borrow.returnDate != null}">
                                    <span class="text-success">
                                            ${borrow.returnDate.format(dateFormatter)}
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="opacity-50">Pending</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="stat-desc">
                            <c:if test="${borrow.returnDate != null}">
                                ${borrow.returnDate.format(timeFormatter)}
                            </c:if>
                            <c:if test="${not empty borrow.actualReturnCondition}">
                                <div class="badge badge-ghost badge-sm">${borrow.actualReturnCondition}</div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Fine Information -->
            <c:if test="${not empty borrow.fine && borrow.fine != 0}">
                <div class="alert alert-error mt-4">
                    <i class="fas fa-exclamation-circle"></i>
                    <div>
                        <h3 class="font-bold">Fine Applied</h3>
                        <div class="text-sm">
                            Amount: <fmt:formatNumber value="${borrow.fine}" type="currency" currencySymbol="₫"/>
                            <c:if test="${not empty borrow.note}">
                                <br/>
                                Reason: ${borrow.note}
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
</div>

<!-- Return Modal -->
<!-- <dialog id="return-modal" class="modal">
<form method="POST" action="/admin/borrows/return/${borrow.borrowId}" class="modal-box">
<h3 class="font-bold text-lg">Return Book</h3>
<div class="py-4">
<div class="form-control">
<label class="label">
<span class="label-text">Book Condition</span>
</label>
<textarea name="condition" class="textarea textarea-bordered" required
placeholder="Enter the condition of the book upon return"></textarea>
</div>
</div>
<div class="modal-action">
<button type="button" class="btn" onclick="closeModal('return-modal')">Cancel</button>
<button type="submit" class="btn btn-success">Return Book</button>
</div>
</form>
</dialog> -->
<dialog id="return-modal" class="modal">
    <div class="modal-box">
        <h3 class="font-bold text-lg">Return Book</h3>
        <form method="POST" action="/admin/borrows/return/${borrow.borrowId}">
            <div class="py-4">
                <div class="form-control">
                    <label class="label">
                        <span class="label-text">Book Condition</span>
                    </label>
                    <textarea name="condition" class="textarea textarea-bordered" required
                              placeholder="Enter the condition of the book upon return"></textarea>
                </div>
            </div>
            <div class="modal-action">
                <button type="button" class="btn" onclick="closeModal('return-modal')">Cancel</button>
                <button type="submit" class="btn btn-success">Return Book</button>
            </div>
        </form>
    </div>
</dialog>

<!-- Extend Modal -->
<dialog id="extend-modal" class="modal">
    <form method="POST" action="/admin/borrows/extend/${borrow.borrowId}" class="modal-box">
        <h3 class="font-bold text-lg">Extend Borrow Period</h3>
        <div class="py-4">
            <p>Current due date: ${borrow.dueDate.format(dateFormatter)}</p>
            <p>New due date will be: ${newDueDate.format(dateFormatter)}</p>

            <div class="alert alert-info mt-4">
                <i class="fas fa-info-circle"></i>
                <span>Extension count: ${borrow.extensionCount}/2</span>
            </div>
        </div>
        <div class="modal-action">
            <button type="button" class="btn" onclick="closeModal('extend-modal')">Cancel</button>
            <button type="submit" class="btn btn-warning">Extend Period</button>
        </div>
    </form>
</dialog>

<!-- Lost Modal -->
<dialog id="lost-modal" class="modal">
    <form method="POST" action="/admin/borrows/lost/${borrow.borrowId}" class="modal-box">
        <h3 class="font-bold text-lg text-error">Report Book as Lost</h3>
        <div class="py-4">
            <div class="alert alert-warning mb-4">
                <i class="fas fa-exclamation-triangle"></i>
                <span>A fine of ${lostBookFine}₫ will be applied.</span>
            </div>

            <div class="form-control">
                <label class="label">
                    <span class="label-text">Additional Notes</span>
                </label>
                <textarea name="notes" class="textarea textarea-bordered" required
                          placeholder="Enter any additional notes about the lost book"></textarea>
            </div>
        </div>
        <div class="modal-action">
            <button type="button" class="btn" onclick="closeModal('lost-modal')">Cancel</button>
            <button type="submit" class="btn btn-error">Report Lost</button>
        </div>
    </form>
</dialog>

<script>
    function showReturnModal() {
        try {
            const modal = document.getElementById('return-modal');
            if (modal) {
                modal.showModal();
            } else {
                console.error('Return modal not found');
            }
        } catch (e) {
            console.error('Error showing return modal:', e);
        }
    }

    function showExtendModal() {
        try {
            const modal = document.getElementById('extend-modal');
            if (modal) {
                modal.showModal();
            } else {
                console.error('Extend modal not found');
            }
        } catch (e) {
            console.error('Error showing extend modal:', e);
        }
    }

    function showLostModal() {
        try {
            const modal = document.getElementById('lost-modal');
            if (modal) {
                modal.showModal();
            } else {
                console.error('Lost modal not found');
            }
        } catch (e) {
            console.error('Error showing lost modal:', e);
        }
    }

    function closeModal(modalId) {
        try {
            const modal = document.getElementById(modalId);
            if (modal) {
                modal.close();
            } else {
                console.error(`Modal ${modalId} not found`);
            }
        } catch (e) {
            console.error('Error closing modal:', e);
        }
    }
</script>
