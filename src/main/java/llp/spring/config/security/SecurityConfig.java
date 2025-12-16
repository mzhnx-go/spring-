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
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // 启用方法级别的权限认证
public class SecurityConfig extends WebSecurityConfigurerAdapter {/////权限配置

@Autowired
private MyUserDetailsService myUserDetailsService;

    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // 1、自定义用户访问控制
                .antMatchers("/images/**","/article/articleSearch","/article/getIndexDetail",
                        "/article/getPageOfArticle","/article/getIndexData1",
                        "/article/getArticleAndfirstPageCommentByArticleId",
                        "/article/selectById","/comment/getPageCommentByArticleId").permitAll()// 任意访问
                .antMatchers("/article/upload","/article/deleteById","/article/getPageOfArticleVO",
                        "/article/publishArticle").hasRole("admin")// 管理员权限
                .antMatchers("/comment/insert").hasRole("common")//注册会员权限
                .anyRequest().authenticated()
                .and()
                // 2、自定义用户登录控制
                .formLogin()
                .failureHandler(myAuthenticationFailureHandler) // 权限验证失败的处理
                .successHandler(myAuthenticationSuccessHandler) // 权限验证成功的处理
                .permitAll() // 验证通过后可以访问任意
                .and()
                .logout() // 注销用户
                .logoutUrl("/logout") // 注销网址
                .logoutSuccessHandler(new LogoutSuccessHandler() { // 注销用户成功时执行
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                        request.getSession().removeAttribute("user");
                        response.setContentType("application/json;charset=utf-8");
                        response.getWriter().write(objectMapper.writeValueAsString(
                                new Result(true, "登出成功")
                        ));
                    }
                })
                .permitAll()
                .and().csrf().disable() // 禁用网站csrf攻击防御
        // 防止错误: Refused to display in a frame because it set 'X-Frame-Options' to 'DENY'
        ;
        http.headers().frameOptions().disable();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();// 密码加密策略
    }
}

