package br.dogs.com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.dogs.com.security.jwt.JwtAuthenticationEntryPoint;
import br.dogs.com.security.jwt.JwtConfigurer;
import br.dogs.com.security.jwt.JwtTokenProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private String prefix_api = "/api/v1";
	
	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); 
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http
			.httpBasic().disable()
			.csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
				.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
			.and()
				.authorizeRequests()
					.antMatchers(prefix_api + "/usuario/login").permitAll()
					.antMatchers(prefix_api + "/usuario/social-login").permitAll()
					.antMatchers(prefix_api + "/usuario/registrar").permitAll()
					.antMatchers(prefix_api + "/usuario/dogwalker").hasAnyRole("ADMIN","TUTOR")
					.antMatchers(prefix_api + "/usuario/tutor").hasAnyRole("ADMIN")
					.antMatchers(prefix_api + "/usuario/todos").hasAnyRole("ADMIN")
					.antMatchers(HttpMethod.PUT, prefix_api + "/usuario/dados").hasAnyRole("ADMIN","TUTOR","DOGWALKER")
					.antMatchers(prefix_api + "/cachorro").hasAnyRole("TUTOR")
					.antMatchers(prefix_api + "/cachorro/{\\d+}").hasAnyRole("TUTOR")
					.antMatchers(HttpMethod.GET, prefix_api + "/raca").hasAnyRole("ADMIN","TUTOR")
					.antMatchers(HttpMethod.POST, prefix_api + "/raca").hasAnyRole("ADMIN")
					.antMatchers(HttpMethod.PUT, prefix_api + "/raca/{\\d+}").hasAnyRole("ADMIN")
					.antMatchers(HttpMethod.DELETE, prefix_api + "/raca/{\\d+}").hasAnyRole("ADMIN")
					.antMatchers(HttpMethod.GET, prefix_api + "/porte").hasAnyRole("ADMIN","TUTOR")
					.antMatchers(HttpMethod.POST, prefix_api + "/porte").hasAnyRole("ADMIN")
					.antMatchers(HttpMethod.PUT, prefix_api + "/porte/{\\d+}").hasAnyRole("ADMIN")
					.antMatchers(HttpMethod.DELETE, prefix_api + "/porte/{\\d+}").hasAnyRole("ADMIN")
					.antMatchers(prefix_api + "/configuracao-base").hasAnyRole("ADMIN")
					.antMatchers(prefix_api + "/configuracao-base/{\\d+}").hasAnyRole("ADMIN")
					.antMatchers(prefix_api + "/reclamacao-sugestao").hasAnyRole("DOGWALKER","TUTOR")
					.antMatchers(prefix_api + "/reclamacao-sugestao/{\\d+}").hasAnyRole("DOGWALKER","TUTOR")
					.antMatchers(prefix_api + "/qualificacao").hasAnyRole("DOGWALKER")
					.antMatchers(prefix_api + "/qualificacao/{\\d+}").hasAnyRole("DOGWALKER")
					.antMatchers(HttpMethod.GET, prefix_api + "/forma-de-pagamento").hasAnyRole("ADMIN","TUTOR")
					.antMatchers(prefix_api + "/forma-de-pagamento/{\\d+}").hasAnyRole("ADMIN")
					.antMatchers(HttpMethod.GET, prefix_api + "/forma-de-pagamento").hasAnyRole("TUTOR","ADMIN")
					.antMatchers(HttpMethod.POST, prefix_api + "/forma-de-pagamento").hasAnyRole("ADMIN")
					.antMatchers(HttpMethod.GET, prefix_api + "/ticket").hasAnyRole("ADMIN","TUTOR")
					.antMatchers(HttpMethod.GET, prefix_api + "/ticket/{\\d+}").hasAnyRole("ADMIN","TUTOR")
					.antMatchers(HttpMethod.POST, prefix_api + "/ticket").hasAnyRole("TUTOR")
					.antMatchers(HttpMethod.PUT, prefix_api + "/ticket/{\\d+}").hasAnyRole("TUTOR")
					.antMatchers(HttpMethod.DELETE, prefix_api + "/ticket/{\\d+}").hasAnyRole("TUTOR")
					.antMatchers(HttpMethod.GET, prefix_api + "/estado").hasAnyRole("ADMIN","TUTOR","DOGWALKER")
					.antMatchers(HttpMethod.GET, prefix_api + "/cidade/{\\d+}").hasAnyRole("ADMIN","TUTOR","DOGWALKER")
					.antMatchers(prefix_api + "/configuracao-horario").hasAnyRole("DOGWALKER")
					.antMatchers(prefix_api + "/configuracao-horario/{\\d+}").hasAnyRole("DOGWALKER")
					.antMatchers(HttpMethod.POST, prefix_api + "/horario/disponibilidade").hasAnyRole("TUTOR")
					.antMatchers(HttpMethod.POST, prefix_api + "/callback/safe2pay").permitAll()
					.antMatchers(HttpMethod.POST, prefix_api + "/passeio").hasAnyRole("TUTOR")
					.antMatchers(HttpMethod.GET, prefix_api + "/passeio").hasAnyRole("DOGWALKER","TUTOR")
					.antMatchers(HttpMethod.GET, prefix_api + "/passeio/{\\d+}").hasAnyRole("DOGWALKER","TUTOR")
					.antMatchers(prefix_api + "/passeio/{\\d+}/aceitar").hasAnyRole("DOGWALKER")
					.antMatchers(prefix_api + "/passeio/{\\d+}/recusar").hasAnyRole("DOGWALKER")
					.antMatchers(prefix_api + "/passeio/{\\d+}/iniciar").hasAnyRole("DOGWALKER")
					.antMatchers(prefix_api + "/passeio/{\\d+}/finalizar").hasAnyRole("DOGWALKER")
					.antMatchers(prefix_api + "/passeio/lat-long").hasAnyRole("DOGWALKER")
					.antMatchers(prefix_api + "/passeio/creditar-saldo/{\\d+}").hasAnyRole("ADMIN")
					.antMatchers(prefix_api + "/deposito").hasAnyRole("DOGWALKER","ADMIN")
					.antMatchers(HttpMethod.GET, prefix_api + "/saldo").hasAnyRole("DOGWALKER")
					.antMatchers(prefix_api + "/saldo/solicitar-deposito").hasAnyRole("DOGWALKER")
					.antMatchers(prefix_api + "/saldo/deposito/{\\d+}").hasAnyRole("DOGWALKER")
					.antMatchers(prefix_api + "/passeio/localizacao/posicao-atual/{\\d+}").hasAnyRole("TUTOR")
					.antMatchers(prefix_api + "/passeio/localizacao/posicao-completa/{\\d+}").hasAnyRole("TUTOR","DOGWALKER")
					.antMatchers(prefix_api + "/usuario/image/{\\d+}").permitAll()
					.antMatchers(HttpMethod.POST, prefix_api + "/avaliacao").hasAnyRole("TUTOR")
					.antMatchers(HttpMethod.PUT, prefix_api + "/avaliacao/{\\d+}").hasAnyRole("TUTOR")
					.antMatchers(HttpMethod.DELETE, prefix_api + "/avaliacao/{\\d+}").hasAnyRole("TUTOR","ADMIN")
					.antMatchers(HttpMethod.PUT, prefix_api + "/ticket/aprovar-pagamento/{\\d+}").hasAnyRole("ADMIN")
				.antMatchers("/api-docs/**").permitAll()
				.antMatchers("swagger-ui.html**").permitAll()
				.antMatchers("/images/**").permitAll()
				.antMatchers("/api/**").authenticated()
				
			.and()
			.apply(new JwtConfigurer(jwtTokenProvider));
		
	}
	
}
