package com.community.community.controller;

import com.community.community.dto.PaginationDTO;
import com.community.community.model.User;
import com.community.community.service.NotificationService;
import com.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired(required = false)
    private QuestionService questionService;

    @Autowired(required = false)
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name = "action") String action,
                          Model model,
                          HttpServletRequest request,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "5") Integer size) {
        //这里通过拦截器判断cookie
        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            return "redirect:/";
        }
        if ("questions".equals(action)) {
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");

            PaginationDTO paginationDTO = questionService.list(user.getId(), page, size);
            model.addAttribute("pagination", paginationDTO);
        }
        else if ("replies".equals(action)) {
            PaginationDTO paginationDTO = notificationService.list(user.getId(), page, size);
            Long unreadCount = notificationService.unreadCount(user.getId());
            model.addAttribute("pagination", paginationDTO);
            model.addAttribute("unreadCount", unreadCount);
            model.addAttribute("section", "replies");
            model.addAttribute("sectionName", "最新回复");
        }

        return "profile";
    }
}
