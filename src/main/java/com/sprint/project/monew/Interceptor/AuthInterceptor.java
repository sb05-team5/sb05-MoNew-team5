package com.sprint.project.monew.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AuthInterceptor implements HandlerInterceptor {

  private static final String USER_HEADER = "MoNew-Request-User-ID";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    String userId = request.getHeader(USER_HEADER);
    if (userId == null || userId.isBlank()) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다");
      return false;
    }
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {
    if (!response.isCommitted()) {
      String userId = request.getHeader(USER_HEADER);
      response.setHeader(USER_HEADER, userId);
    }
  }
}
