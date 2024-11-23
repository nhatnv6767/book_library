<%--
  Created by IntelliJ IDEA.
  User: bhnone
  Date: 2024/11/23
  Time: 16:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="container mx-auto p-4">
    <!-- Back button and edit -->
    <div class="flex justify-between items-center mb-6">
        <a href="/admin/books" class="btn btn-ghost">
            <i class="fas fa-arrow-left"></i> Back to List
        </a>
        <a href="/admin/books/edit/${book.bookId}" class="btn btn-warning">
            <i class="fas fa-edit"></i> Edit Book
        </a>
    </div>

    <!-- Book Details -->
    <div class="card bg-base-100 shadow-xl">
        <div class="card-body">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <!-- Left Column - Image -->
                <div>
                    <div class="w-full aspect-[3/4] rounded-lg overflow-hidden shadow-lg">
                        <img src="${book.coverImage != null ? book.coverImage : '/static/images/default-book.png'}"
                             alt="${book.title}"
                             class="w-full h-full object-cover"/>
                    </div>
                </div>

                <!-- Right Column - Details -->
                <div class="space-y-4">
                    <h2 class="text-3xl font-bold">${book.title}</h2>

                    <div class="grid grid-cols-1 gap-3">
                        <!-- ISBN -->
                        <div>
                            <span class="text-sm text-gray-500">ISBN</span>
                            <p class="font-semibold">${book.isbn}</p>
                        </div>

                        <!-- Author -->
                        <div>
                            <span class="text-sm text-gray-500">Author</span>
                            <p class="font-semibold">${book.author}</p>
                        </div>

                        <!-- Publisher & Year -->
                        <div>
                            <span class="text-sm text-gray-500">Publisher</span>
                            <p class="font-semibold">
                                ${book.publisher}
                                <c:if test="${book.edition != null}">
                                    (${book.edition})
                                </c:if>
                                , ${book.publicationYear}
                            </p>
                        </div>

                        <!-- Category -->
                        <div>
                            <span class="text-sm text-gray-500">Category</span>
                            <div class="badge badge-ghost">${book.category}</div>
                        </div>

                        <!-- Language -->
                        <div>
                            <span class="text-sm text-gray-500">Language</span>
                            <p class="font-semibold">${book.language}</p>
                        </div>

                        <!-- Price -->
                        <div>
                            <span class="text-sm text-gray-500">Price</span>
                            <p class="font-semibold">
                                <fmt:formatNumber value="${book.price}" type="currency" currencySymbol="₫"/>
                            </p>
                        </div>

                        <!-- Status Information -->
                        <div class="divider"></div>

                        <div class="stats shadow">
                            <!-- Quantity -->
                            <div class="stat">
                                <div class="stat-title">Quantity</div>
                                <div class="stat-value text-primary">${book.quantity}</div>
                                <div class="stat-desc">Available copies</div>
                            </div>

                            <!-- Status -->
                            <div class="stat">
                                <div class="stat-title">Status</div>
                                <div class="stat-value">
                                    <div class="badge badge-lg
                                        ${book.bookStatus == 'AVAILABLE' ? 'badge-success' :
                                          book.bookStatus == 'OUT_OF_STOCK' ? 'badge-warning' :
                                          'badge-error'}">
                                        ${book.bookStatus.displayValue}
                                    </div>
                                </div>
                                <div class="stat-desc">Current status</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Description -->
            <div class="mt-6">
                <h3 class="text-xl font-bold mb-2">Description</h3>
                <p class="text-gray-600 whitespace-pre-line">${book.description}</p>
            </div>

            <!-- Borrowing History -->
            <div class="mt-6">
                <h3 class="text-xl font-bold mb-2">Borrowing History</h3>
                <div class="overflow-x-auto">
                    <table class="table table-zebra w-full">
                        <thead>
                        <tr>
                            <th>Member</th>
                            <th>Borrow Date</th>
                            <th>Due Date</th>
                            <th>Return Date</th>
                            <th>Status</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${book.borrowRecords}" var="record">
                            <tr>
                                <td>${record.member.fullName}</td>
                                <td>
                                    <fmt:formatDate value="${record.borrowDate}" pattern="dd/MM/yyyy"/>
                                </td>
                                <td>
                                    <fmt:formatDate value="${record.dueDate}" pattern="dd/MM/yyyy"/>
                                </td>
                                <td>
                                    <c:if test="${record.returnDate != null}">
                                        <fmt:formatDate value="${record.returnDate}" pattern="dd/MM/yyyy"/>
                                    </c:if>
                                </td>
                                <td>
                                    <div class="badge
                                            ${record.status == 'BORROWING' ? 'badge-primary' :
                                              record.status == 'RETURNED' ? 'badge-success' :
                                              record.status == 'OVERDUE' ? 'badge-warning' :
                                              'badge-error'}">
                                            ${record.status}
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
