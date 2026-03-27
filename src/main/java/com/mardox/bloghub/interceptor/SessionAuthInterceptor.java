package com.mardox.bloghub.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

public class SessionAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        System.out.println("Path: "+request.getRequestURL());
        System.out.println("Method: "+request.getMethod());
        System.out.println("session present ?: "+(session!=null));

        if(session != null){
            System.out.println("session id: "+session.getId());
            System.out.println("user id: "+session.getAttribute("userId"));
        }

        if(session == null || session.getAttribute("userId") == null){
            response.setStatus(401);//unauthorized
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.write("{\"error\" : \"Please Login First\"}");
            return false;
        }

        Long userId = (Long)session.getAttribute("userId");
        String userRole = (String)session.getAttribute("userRole");

        request.setAttribute("currentUserId",userId);
        request.setAttribute("currentUserRole",userRole);

        //

        return true;
    }
}
