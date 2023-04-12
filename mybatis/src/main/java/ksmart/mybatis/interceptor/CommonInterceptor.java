package ksmart.mybatis.interceptor;

import java.util.Set;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component //컴포넌트가 붙은 객체들은 bean으로 관리된다.
public class CommonInterceptor implements HandlerInterceptor{ 
	
	private static final Logger log = LoggerFactory.getLogger(CommonInterceptor.class);

	/**
	 * HandlerMapping이 핸들러 객체를 결정한 후 HandlerAdapter가 핸들러를 호출하기 전에 호출되는 메소드
	 * return true일 경우: 핸들러메소드 실행, false일 경우: 핸들러까지 진입 금지
	 */
    @Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
    	// 파라미터 값을 확인
    	Set<String> paramKeySet = request.getParameterMap().keySet(); //쿼리스트링을 key, value 형식의 Map으로 반환하여 Set 컬렉션(저장순서 유지x)에 담는다.
    	
    	StringJoiner param = new StringJoiner(", "); // StringJoiner 결과에 해당하는 것을 구분자를 넣어 문자열로 표현한다.     	
    	
    	// 예시) memberId: id001, memberPw: pw001
    	for(String key : paramKeySet) {
    		param.add(key + ":" + request.getParameter(key));
    	}
    	
    	log.info("ACCESS INFO=============================================");
    	log.info("PORT		::::::::	{}", request.getLocalPort());
    	log.info("ServerName		::::::::	{}", request.getServerName());
    	log.info("HTTPMethod		::::::::	{}", request.getMethod());
    	log.info("URI			::::::::	{}", request.getRequestURI());
    	log.info("CLIENT IP		::::::::	{}", request.getRemoteAddr());
    	log.info("Parameter		::::::::	{}", param);
    	log.info("ACCESS INFO=============================================");
    
    	
		return HandlerInterceptor.super.preHandle(request, response, handler); //return true와 동일한 내용
	}
	
    /**
     * HandlerAdapter가 실제로 핸들러를 호출한 후 DispatcherServlet이 뷰를 전달하기 전에 호출되는 메소드
     */
	@Override
		public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
				ModelAndView modelAndView) throws Exception { //Model과 View의 컨트롤이 가능하다.
			// TODO Auto-generated method stub
			HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
		}
	
	/**
     * HandlerAdapter가 실제로 핸들러를 호출한 후 DispatcherServlet이 뷰를 랜더링한 후에 호출되는 메소드
     */
	@Override
		public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
				throws Exception {
			// TODO Auto-generated method stub
			HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
		}
	
}
