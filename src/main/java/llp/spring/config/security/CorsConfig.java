package llp.spring.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 全局跨域配置（适配Spring Boot 2.6.13）
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许前端域名（本地开发）
        config.addAllowedOrigin("http://localhost:5173");
        // 允许携带Cookie（登录态）
        config.setAllowCredentials(true);
        // 允许所有HTTP方法
        config.addAllowedMethod("*");
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 预检请求缓存时间（减少OPTIONS请求）
        config.setMaxAge(3600L);

        // 应用到所有接口
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}