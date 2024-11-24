<%--
  Created by IntelliJ IDEA.
  User: bhnone
  Date: 2024/11/23
  Time: 13:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!-- Search and Filter Section -->
<div class="bg-base-100 p-4 rounded-lg shadow mb-6">
    <form action="/admin/books/search" method="GET" class="space-y-4">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
            <!-- Search Input -->
            <div class="form-control">
                <div class="input-group">
                    <input type="text" name="keyword" placeholder="Search by ISBN, title, author..."
                           class="input input-bordered w-full" value="${param.keyword}"/>
                    <button class="btn btn-square">
                        <i class="fas fa-search"></i>
                    </button>
                </div>
            </div>

            <!-- Category Filter -->
            <div class="form-control">
                <select name="category" class="select select-bordered w-full">
                    <option value="">All Categories</option>
                    <c:forEach items="${categories}" var="category">
                        <option value="${category}" ${param.category == category ? 'selected' : ''}>
                                ${category}
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

            <!-- Add New Book Button -->
            <div class="form-control">
                <a href="/admin/books/add" class="btn btn-primary">
                    <i class="fas fa-plus"></i> Add New Book
                </a>
            </div>
        </div>
    </form>
</div>

<!-- Books Table -->
<div class="overflow-x-auto bg-base-100 rounded-lg shadow">
    <table class="table table-zebra w-full">
        <thead>
        <tr>
            <th>Cover</th>
            <th>ISBN</th>
            <th>Title</th>
            <th>Author</th>
            <th>Category</th>
            <th>Quantity</th>
            <th>Status</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td colspan="8">
                <c:if test="${empty books}">
                    Books is empty
                </c:if>
                <c:if test="${not empty books}">
                    Number of books: ${books.size()}
                </c:if>
            </td>
        </tr>
        <c:forEach items="${books}" var="book">
            <tr>
                <td>
                    <div class="avatar">
                        <div class="mask mask-squircle w-12 h-12">
                            <img src="${book.coverImage != null ? book.coverImage : '/img/default-book.png'}"
                                 alt="${book.title}"/>
                        </div>
                    </div>
                </td>
                <td>${book.isbn}</td>
                <td>
                    <div class="font-bold">${book.title}</div>
                    <div class="text-sm opacity-50">
                            ${book.publisher}, ${book.publicationYear}
                    </div>
                </td>
                <td>${book.author}</td>
                <td>
                    <div class="badge badge-ghost">${book.category}</div>
                </td>
                <td>
                    <div class="badge ${book.quantity > 0 ? 'badge-success' : 'badge-error'}">
                            ${book.quantity}
                    </div>
                </td>
                <td>
                    <div class="badge
                            ${book.bookStatus == 'AVAILABLE' ? 'badge-success' :
                              book.bookStatus == 'OUT_OF_STOCK' ? 'badge-warning' : 'badge-error'}">
                            ${book.bookStatus.displayValue}
                    </div>
                </td>
                <td>
                    <div class="dropdown dropdown-end">
                        <label tabindex="0" class="btn btn-ghost btn-xs">
                            <i class="fas fa-ellipsis-v"></i>
                        </label>
                        <ul tabindex="0" class="dropdown-content menu p-2 shadow bg-base-100 rounded-box w-52">
                            <li>
                                <a href="/admin/books/view/${book.bookId}" class="text-info">
                                    <i class="fas fa-eye"></i> View Details
                                </a>
                            </li>
                            <li>
                                <a href="/admin/books/edit/${book.bookId}" class="text-warning">
                                    <i class="fas fa-edit"></i> Edit
                                </a>
                            </li>
                            <li>
                                <a href="#" onclick="confirmDelete('${book.bookId}')" class="text-error">
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

<!-- Pagination -->
<div class="flex justify-center mt-4">
    <div class="btn-group">
        <c:forEach begin="1" end="${totalPages}" var="page">
            <a href="?page=${page}&keyword=${param.keyword}&category=${param.category}&status=${param.status}"
               class="btn ${currentPage == page ? 'btn-active' : ''}">${page}</a>
        </c:forEach>
    </div>
</div>

<!-- Delete Confirmation Modal -->
<dialog id="delete-modal" class="modal">
    <form method="dialog" class="modal-box">
        <h3 class="font-bold text-lg">Confirm Delete</h3>
        <p class="py-4">Are you sure you want to delete this book? This action cannot be undone.</p>
        <div class="modal-action">
            <button class="btn" onclick="closeDeleteModal()">Cancel</button>
            <button class="btn btn-error" onclick="deleteBook()">Delete</button>
        </div>
    </form>
</dialog>

<!-- JavaScript -->
<script>
    let bookIdToDelete = null;

    function confirmDelete(bookId) {
        bookIdToDelete = bookId;
        document.getElementById('delete-modal').showModal();
    }

    function closeDeleteModal() {
        document.getElementById('delete-modal').close();
        bookIdToDelete = null;
    }

    function deleteBook() {
        console.log(bookIdToDelete);
        if (bookIdToDelete) {
            try {
                window.location.href = "/admin/books/delete/" + bookIdToDelete;
            } catch (e) {
                console.log(e);
            }
        }
    }
</script>
