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

            <form id="borrowForm" action="/admin/borrows/add" method="POST" class="space-y-6">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <!-- Member Selection -->
                    <div class="form-control">
                        <label class="label">
                            <span class="label-text font-semibold">
                                <i class="fas fa-user text-primary mr-2"></i>Member
                            </span>
                        </label>
                        <select id="member-select" name="memberId" required>
                            <c:forEach items="${members}" var="member">
                                <option value="${member.memberId}"
                                        data-code="${member.memberCode}"
                                        data-status="${member.status}"
                                        data-active-borrows="${memberActiveBorrows[member.memberId]}"
                                        data-type="${member.memberType}"
                                        data-email="${member.email}"
                                        data-phone="${member.phone}">
                                        ${member.memberCode} - ${member.fullName}
                                </option>
                            </c:forEach>
                        </select>
                        <!-- Member Preview -->
                        <div id="member-preview" class="mt-2 hidden animate__animated animate__fadeIn">
                            <div class="bg-base-200 p-4 rounded-lg">
                                <div class="flex items-center gap-2 mb-2">
                                    <span id="member-status" class="badge"></span>
                                    <span id="member-type" class="badge badge-primary"></span>
                                </div>
                                <div class="space-y-1 text-sm">
                                    <p><i class="fas fa-envelope mr-2"></i><span id="member-email"></span></p>
                                    <p><i class="fas fa-phone mr-2"></i><span id="member-phone"></span></p>
                                    <div class="flex items-center mt-2">
                                        <i class="fas fa-book mr-2"></i>
                                        Active Borrows: <span id="member-active-borrows" class="ml-1 font-bold"></span>
                                        <div id="borrow-limit-warning" class="hidden ml-2 text-error">
                                            <i class="fas fa-exclamation-circle"></i> Reached borrow limit
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Book Selection -->
                    <div class="form-control">
                        <label class="label">
                            <span class="label-text font-semibold">
                                <i class="fas fa-book text-primary mr-2"></i>Book
                            </span>
                        </label>
                        <select id="book-select" name="bookId" required>
                            <c:forEach items="${books}" var="book">
                                <option value="${book.bookId}"
                                        data-quantity="${book.quantity}"
                                        data-isbn="${book.isbn}"
                                        data-author="${book.author}"
                                        data-category="${book.category}"
                                        data-publisher="${book.publisher}">
                                        ${book.title}
                                </option>
                            </c:forEach>
                        </select>
                        <!-- Book Preview -->
                        <div id="book-preview" class="mt-2 hidden animate__animated animate__fadeIn">
                            <div class="bg-base-200 p-4 rounded-lg">
                                <div class="grid grid-cols-2 gap-2 text-sm">
                                    <p><i class="fas fa-barcode mr-2"></i>ISBN: <span id="book-isbn"
                                                                                      class="font-medium"></span></p>
                                    <p><i class="fas fa-user-edit mr-2"></i>Author: <span id="book-author"
                                                                                          class="font-medium"></span>
                                    </p>
                                    <p><i class="fas fa-tag mr-2"></i>Category: <span id="book-category"
                                                                                      class="font-medium"></span></p>
                                    <p><i class="fas fa-building mr-2"></i>Publisher: <span id="book-publisher"
                                                                                            class="font-medium"></span>
                                    </p>
                                </div>
                                <div class="mt-2 flex items-center">
                                    <i class="fas fa-layer-group mr-2"></i>
                                    Available: <span id="book-quantity" class="ml-1 font-bold"></span>
                                    <div id="quantity-warning" class="hidden ml-2 text-error">
                                        <i class="fas fa-exclamation-circle"></i> Out of stock
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Borrow Rules Notice -->
                <div class="bg-info/10 rounded-lg p-4 shadow-lg mt-4">
                    <div class="animate__animated animate__fadeIn">
                        <div class="flex items-center text-info mb-2">
                            <i class="fas fa-info-circle text-xl mr-2"></i>
                            <h3 class="font-bold text-lg">Borrowing Rules</h3>
                        </div>
                        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                            <div class="card bg-base-100 shadow-sm">
                                <div class="card-body p-4">
                                    <h4 class="card-title text-sm mb-2">
                                        <i class="fas fa-clock text-primary"></i>
                                        Duration
                                    </h4>
                                    <p class="text-sm">${maxBorrowDays} days maximum</p>
                                </div>
                            </div>
                            <div class="card bg-base-100 shadow-sm">
                                <div class="card-body p-4">
                                    <h4 class="card-title text-sm mb-2">
                                        <i class="fas fa-money-bill text-success"></i>
                                        Late Fee
                                    </h4>
                                    <p class="text-sm">
                                        <fmt:formatNumber value="${lateFeePerDay}" type="currency" currencySymbol="₫"/>
                                        / day
                                    </p>
                                </div>
                            </div>
                            <div class="card bg-base-100 shadow-sm">
                                <div class="card-body p-4">
                                    <h4 class="card-title text-sm mb-2">
                                        <i class="fas fa-book text-warning"></i>
                                        Limit Per Type
                                    </h4>
                                    <ul class="text-xs space-y-1">
                                        <li>Regular: ${regularMaxBooks} books</li>
                                        <li>VIP: ${vipMaxBooks} books</li>
                                        <li>Student: ${studentMaxBooks} books</li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Submit Buttons -->
                <div class="card-actions justify-end mt-6">
                    <a href="/admin/borrows" class="btn btn-ghost">
                        <i class="fas fa-times mr-2"></i>Cancel
                    </a>
                    <button type="submit" id="submit-btn" class="btn btn-primary" disabled>
                        <i class="fas fa-check mr-2"></i>Create Borrow Record
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Initialize Tom Select and Validation -->
<script>
    let memberSelect, bookSelect;

    document.addEventListener('DOMContentLoaded', function () {
        memberSelect = new TomSelect('#member-select', {
            create: false,
            sortField: {
                field: "text",
                direction: "asc"
            },
            placeholder: "Search for member...",
            render: {
                option: function (item, escape) {
                    var statusBadge = item.dataset.status === 'ACTIVE' ?
                        '<span class="badge badge-success badge-sm ml-2">Active</span>' :
                        '<span class="badge badge-error badge-sm ml-2">Inactive</span>';

                    var borrowsBadge = item.dataset.status === 'ACTIVE' ?
                        `<span class="badge badge-ghost badge-sm ml-2">
                    Borrows: ${item.dataset.activeBorrows}
                </span>` : '';

                    return '<div class="d-flex align-items-center">' +
                        '<span>' + escape(item.text) + '</span>' +
                        statusBadge +
                        borrowsBadge +
                        '</div>';
                },
                no_results: function () {
                    return '<div class="no-results">No members found</div>';
                }
            },
            searchField: ['text', 'value'],
            valueField: 'value',
            labelField: 'text',
            options: Array.from(document.querySelectorAll('#member-select option')).map(opt => ({
                value: opt.value,
                text: opt.textContent.trim(),
                dataset: opt.dataset
            })),
            onInitialize: function () {
                this.setValue(''); // Clear selection on initialize
            },
            openOnFocus: true,
            onChange: function (value) {
                if (!value) {
                    hidePreview('member-preview');
                    validateForm();
                    return;
                }
                updateMemberPreview(this.options[value]);
                validateForm();
            }
        });

        bookSelect = new TomSelect('#book-select', {
            create: false,
            sortField: {
                field: "text",
                direction: "asc"
            },
            placeholder: "Search for book...",
            render: {
                option: function (item, escape) {
                    const quantity = parseInt(item.dataset.quantity);
                    const author = item.dataset.author;

                    return '<div class="flex items-center justify-between w-full">' +
                        '<div class="flex flex-col">' +
                        '<span class="font-medium">' + escape(item.text) + '</span>' +
                        '<span class="text-sm opacity-70">' + escape(author) + '</span>' +
                        '</div>' +
                        '<span class="badge ' + (quantity > 0 ? 'badge-success' : 'badge-error') + ' badge-sm">' +
                        (quantity > 0 ? 'Available: ' + quantity : 'Out of stock') +
                        '</span>' +
                        '</div>';
                },
                no_results: function () {
                    return '<div class="text-center">No books found</div>';
                }
            },
            searchField: ['text', 'value'],
            valueField: 'value',
            labelField: 'text',
            options: Array.from(document.querySelectorAll('#book-select option')).map(opt => ({
                value: opt.value,
                text: opt.textContent.trim(),
                dataset: opt.dataset
            })),
            onInitialize: function () {
                this.setValue('');
            },
            openOnFocus: true,
            onChange: function (value) {
                if (!value) {
                    hidePreview('book-preview');
                    validateForm();
                    return;
                }
                updateBookPreview(this.options[value]);
                validateForm();
            }
        });

        // Call validateForm after initializing TomSelect instances
        validateForm();
    });

    function hidePreview(id) {
        const preview = document.getElementById(id);
        preview.classList.add('hidden');
    }

    function updateMemberPreview(memberOption) {
        const preview = document.getElementById('member-preview');
        const status = memberOption.dataset.status;
        const type = memberOption.dataset.type;
        const activeBorrows = parseInt(memberOption.dataset.activeBorrows);
        const email = memberOption.dataset.email;
        const phone = memberOption.dataset.phone;

        const statusElement = document.getElementById('member-status');
        if (status === 'ACTIVE') {
            statusElement.className = 'badge badge-success';
            statusElement.textContent = 'Active';
        } else {
            statusElement.className = 'badge badge-error';
            statusElement.textContent = 'Inactive';
        }

        document.getElementById('member-type').textContent = type;
        document.getElementById('member-email').textContent = email || 'N/A';
        document.getElementById('member-phone').textContent = phone || 'N/A';

        const borrowsElement = document.getElementById('member-active-borrows');
        const borrowLimitWarning = document.getElementById('borrow-limit-warning');

        if (status === 'ACTIVE') {
            borrowsElement.textContent = activeBorrows;
            const maxBooks = getMaxBooksForType(type);
            borrowsElement.className = activeBorrows >= maxBooks ? 'text-error' : 'text-success';
            borrowLimitWarning.classList.toggle('hidden', activeBorrows < maxBooks);
        } else {
            borrowsElement.textContent = 'N/A';
            borrowsElement.className = 'text-error';
            borrowLimitWarning.classList.add('hidden');
        }

        preview.classList.remove('hidden');
    }

    function updateBookPreview(bookOption) {

        const preview = document.getElementById('book-preview');
        console.log('Book data:', {
            isbn: bookOption.dataset.isbn,
            author: bookOption.dataset.author,
            category: bookOption.dataset.category,
            publisher: bookOption.dataset.publisher,
            quantity: bookOption.dataset.quantity
        });

        document.getElementById('book-isbn').textContent = bookOption.dataset.isbn || 'N/A';
        document.getElementById('book-author').textContent = bookOption.dataset.author || 'N/A';
        document.getElementById('book-category').textContent = bookOption.dataset.category || 'N/A';
        document.getElementById('book-publisher').textContent = bookOption.dataset.publisher || 'N/A';


        const quantity = parseInt(bookOption.dataset.quantity);
        const quantityElement = document.getElementById('book-quantity');
        if (quantity > 0) {
            console.log("Quantity", quantity)
            quantityElement.innerHTML = `
            <span class="badge badge-success">
                <i class="fas fa-check-circle mr-1"></i>
                Available: ${quantity}
            </span>
        `;
        } else {
            quantityElement.innerHTML = `
            <span class="badge badge-error">
                <i class="fas fa-times-circle mr-1"></i>
                Out of stock
            </span>
        `;
        }

        document.getElementById('quantity-warning').classList.toggle('hidden', quantity > 0);

        preview.classList.remove('hidden');
        preview.classList.add('animate__animated', 'animate__fadeIn');
    }

    function getMaxBooksForType(type) {
        switch (type) {
            case 'VIP':
                return ${vipMaxBooks};
            case 'STUDENT':
                return ${studentMaxBooks};
            default:
                return ${regularMaxBooks};
        }
    }

    function validateForm() {
        const submitBtn = document.getElementById('submit-btn');

        if (!memberSelect || !bookSelect) {
            submitBtn.disabled = true;
            return;
        }

        const memberValue = memberSelect.getValue();
        const bookValue = bookSelect.getValue();

        if (!memberValue || !bookValue) {
            submitBtn.disabled = true;
            return;
        }

        const memberOption = memberSelect.options[memberValue];
        const bookOption = bookSelect.options[bookValue];

        if (!memberOption || !bookOption) {
            submitBtn.disabled = true;
            return;
        }

        if (memberOption.dataset.status !== 'ACTIVE') {
            submitBtn.disabled = true;
            return;
        }

        const type = memberOption.dataset.type;
        const activeBorrows = parseInt(memberOption.dataset.activeBorrows);
        const quantity = parseInt(bookOption.dataset.quantity);

        const isValid = activeBorrows < getMaxBooksForType(type) && quantity > 0;

        submitBtn.disabled = !isValid;
    }
</script>
