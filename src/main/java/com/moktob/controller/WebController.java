package com.moktob.controller;

import com.moktob.common.TenantContextHolder;
import com.moktob.dto.ClientRegistrationRequest;
import com.moktob.dto.AuthenticationRequest;
import com.moktob.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebController {

    private final DashboardService dashboardService;

    // Home page
    @GetMapping("/")
    public String home() {
        return "redirect:/moktob/login";
    }

    // Moktob root page
    @GetMapping("/moktob")
    public String moktobHome() {
        return "redirect:/moktob/login";
    }
    
    // Moktob root page with trailing slash
    @GetMapping("/moktob/")
    public String moktobHomeSlash() {
        return "redirect:/moktob/login";
    }

    // Login page
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new AuthenticationRequest());
        return "auth/login";
    }

    // Registration page
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("clientRegistrationRequest", new ClientRegistrationRequest());
        return "auth/register";
    }

    // Dashboard page
    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        Long clientId = TenantContextHolder.getTenantId();
        String userName = TenantContextHolder.getUsername();
        log.info("Dashboard page - Client ID: {}", clientId);
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("title", "Dashboard - Moktob Management System");
        model.addAttribute("clientId", clientId);
        model.addAttribute("userName", userName);
        return "dashboard/index";
    }

    // Students page
    @GetMapping("/students")
    public String studentsPage(Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Students");
        model.addAttribute("title", "Students - Moktob Management System");
        model.addAttribute("userName", userName);
        return "students/index";
    }

    // Add Student page
    @GetMapping("/students/add")
    public String addStudentPage(Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Add Student");
        model.addAttribute("title", "Add Student - Moktob Management System");
        model.addAttribute("isEdit", false);
        model.addAttribute("userName", userName);
        return "students/add";
    }

    // Edit Student page
    @GetMapping("/students/edit/{id}")
    public String editStudentPage(@PathVariable Long id, Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Edit Student");
        model.addAttribute("title", "Edit Student - Moktob Management System");
        model.addAttribute("isEdit", true);
        model.addAttribute("studentId", id);
        model.addAttribute("userName", userName);
        return "students/add";
    }

    // Teachers page
    @GetMapping("/teachers")
    public String teachersPage(Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Teachers");
        model.addAttribute("title", "Teachers - Moktob Management System");
        model.addAttribute("userName", userName);
        return "teachers/index";
    }

    // Add Teacher page
    @GetMapping("/teachers/add")
    public String addTeacherPage(Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Add Teacher");
        model.addAttribute("title", "Add Teacher - Moktob Management System");
        model.addAttribute("isEdit", false);
        model.addAttribute("userName", userName);
        return "teachers/add";
    }

    // Edit Teacher page
    @GetMapping("/teachers/edit/{id}")
    public String editTeacherPage(@PathVariable Long id, Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Edit Teacher");
        model.addAttribute("title", "Edit Teacher - Moktob Management System");
        model.addAttribute("isEdit", true);
        model.addAttribute("teacherId", id);
        model.addAttribute("userName", userName);
        return "teachers/add";
    }

    // Classes page
    @GetMapping("/classes")
    public String classesPage(Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Classes");
        model.addAttribute("title", "Classes - Moktob Management System");
        model.addAttribute("userName", userName);
        return "classes/index";
    }

    // Add Class page
    @GetMapping("/classes/add")
    public String addClassPage(Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Add Class");
        model.addAttribute("title", "Add Class - Moktob Management System");
        model.addAttribute("isEdit", false);
        model.addAttribute("userName", userName);
        return "classes/add";
    }

    // Edit Class page
    @GetMapping("/classes/edit/{id}")
    public String editClassPage(@PathVariable Long id, Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Edit Class");
        model.addAttribute("title", "Edit Class - Moktob Management System");
        model.addAttribute("isEdit", true);
        model.addAttribute("classId", id);
        model.addAttribute("userName", userName);
        return "classes/add";
    }

    // Attendance page
    @GetMapping("/attendance")
    public String attendancePage(Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Attendance");
        model.addAttribute("title", "Attendance - Moktob Management System");
        model.addAttribute("userName", userName);
        return "attendance/index";
    }

    // Assessments page
    @GetMapping("/assessments")
    public String assessmentsPage(Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Assessments");
        model.addAttribute("title", "Assessments - Moktob Management System");
        model.addAttribute("userName", userName);
        return "assessments/index";
    }

    // Payments page
    @GetMapping("/payments")
    public String paymentsPage(Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Payments");
        model.addAttribute("title", "Payments - Moktob Management System");
        model.addAttribute("userName", userName);
        return "payments/index";
    }

    // Reports page
    @GetMapping("/reports")
    public String reportsPage(Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Reports");
        model.addAttribute("title", "Reports - Moktob Management System");
        model.addAttribute("userName", userName);
        return "reports/index";
    }

    // Profile page
    @GetMapping("/profile")
    public String profilePage(Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Profile");
        model.addAttribute("title", "Profile - Moktob Management System");
        model.addAttribute("userName", userName);
        return "profile/index";
    }

    // Settings page
    @GetMapping("/settings")
    public String settingsPage(Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Settings");
        model.addAttribute("title", "Settings - Moktob Management System");
        model.addAttribute("userName", userName);
        return "settings/index";
    }

    // Terms of Service page
    @GetMapping("/terms")
    public String termsPage(Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Terms of Service");
        model.addAttribute("title", "Terms of Service - Moktob Management System");
        model.addAttribute("userName", userName);
        return "terms/index";
    }

    // Privacy Policy page
    @GetMapping("/privacy")
    public String privacyPage(Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Privacy Policy");
        model.addAttribute("title", "Privacy Policy - Moktob Management System");
        model.addAttribute("userName", userName);
        return "privacy/index";
    }

    // Help page
    @GetMapping("/help")
    public String helpPage(Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Help & Documentation");
        model.addAttribute("title", "Help & Documentation - Moktob Management System");
        model.addAttribute("userName", userName);
        return "help/index";
    }

    // Support page
    @GetMapping("/support")
    public String supportPage(Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Support Center");
        model.addAttribute("title", "Support Center - Moktob Management System");
        model.addAttribute("userName", userName);
        return "support/index";
    }

    // Logout
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/moktob/login";
    }

    // Error pages
    @GetMapping("/error")
    public String errorPage(Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Error");
        model.addAttribute("title", "Error - Moktob Management System");
        model.addAttribute("userName", userName);
        return "error/index";
    }
    
    // Forgot password page
    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Forgot Password");
        model.addAttribute("title", "Forgot Password - Moktob Management System");
        model.addAttribute("userName", userName);
        return "auth/forgot-password";
    }

    // Reset password page
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(required = false) String token, Model model) {
        String userName = TenantContextHolder.getUsername();
        model.addAttribute("pageTitle", "Reset Password");
        model.addAttribute("title", "Reset Password - Moktob Management System");
        model.addAttribute("token", token);
        model.addAttribute("userName", userName);
        return "auth/reset-password";
    }
}
