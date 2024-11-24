package ra.librarymanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.librarymanagement.model.BorrowRecord.BorrowRecord;
import ra.librarymanagement.model.BorrowRecord.BorrowStatus;
import ra.librarymanagement.model.member.Member;
import ra.librarymanagement.model.member.MemberStatus;
import ra.librarymanagement.model.member.MemberType;
import ra.librarymanagement.service.IMemberService;
import ra.librarymanagement.util.FileUploadUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/members")
public class MemberController {

    private final IMemberService memberService;

    @Autowired
    public MemberController(IMemberService memberService) {
        this.memberService = memberService;
    }

    // @ModelAttribute
    // public void addCommonAttributes(Model model) {
    //     model.addAttribute("memberTypes", MemberType.values());
    //     model.addAttribute("memberStatuses", MemberStatus.values());
    // }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("members", memberService.findAll());
        model.addAttribute("memberTypes", MemberType.values());
        model.addAttribute("memberStatuses", MemberStatus.values());
        return "admin/members/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("member", new Member());
        model.addAttribute("memberTypes", MemberType.values());
        model.addAttribute("memberStatuses", MemberStatus.values());
        return "admin/members/form";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Member member,
                      @RequestParam("avatarFile") MultipartFile file,
                      RedirectAttributes redirectAttributes) {
        try {
            if (!file.isEmpty()) {
                String filename = FileUploadUtil.saveFile(file, "avatars");
                member.setAvatar(filename);
            }
            member.setStatus(MemberStatus.ACTIVE);
            member.setJoinDate(LocalDateTime.now());
            memberService.save(member);
            redirectAttributes.addFlashAttribute("successMessage", "Member added successfully");
            return "redirect:/admin/members";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Could not add member: " + e.getMessage());
            return "redirect:/admin/members/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Member member = memberService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member Id:" + id));
        model.addAttribute("member", member);
        model.addAttribute("memberTypes", MemberType.values());
        model.addAttribute("memberStatuses", MemberStatus.values());
        return "admin/members/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Member member,
                         @RequestParam(value = "avatarFile", required = false) MultipartFile file,
                         RedirectAttributes redirectAttributes) {
        try {

            // Get the existing member
            Member existingMember = memberService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid member Id:" + id));

            member.setCreatedAt(existingMember.getCreatedAt());
            member.setJoinDate(existingMember.getJoinDate());
            member.setUpdatedAt(LocalDateTime.now());


            if (member.getDateOfBirth() == null && existingMember.getDateOfBirth() != null) {
                member.setDateOfBirth(existingMember.getDateOfBirth());
            }

            if (member.getExpiryDate() == null && existingMember.getExpiryDate() != null) {
                member.setExpiryDate(existingMember.getExpiryDate());
            }

            // If the file is not empty, save the new file
            if (!file.isEmpty() && file != null) {
                // Delete the existing file
                if (existingMember.getAvatar() != null) {
                    FileUploadUtil.deleteFile(existingMember.getAvatar());
                }
                String filename = FileUploadUtil.saveFile(file, "avatars");
                member.setAvatar(filename);
            } else {
                member.setAvatar(existingMember.getAvatar());
            }
            memberService.update(member);
            redirectAttributes.addFlashAttribute("successMessage", "Member updated successfully");
            return "redirect:/admin/members";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Could not update member: " + e.getMessage());
            return "redirect:/admin/members/edit/" + id;
        }
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String keyword,
                         @RequestParam(required = false) MemberType memberType,
                         @RequestParam(required = false) MemberStatus status,
                         Model model) {
        // If no search criteria is provided, return all members
        List<Member> members = memberService.searchMembers(keyword, memberType, status);

        model.addAttribute("members", members);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedType", memberType);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("memberTypes", MemberType.values());
        model.addAttribute("memberStatuses", MemberStatus.values());
        return "admin/members/index";
    }


    // NEW


    @GetMapping("/view/{id}")
    public String viewDetails(@PathVariable Long id, Model model) {
        try {
            Member member = memberService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid member Id:" + id));

            // Tạo statistics cho member này
            long totalBorrows = member.getBorrowRecord().size();
            long currentBorrows = member.getBorrowRecord().stream()
                    .filter(record -> record.getStatus() == BorrowStatus.BORROWING)
                    .count();
            long overdueBorrows = member.getBorrowRecord().stream()
                    .filter(record -> record.getStatus() == BorrowStatus.OVERDUE)
                    .count();

            // Add to model
            model.addAttribute("member", member);
            model.addAttribute("totalBorrows", totalBorrows);
            model.addAttribute("currentBorrows", currentBorrows);
            model.addAttribute("overdueBorrows", overdueBorrows);

            // Check if member is expired
            boolean isExpired = member.getExpiryDate() != null &&
                    member.getExpiryDate().isBefore(LocalDateTime.now());
            model.addAttribute("isExpired", isExpired);

            // Add active borrows at the top for quick reference
            List<BorrowRecord> activeBorrows = member.getBorrowRecord().stream()
                    .filter(record -> record.getStatus() == BorrowStatus.BORROWING)
                    .collect(Collectors.toList());
            model.addAttribute("activeBorrows", activeBorrows);

            return "admin/members/view";
        } catch (Exception e) {
            return "redirect:/admin/members";
        }
    }

    // Thêm phương thức suspend
    @PostMapping("/suspend/{id}")
    public String suspendMember(@PathVariable Long id,
                                @RequestParam("suspensionReason") String reason,
                                RedirectAttributes redirectAttributes) {
        try {
            Member member = memberService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid member Id:" + id));

            // Không thể suspend member đã bị suspend hoặc expired
            if (!member.getStatus().equals(MemberStatus.ACTIVE)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Cannot suspend member. Member is not active.");
                return "redirect:/admin/members/view/" + id;
            }

            // Check nếu member đang mượn sách
            long activeBorrows = member.getBorrowRecord().stream()
                    .filter(record -> record.getStatus() == BorrowStatus.BORROWING)
                    .count();
            if (activeBorrows > 0) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Cannot suspend member. Member has active borrows.");
                return "redirect:/admin/members/view/" + id;
            }

            // Cập nhật status và note
            member.setStatus(MemberStatus.SUSPENDED);
            member.setNote(member.getNote() != null ?
                    member.getNote() + "\n[Suspended] " + reason :
                    "[Suspended] " + reason);
            member.setUpdatedAt(LocalDateTime.now());

            memberService.update(member);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Member has been suspended successfully.");

            return "redirect:/admin/members/view/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Could not suspend member: " + e.getMessage());
            return "redirect:/admin/members/view/" + id;
        }
    }

    // Thêm phương thức activate (để reactivate suspended member)
    @GetMapping("/activate/{id}")
    public String activateMember(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            Member member = memberService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid member Id:" + id));

            // Chỉ có thể activate member bị suspended
            if (!member.getStatus().equals(MemberStatus.SUSPENDED)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Cannot activate member. Member is not suspended.");
                return "redirect:/admin/members/view/" + id;
            }

            member.setStatus(MemberStatus.ACTIVE);
            member.setNote(member.getNote() + "\n[Activated] Member reactivated");
            member.setUpdatedAt(LocalDateTime.now());

            memberService.update(member);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Member has been activated successfully.");

            return "redirect:/admin/members/view/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Could not activate member: " + e.getMessage());
            return "redirect:/admin/members/view/" + id;
        }
    }

    // Thêm phương thức delete
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         @RequestParam(required = false) String reason,
                         RedirectAttributes redirectAttributes) {
        try {
            Member member = memberService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid member Id:" + id));

            // Check nếu member đang mượn sách
            long activeBorrows = member.getBorrowRecord().stream()
                    .filter(record -> record.getStatus() == BorrowStatus.BORROWING)
                    .count();
            if (activeBorrows > 0) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Cannot delete member. Member has active borrows.");
                return "redirect:/admin/members";
            }

            // Delete avatar file nếu có
            if (member.getAvatar() != null) {
                FileUploadUtil.deleteFile(member.getAvatar());
            }

            memberService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Member deleted successfully.");

            return "redirect:/admin/members";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Could not delete member: " + e.getMessage());
            return "redirect:/admin/members";
        }
    }

}
