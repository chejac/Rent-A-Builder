package com.construction.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
	
	private AuthenticationManager authManager;
	
	private UserDetailsService userDetailsService;
	
	private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);
	
	@Autowired
	public SecurityService(AuthenticationManager authManager, 
			UserDetailsService userDetailsService) {
		this.authManager = authManager;
		this.userDetailsService = userDetailsService;
	}
	
    public void autoLogin(String username, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authToken = 
        		new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        
        authManager.authenticate(authToken);
        if (authToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authToken);
            logger.debug(String.format("Auto login for %s is successful!", username));
        }
    }

}
