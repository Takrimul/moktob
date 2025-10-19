package com.moktob.controller;

import com.moktob.dto.ClientRegistrationRequest;
import com.moktob.dto.AuthenticationRequest;
import com.moktob.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebController {

    private final DashboardService dashboardService;

    // Home page
    @GetMapping("/")
    public String home() {
        return "redirect:/moktob/dashboard";
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
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(30);
            
            var dashboardData = dashboardService.getDashboardOverview(startDate, endDate);
            
            model.addAttribute("dashboard", dashboardData);
            model.addAttribute("pageTitle", "Dashboard");
            model.addAttribute("title", "Dashboard - Moktob Management System");
            
            return "dashboard/index";
        } catch (Exception e) {
            log.error("Error loading dashboard", e);
            model.addAttribute("error", "Unable to load dashboard data");
            return "dashboard/index";
        }
    }

    // Students page
    @GetMapping("/students")
    public String studentsPage(Model model) {
        model.addAttribute("pageTitle", "Students");
        model.addAttribute("title", "Students - Moktob Management System");
        return "students/index";
    }

    // Teachers page
    @GetMapping("/teachers")
    public String teachersPage(Model model) {
        model.addAttribute("pageTitle", "Teachers");
        model.addAttribute("title", "Teachers - Moktob Management System");
        return "teachers/index";
    }

    // Classes page
    @GetMapping("/classes")
    public String classesPage(Model model) {
        model.addAttribute("pageTitle", "Classes");
        model.addAttribute("title", "Classes - Moktob Management System");
        return "classes/index";
    }

    // Attendance page
    @GetMapping("/attendance")
    public String attendancePage(Model model) {
        model.addAttribute("pageTitle", "Attendance");
        model.addAttribute("title", "Attendance - Moktob Management System");
        return "attendance/index";
    }

    // Assessments page
    @GetMapping("/assessments")
    public String assessmentsPage(Model model) {
        model.addAttribute("pageTitle", "Assessments");
        model.addAttribute("title", "Assessments - Moktob Management System");
        return "assessments/index";
    }

    // Payments page
    @GetMapping("/payments")
    public String paymentsPage(Model model) {
        model.addAttribute("pageTitle", "Payments");
        model.addAttribute("title", "Payments - Moktob Management System");
        return "payments/index";
    }

    // Reports page
    @GetMapping("/reports")
    public String reportsPage(Model model) {
        model.addAttribute("pageTitle", "Reports");
        model.addAttribute("title", "Reports - Moktob Management System");
        return "reports/index";
    }

    // Profile page
    @GetMapping("/profile")
    public String profilePage(Model model) {
        model.addAttribute("pageTitle", "Profile");
        model.addAttribute("title", "Profile - Moktob Management System");
        return "profile/index";
    }

    // Settings page
    @GetMapping("/settings")
    public String settingsPage(Model model) {
        model.addAttribute("pageTitle", "Settings");
        model.addAttribute("title", "Settings - Moktob Management System");
        return "settings/index";
    }

    // Logout
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/moktob/login";
    }

    // Error pages
    @GetMapping("/error")
    public String errorPage(Model model) {
        model.addAttribute("pageTitle", "Error");
        model.addAttribute("title", "Error - Moktob Management System");
        return "error/index";
    }

    // Forgot password page
    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        model.addAttribute("pageTitle", "Forgot Password");
        model.addAttribute("title", "Forgot Password - Moktob Management System");
        return "auth/forgot-password";
    }
}
