package br.dogs.com.security.jwt;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import br.dogs.com.exceptions.InvalidJwtAuthenticationException;
import br.dogs.com.model.entities.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtTokenProvider {
	
	@Value("${jwt_secret}")
	private String jwtSecret;
	
	private SecretKey secretKey;
	
	@Value("${jwt_validity_miliseconds}")
	private long validityMiliseconds;
	
	@PostConstruct
	private void init() {
		this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
	}
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	public String createToken(Usuario usuario) {
		
		Claims claims = Jwts.claims().setSubject(usuario.getEmail());
		claims.put("role", usuario.getAuthority());
		
		Date now = new Date();
		Date validity = new Date(now.getTime() + validityMiliseconds);
		
		String token = Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(validity)
				.signWith(secretKey)
				.compact();
		
		
		return token;
	}
	
	public Authentication getAuthentication(String token) {
		
		UserDetails userDetails = userDetailsService.loadUserByUsername(getUsernameByToken(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
		
	}
	
	private String getUsernameByToken(String token) {
		return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
	}
	
	public String resolveToken(HttpServletRequest request) {
		
		String bearerToken = request.getHeader("Authorization");
		
		if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		
		return null;
		
	}
	
	public boolean validateToken(String token) {
		
		try {
			
			Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
			
		} catch (ExpiredJwtException e) {
			throw new InvalidJwtAuthenticationException("Token expirado.");
		} catch (MalformedJwtException e) {
			throw new InvalidJwtAuthenticationException("Token informado não segue o formato correspondente.");
		} catch (Exception e) {
			throw new InvalidJwtAuthenticationException("Token inválido.");
		} 
		
		return true;
		
	}
	
}
