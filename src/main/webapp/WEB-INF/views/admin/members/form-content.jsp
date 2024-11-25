<%--
  Created by IntelliJ IDEA.
  User: bhnone
  Date: 2024/11/23
  Time: 23:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="max-w-4xl mx-auto">
    <div class="card bg-base-100 shadow-xl">
        <div class="card-body">
            <h2 class="card-title">
                ${member.memberId == null ? 'Add New Member' : 'Edit Member'}
            </h2>

            <form action="${member.memberId == null ? '/admin/members/add' : '/admin/members/edit/'.concat(member.memberId)}"
                  method="POST"
                  enctype="multipart/form-data"
                  class="space-y-6">

                <input type="hidden" name="memberId" value="${member.memberId}"/>

                <!-- Basic Information Section -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <!-- Member Code -->
                    <div class="form-control">
                        <label class="label">
                            <span class="label-text">Member Code*</span>
                        </label>
                        <input type="text" name="memberCode" value="${member.memberCode}"
                               class="input input-bordered" required
                        ${member.memberId != null ? 'readonly' : ''}
                               pattern="^[A-Za-z0-9]{5,10}$"
                               title="5-10 characters, alphanumeric only"/>
                    </div>

                    <!-- Full Name -->
                    <div class="form-control">
                        <label class="label">
                            <span class="label-text">Full Name</span>
                        </label>
                        <input type="text" name="fullName" value="${member.fullName}"
                               class="input input-bordered ${bindingResult.hasFieldErrors('fullName') ? 'input-error' : ''}" />
                        <c:if test="${bindingResult.hasFieldErrors('fullName')}">
                            <label class="label">
                                <span class="label-text-alt text-error">${bindingResult.getFieldError('fullName').defaultMessage}</span>
                            </label>
                        </c:if>
                    </div>

                    <!-- Email -->
                    <div class="form-control">
                        <label class="label">
                            <span class="label-text">Email*</span>
                        </label>
                        <input type="email" name="email" value="${member.email}"
                               class="input input-bordered" required/>
                    </div>

                    <!-- Phone -->
                    <div class="form-control">
                        <label class="label">
                            <span class="label-text">Phone</span>
                        </label>
                        <input type="tel" name="phone" value="${member.phone}"
                               class="input input-bordered"
                               pattern="[0-9]{10,15}"/>
                    </div>
                </div>

                <!-- Additional Information Section -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <!-- Member Type -->
                    <div class="form-control">
                        <label class="label">
                            <span class="label-text">Member Type*</span>
                        </label>
                        <select name="memberType" class="select select-bordered" required>
                            <c:forEach items="${memberTypes}" var="type">
                                <option value="${type}" ${member.memberType == type ? 'selected' : ''}>
                                        ${type.displayValue}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- Status (only for edit) -->
                    <c:if test="${member.memberId != null}">
                        <div class="form-control">
                            <label class="label">
                                <span class="label-text">Status*</span>
                            </label>
                            <select name="status" class="select select-bordered" required>
                                <c:forEach items="${memberStatuses}" var="status">
                                    <option value="${status}" ${member.status == status ? 'selected' : ''}>
                                            ${status.displayValue}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </c:if>

                    <!-- Identity Card -->
                    <div class="form-control">
                        <label class="label">
                            <span class="label-text">Identity Card</span>
                        </label>
                        <input type="text" name="identityCard" value="${member.identityCard}"
                               class="input input-bordered"/>
                    </div>

                    <!-- Date of Birth -->
                    <div class="form-control">
                        <label class="label">
                            <span class="label-text">Date of Birth</span>
                        </label>
                        <!-- <input type="datetime-local" name="dateOfBirth"
                               value="${member.dateOfBirth}"
                               class="input input-bordered"/> -->
                               <input type="date" name="dateOfBirth"
           value="${member.dateOfBirth != null ? member.dateOfBirth.toLocalDate() : ''}"
           class="input input-bordered"/>
                    </div>
                </div>

                <!-- Address Section -->
                <div class="form-control">
                    <label class="label">
                        <span class="label-text">Address</span>
                    </label>
                    <textarea name="address" class="textarea textarea-bordered h-24">${member.address}</textarea>
                </div>

                <!-- Avatar Upload -->
                <div class="form-control">
                    <label class="label">
                        <span class="label-text">Avatar</span>
                    </label>
                    <div class="flex items-center space-x-4">
                        <c:if test="${member.avatar != null}">
                            <div class="avatar">
                                <div class="w-24 rounded">
                                    <img src="${member.avatar}" alt="Current avatar"/>
                                </div>
                            </div>
                        </c:if>
                        <input type="file" name="avatarFile"
                               class="file-input file-input-bordered w-full max-w-xs"
                               accept="image/*"/>
                    </div>
                </div>

                <!-- Notes -->
                <div class="form-control">
                    <label class="label">
                        <span class="label-text">Notes</span>
                    </label>
                    <textarea name="note" class="textarea textarea-bordered h-24">${member.note}</textarea>
                </div>

                <!-- Expiry Date -->
                <div class="form-control">
                    <label class="label">
                        <span class="label-text">Expiry Date</span>
                    </label>
                    <!-- <input type="datetime-local" name="expiryDate"
                           value="${member.expiryDate}"
                           class="input input-bordered"/> -->
                           <input type="date" name="expiryDate"
           value="${member.expiryDate != null ? member.expiryDate.toLocalDate() : ''}"
           class="input input-bordered"/>
                </div>

                <!-- Form Buttons -->
                <div class="card-actions justify-end mt-6">
                    <a href="/admin/members" class="btn btn-ghost">Cancel</a>
                    <button type="submit" class="btn btn-primary">
                        ${member.memberId == null ? 'Add Member' : 'Update Member'}
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    // Form validation
    document.querySelector('form').addEventListener('submit', function (e) {
        const memberType = document.querySelector('select[name="memberType"]').value;
        const expiryDate = document.querySelector('input[name="expiryDate"]').value;

        // Validate expiry date for students
        if (memberType === 'STUDENT' && !expiryDate) {
            e.preventDefault();
            alert('Expiry date is required for student members');
            return;
        }

        // Additional form validations can be added here
    });
</script>
