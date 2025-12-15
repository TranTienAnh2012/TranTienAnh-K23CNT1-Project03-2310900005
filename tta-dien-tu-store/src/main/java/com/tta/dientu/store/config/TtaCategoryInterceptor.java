package com.tta.dientu.store.config;

import com.tta.dientu.store.repository.TtaDanhMucRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@RequiredArgsConstructor
public class TtaCategoryInterceptor implements HandlerInterceptor {

    private final TtaDanhMucRepository ttaDanhMucRepository;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && !modelAndView.getViewName().startsWith("redirect:")) {
            // Inject categories into all views
            modelAndView.addObject("globalDanhMucs", ttaDanhMucRepository.findAllByOrderByTtaTenDanhMucAsc());
            modelAndView.addObject("ttaWebsitePhone", "0345862097");
        }
    }
}
