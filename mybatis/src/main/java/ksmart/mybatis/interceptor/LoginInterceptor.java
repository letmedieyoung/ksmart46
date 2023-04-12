package ksmart.mybatis.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ksmart.mybatis.dto.Member;
import ksmart.mybatis.mapper.MemberMapper;

@Component
public class LoginInterceptor implements HandlerInterceptor{
	
	private final MemberMapper memberMapper;
	
	public LoginInterceptor(MemberMapper memberMapper) {
		this.memberMapper = memberMapper;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession();
		String sessionId = (String) session.getAttribute("SID"); //SID를 String으로 형변환하여 변수 sessionId에 담는다
		
		Cookie cookie = WebUtils.getCookie(request, "loginKeepId"); //WebUtils 웹에서 자주 사용하는 API를 스프링부트에서 모아놓은 것
		
		if(cookie != null) { //쿠키값이 null이 아니라면
			String loginId = cookie.getValue(); //쿠키의 value값을 가져와 loginId에 담는다.
			if(sessionId == null) { //쿠키값이 null이라면
				Member memberInfo = memberMapper.getMemberInfoById(loginId); //memberInfo를 조회
				if(memberInfo != null) {
					String memberLevel = memberInfo.getMemberLevel();
					String memberName = memberInfo.getMemberName();
					session.setAttribute("SID", loginId);
					session.setAttribute("SLEVEL", memberLevel);
					session.setAttribute("SNAME", memberName);
				}
			}
		}
		
		if(sessionId == null) { //sessionId가 null이라면
			response.sendRedirect("/member/login"); // '/member/login'페이지로 이동시킨다.
			return false;
		}else {
			String memberLevel = (String) session.getAttribute("SLEVEL"); // 세션레벨을 변수 memberLevel에 담는다.
			String requestURI = request.getRequestURI();
			
			if("2".equals(memberLevel)) { //memberLevel이 2일 때,
				if(requestURI.indexOf("member/") > -1) { //지정된 요소(member/)를 찾을 수 있는 첫 번째 인덱스를 반환하고 존재하지 않으면 -1을 반환.
					response.sendRedirect("/"); // 메인페이지로 리다이렉트.
					return false;
				}
			}else if("3".equals(memberLevel)) {
				if(  requestURI.indexOf("/member/") > -1
				   ||requestURI.indexOf("goods/add") > -1
				   ||requestURI.indexOf("goods/modify") > -1
				   ||requestURI.indexOf("goods/remove") > -1) {
					response.sendRedirect("/");
					return false;
				}
			}
		}		
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}
	
}
