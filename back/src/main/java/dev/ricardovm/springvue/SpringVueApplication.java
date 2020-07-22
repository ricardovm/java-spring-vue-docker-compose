package dev.ricardovm.springvue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@SpringBootApplication
public class SpringVueApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringVueApplication.class, args);
	}

	@Configuration
	public static class WebConfig implements WebMvcConfigurer {

		@Autowired
		private ApplicationContext applicationContext;

		@Bean
		public FilterRegistrationBean corsFilter() {
			CorsConfiguration config = new CorsConfiguration();
			config.setAllowCredentials(true);
			config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept"));
			config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));

			CorsConfigurationSource source = new CorsConfigurationSource() {
				@Override
				public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
					CorsConfiguration newConfig = new CorsConfiguration(config);
					var origin = request.getHeader("Origin");
					newConfig.setAllowedOrigins(Arrays.asList(origin != null ? origin : "*"));
					return newConfig;
				}
			};

			return new FilterRegistrationBean(new CorsFilter(source));
		}
	}


}
