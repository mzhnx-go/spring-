package llp.spring.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import llp.spring.tools.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security 核心配置
 * 适配你的 Result 工具类（@NoArgsConstructor + 默认success=true）
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 自定义401未授权返回（适配你的Result类）
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            // 跨域头（必须）
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            // 401响应配置
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            // 适配你的Result类（使用带参构造器，覆盖默认success=true）
            Result result = new Result(false, "请先登录（仅发表评论需要登录）");
            response.getWriter().write(objectMapper.writeValueAsString(result));
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 启用CORS
                .cors().and()
                // 权限控制
                .authorizeRequests()
                // 放行查询接口（未登录可看评论）
                .antMatchers(
                        "/images/**",
                        "/article/articleSearch","/article/getIndexDetail",
                        "/article/getPageOfArticle","/article/getIndexData1",
                        "/article/getArticleAndfirstPageCommentByArticleId",
                        "/article/selectById",
                        "/comment/getPageCommentByArticleId",
                        "/comment/getAPageCommentByArticleId"
                ).permitAll()
                // 管理员接口
                .antMatchers("/article/upload","/article/deleteById","/article/getPageOfArticleVO",
                        "/article/publishArticle").hasRole("admin")
                // 发表评论需要登录
                .antMatchers("/comment/insert").hasRole("common")
                .anyRequest().authenticated()
                .and()
                // 登录配置
                .formLogin()
                .failureHandler(myAuthenticationFailureHandler)
                .successHandler(myAuthenticationSuccessHandler)
                .permitAll()
                .and()
                // 注销配置（适配你的Result类）
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                        // 跨域头
                        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
                        response.setHeader("Access-Control-Allow-Credentials", "true");
                        // 注销成功返回（使用带参构造器）
                        response.setContentType("application/json;charset=utf-8");
                        Result result = new Result(true, "登出成功");
                        response.getWriter().write(objectMapper.writeValueAsString(result));

                        request.getSession().removeAttribute("user");
                    }
                })
                .permitAll()
                // 异常处理
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .and()
                // 禁用CSRF
                .csrf().disable();

        http.headers().frameOptions().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}