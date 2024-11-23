package ra.librarymanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.librarymanagement.model.member.Member;
import ra.librarymanagement.model.member.MemberStatus;
import ra.librarymanagement.model.member.MemberType;
import ra.librarymanagement.service.IMemberService;
import ra.librarymanagement.util.FileUploadUtil;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/members")
public class MemberController {

    private final IMemberService memberService;

    @Autowired
    public MemberController(IMemberService memberService) {
        this.memberService = memberService;
    }

    @ModelAttribute
    public void addCommonAttributes(Model model) {
        model.addAttribute("memberTypes", MemberType.values());
        model.addAttribute("memberStatuses", MemberStatus.values());
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("members", memberService.findAll());
        return "admin/members/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("member", new Member());
        model.addAttribute("memberTypes", MemberType.values());
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
        List<Member> members;
        if (keyword != null && !keyword.trim().isEmpty()) {
            members = memberService.findByFullNameContaining(keyword);
        } else if (memberType != null) {
            members = memberService.findByMemberType(memberType);
        } else if (status != null) {
            members = memberService.findByStatus(status);
        } else {
            members = memberService.findAll();
        }

        model.addAttribute("members", members);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedType", memberType);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("memberTypes", MemberType.values());
        model.addAttribute("memberStatuses", MemberStatus.values());
        return "admin/members/index";
    }
}
