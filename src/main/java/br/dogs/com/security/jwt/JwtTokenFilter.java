package br.dogs.com.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class JwtTokenFilter extends GenericFilterBean {
	
	Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		try {
			
			String token = jwtTokenProvider.resolveToken( (HttpServletRequest) request);
			
			if(token != null && jwtTokenProvider.validateToken(token)) {
				Authentication auth = jwtTokenProvider.getAuthentication(token);
				
				if(auth != null) {
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
				
			}
			
		} catch (Exception e) {
			logger.warn(e.getMessage());
		} finally {
			chain.doFilter(request, response);
		}
		
	}
	
}
