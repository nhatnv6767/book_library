<%--
  Created by IntelliJ IDEA.
  User: bhnone
  Date: 2024/11/23
  Time: 13:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="max-w-4xl mx-auto">
    <div class="card bg-base-100 shadow-xl">
        <div class="card-body">
            <h2 class="card-title">
                ${book.bookId == null ? 'Add New Book' : 'Edit Book'}
            </h2>

            <form action="${book.bookId == null ? '/admin/books/add' : '/admin/books/edit/'.concat(book.bookId)}"
                  method="POST"
                  enctype="multipart/form-data"
                  class="space-y-4">

                <input type="hidden" name="bookId" value="${book.bookId}"/>

                <!-- ISBN -->
                <div class="form-control">
                    <label class="label">
                        <span class="label-text">ISBN</span>
                    </label>
                    <input type="text" name="isbn" value="${book.isbn}"
                           class="input input-bordered"
                           required pattern=".{13,13}"
                           title="ISBN must be exactly 13 characters"
                    ${book.bookId != null ? 'readonly' : ''}/>
                </div>

                <!-- Title -->
                <div class="form-control">
                    <label class="label">
                        <span class="label-text">Title</span>
                    </label>
                    <input type="text" name="title" value="${book.title}"
                           class="input input-bordered" required/>
                </div>

                <!-- Author -->
                <div class="form-control">
                    <label class="label">
                        <span class="label-text">Author</span>
                    </label>
                    <input type="text" name="author" value="${book.author}"
                           class="input input-bordered" required/>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <!-- Publication Year -->
                    <div class="form-control">
                        <label class="label">
                            <span class="label-text">Publication Year</span>
                        </label>
                        <input type="number" name="publicationYear"
                               value="${book.publicationYear}"
                               class="input input-bordered" required
                               min="1900" max="2099"/>
                    </div>

                    <!-- Category -->
                    <div class="form-control">
                        <label class="label">
                            <span class="label-text">Category</span>
                        </label>
                        <select name="category" class="select select-bordered" required>
                            <option value="">Select Category</option>
                            <c:forEach items="${categories}" var="category">
                                <option value="${category}"
                                    ${book.category == category ? 'selected' : ''}>
                                        ${category}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <!-- Quantity -->
                    <div class="form-control">
                        <label class="label">
                            <span class="label-text">Quantity</span>
                        </label>
                        <input type="number" name="quantity"
                               value="${book.quantity}"
                               class="input input-bordered" required
                               min="0"/>
                    </div>

                    <!-- Price -->
                    <div class="form-control">
                        <label class="label">
                            <span class="label-text">Price (VND)</span>
                        </label>
                        <input type="number" name="price"
                               value="${book.price}"
                               class="input input-bordered" required
                               min="0" step="1000"/>
                    </div>
                </div>

                <!-- Cover Image -->
                <div class="form-control">
                    <label class="label">
                        <span class="label-text">Cover Image</span>
                    </label>
                    <input type="file" name="coverImageFile"
                           class="file-input file-input-bordered w-full"
                           accept="image/*"
                    ${book.bookId == null ? 'required' : ''}/>
                    <c:if test="${book.coverImage != null}">
                        <div class="mt-2">
                            <img src="${book.coverImage}"
                                 alt="${book.title}"
                                 class="w-32 h-32 object-cover rounded"/>
                        </div>
                    </c:if>
                </div>

                <!-- Description -->
                <div class="form-control">
                    <label class="label">
                        <span class="label-text">Description</span>
                    </label>
                    <textarea name="description"
                              class="textarea textarea-bordered h-24">${book.description}</textarea>
                </div>

                <!-- Additional Fields -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <!-- Publisher -->
                    <div class="form-control">
                        <label class="label">
                            <span class="label-text">Publisher</span>
                        </label>
                        <input type="text" name="publisher"
                               value="${book.publisher}"
                               class="input input-bordered"/>
                    </div>

                    <!-- Edition -->
                    <div class="form-control">
                        <label class="label">
                            <span class="label-text">Edition</span>
                        </label>
                        <input type="text" name="edition"
                               value="${book.edition}"
                               class="input input-bordered"/>
                    </div>
                </div>

                <!-- Status -->
                <div class="form-control">
                    <label class="label">
                        <span class="label-text">Status</span>
                    </label>
                    <select name="status" class="select select-bordered" required>
                        <c:forEach items="${statuses}" var="status">
                            <option value="${status}"
                                ${book.bookStatus == status ? 'selected' : ''}>
                                    ${status.displayValue}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <!-- Submit Buttons -->
                <div class="card-actions justify-end">
                    <a href="/admin/books" class="btn btn-ghost">Cancel</a>
                    <button type="submit" class="btn btn-primary">
                        ${book.bookId == null ? 'Add Book' : 'Update Book'}
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
