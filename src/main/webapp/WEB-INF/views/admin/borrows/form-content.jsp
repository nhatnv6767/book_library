<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="max-w-4xl mx-auto">
    <div class="card bg-base-100 shadow-xl">
        <div class="card-body">
            <h2 class="card-title mb-6">
                <i class="fas fa-book-reader text-primary mr-2"></i>
                New Borrow Record
            </h2>

            <form action="/admin/borrows/add" method="POST" class="space-y-6">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <!-- Member Selection -->
                    <div class="form-control">
                        <label class="label">
                            <span class="label-text font-semibold">
                                <i class="fas fa-user text-primary mr-2"></i>Member
                            </span>
                        </label>
                        <div class="relative">
                            <select name="memberId" class="select select-bordered w-full" required>
                                <option value="">Select Member</option>
                                <c:forEach items="${members}" var="member">
                                    <option value="${member.memberId}">
                                        <div class="flex items-center">
                                                ${member.memberCode} - ${member.fullName}
                                            <c:if test="${member.status == 'ACTIVE'}">
                                                <span class="badge badge-success badge-sm ml-2">Active</span>
                                            </c:if>
                                        </div>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <!-- Book Selection -->
                    <div class="form-control">
                        <label class="label">
                            <span class="label-text font-semibold">
                                <i class="fas fa-book text-primary mr-2"></i>Book
                            </span>
                        </label>
                        <div class="relative">
                            <select name="bookId" class="select select-bordered w-full" required>
                                <option value="">Select Book</option>
                                <c:forEach items="${books}" var="book">
                                    <option value="${book.bookId}" class="flex items-center justify-between">
                                            ${book.title}
                                        <span class="badge badge-info badge-sm ml-2">
                                            Available: ${book.quantity}
                                        </span>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>

                <!-- Borrow Rules Notice -->
                <div class="bg-info/10 rounded-lg p-4 shadow-lg mt-4">
                    <div class="animate__animated animate__fadeIn animate__slower">
                        <i class="fas fa-info-circle text-info"></i>
                        <div class="space-y-2">
                            <h3 class="font-bold text-lg">Borrowing Rules</h3>
                            <div class="pl-4">
                                <ul class="list-disc space-y-1">
                                    <li class="animate__animated animate__fadeInLeft animate__delay-1s animate__slower">
                                        Maximum borrow duration: 14 days
                                    </li>
                                    <li class="animate__animated animate__fadeInLeft animate__delay-2s animate__slower">
                                        Late return fee: 5,000đ/day
                                    </li>
                                    <li class="animate__animated animate__fadeInLeft animate__delay-3s animate__slower">
                                        Maximum books per member: 3
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Submit Buttons -->
                <div class="card-actions justify-end mt-6">
                    <a href="/admin/borrows" class="btn btn-ghost">
                        <i class="fas fa-times mr-2"></i>Cancel
                    </a>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-check mr-2"></i>Create Borrow Record
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>