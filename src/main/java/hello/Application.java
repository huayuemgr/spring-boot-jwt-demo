package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static hello.JwtUtil.HEADER_STRING;
import static hello.JwtUtil.TOKEN_PREFIX;

/*
	https://auth0.com/blog/securing-spring-boot-with-jwts/
	https://github.com/auth0-blog/spring-boot-jwts
	https://github.com/szerhusenBC/jwt-spring-security-demo
*/
	
@SpringBootApplication
@RestController
public class Application {

    @GetMapping("/protected")
    public @ResponseBody Object hellWorld() {
        return "Hello World! This is a protected api";
    }

    @Bean
    public FilterRegistrationBean jwtFilter() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                "/*.html", "/", "/login");
        registrationBean.setFilter(filter);
        return registrationBean;
    }

    @PostMapping("/login")
    public void login(HttpServletResponse response,
                      @RequestBody final AccountCredentials credentials) throws IOException {
        //here we just have one hardcoded username=admin and password=admin
        //TODO add your own user validation code here
        if(validCredentials(credentials)) {
            String jwt = JwtUtil.generateToken(credentials.username);
            response.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + jwt);
        }else
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Wrong credentials");
    }

    private boolean validCredentials(AccountCredentials credentials) {
        return "admin".equals(credentials.username)
                && "admin".equals(credentials.password);
    }


    public static class AccountCredentials {
        public String username;
        public String password;
    }
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}