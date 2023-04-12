package ksmart.mybatis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ksmart.mybatis.interceptor.CommonInterceptor;
import ksmart.mybatis.interceptor.LoginInterceptor;

@Configuration //bean 등록 설정
public class WebConfig implements WebMvcConfigurer{
		
	private final CommonInterceptor commonInterceptor;
	private final LoginInterceptor loginInterceptor;
	
	public WebConfig(CommonInterceptor commonInterceptor, LoginInterceptor loginInterceptor) {
		this.commonInterceptor = commonInterceptor;
		this.loginInterceptor = loginInterceptor;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) { //인터셉터 등록 
		// 로깅 인터셉터 등록
		registry.addInterceptor(commonInterceptor)
				.addPathPatterns("/**") //어떤 패턴일 때 commonInterceptor를 적용할 것인가?
				.excludePathPatterns("/css/**") //인터셉터를 제외할 패턴을 등록한다.
				.excludePathPatterns("/js/**")
				.excludePathPatterns("/favicon.ico");
		
		// 로그인 인터셉터 등록
		registry.addInterceptor(loginInterceptor)
				.addPathPatterns("/**") // 모든 주소 요청에 대해 loginInterceptor를 거쳐야 한다.
				.excludePathPatterns("/css/**")
				.excludePathPatterns("/js/**")
				.excludePathPatterns("/favicon.ico")
				.excludePathPatterns("/")
				.excludePathPatterns("/member/addMember")
				.excludePathPatterns("/member/idCheck")
				.excludePathPatterns("/member/login")
				.excludePathPatterns("/member/logout");
		
		
		

		WebMvcConfigurer.super.addInterceptors(registry);
	}
}
