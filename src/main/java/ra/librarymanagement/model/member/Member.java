package ra.librarymanagement.model.member;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import ra.librarymanagement.model.BorrowRecord.BorrowRecord;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "members")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Member {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(name = "member_code", nullable = false, unique = true, length = 10)
    @Pattern(regexp = "^MEM\\d{7}$", message = "Invalid member code format")
    private String memberCode;

    @Column(name = "full_name", nullable = false, length = 100)
    @NotBlank(message = "Full name is required")
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format") 
    private String email;

    @Column(name = "phone", nullable = true, length = 15)
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phone;

    @Column(name = "address", nullable = true, length = 255)
    private String address;

    @Column(name = "join_date", nullable = false)
    private LocalDateTime joinDate;

    @Column(name = "member_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ToString.Exclude
    @OneToMany(mappedBy = "member")
    private List<BorrowRecord> borrowRecord;

    @Column(name = "date_of_birth", nullable = true)
    private LocalDateTime dateOfBirth;

    @Column(name = "avatar", nullable = true, length = 255)
    private String avatar;

    @Column(name = "note", nullable = true, length = 500)
    private String note;

    @Column(name = "identity_card", nullable = true, length = 20)
    private String identityCard;

    @Column(name = "expiry_date", nullable = true)
    private LocalDateTime expiryDate;
}
