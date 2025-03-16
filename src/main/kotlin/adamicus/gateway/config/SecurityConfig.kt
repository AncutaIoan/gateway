//package adamicus.gateway.config
//
//import org.springframework.context.annotation.Configuration
//
//@Configuration
//@EnableWebSecurity
//class SecurityConfig : WebSecurityConfigurerAdapter() {
//
//    override fun configure(http: HttpSecurity) {
//        http
//            .authorizeRequests()
//            .antMatchers("/ping", "/user-service/**").permitAll() // Permit these routes
//            .anyRequest().authenticated()  // Authenticate others if needed
//            .and()
//            .csrf().disable() // Disable CSRF if required for stateless services
//    }
//}