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
                        <select id="member-select" name="memberId" required>
                            <c:forEach items="${members}" var="member">
                                <option value="${member.memberId}"
                                        data-code="${member.memberCode}"
                                        data-status="${member.status}">
                                        ${member.memberCode} - ${member.fullName}
                                </option>
                            </c:forEach>
                        </select>
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
                                        data-isbn="${book.isbn}">
                                        ${book.title}
                                </option>
                            </c:forEach>
                        </select>
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

<!-- Initialize Tom Select -->
<script>
    document.addEventListener('DOMContentLoaded', function () {
        new TomSelect('#member-select', {
            create: false,
            sortField: {
                field: "text",
                direction: "asc"
            },
            placeholder: "Search for member...",
            render: {
                option: function (item, escape) {
                    var status = item.dataset.status === 'ACTIVE' ?
                        '<span class="badge badge-success badge-sm ml-2">Active</span>' : '';
                    return '<div class="d-flex align-items-center">' +
                        '<span>' + escape(item.text) + '</span>' +
                        status +
                        '</div>';
                },
                no_results: function () {
                    return '<div class="no-results">No members found</div>';
                }
            },
            searchField: ['text', 'value'], // Add 'value' into searchField
            valueField: 'value', // add valueField
            labelField: 'text',  // add labelField
            options: Array.from(document.querySelectorAll('#member-select option')).map(opt => ({
                value: opt.value,
                text: opt.textContent.trim(),
                dataset: opt.dataset
            })),
            onInitialize: function () {
                this.setValue(''); // Clear selection on initialize
            },
            openOnFocus: true
        });

        new TomSelect('#book-select', {
            create: false,
            sortField: {
                field: "text",
                direction: "asc"
            },
            placeholder: "Search for book...",
            render: {
                option: function (item, escape) {
                    return '<div class="d-flex align-items-center">' +
                        '<span>' + escape(item.text) + '</span>' +
                        '<span class="badge badge-info badge-sm ml-2">' +
                        'Available: ' + item.dataset.quantity +
                        '</span>' +
                        '</div>';
                },
                no_results: function () {
                    return '<div class="no-results">No members found</div>';
                }
            },
            searchField: ['text', 'value'], // add 'value' into searchField
            valueField: 'value', // add valueField
            labelField: 'text',  // add labelField
            options: Array.from(document.querySelectorAll('#book-select option')).map(opt => ({
                value: opt.value,
                text: opt.textContent.trim(),
                dataset: opt.dataset
            })),
            onInitialize: function () {
                this.setValue(''); // Clear selection on initialize
            },
            openOnFocus: true
        });
    });
</script>