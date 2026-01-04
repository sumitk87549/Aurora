package com.ecomm.AuroraFlames.security;

import com.ecomm.AuroraFlames.config.JwtUtil;
import com.ecomm.AuroraFlames.entity.User;
import com.ecomm.AuroraFlames.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        // Fallback if name is null (e.g. some providers might not send "name")
        if (name == null) {
            String firstName = oauthUser.getAttribute("given_name");
            String lastName = oauthUser.getAttribute("family_name");
            if (firstName != null && lastName != null) {
                name = firstName + " " + lastName;
            } else if (firstName != null) {
                name = firstName;
            } else {
                name = "User";
            }
        }

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setFirstName(name.split(" ")[0]);
            user.setLastName(name.contains(" ") ? name.substring(name.indexOf(" ") + 1) : "User");
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // Random password for OAuth users
            user.setRole(User.Role.USER);
            user.setPhone(""); // Phone might not be available
            userRepository.save(user);
        }

        String token = jwtUtil.generateToken(email);

        // Redirect to frontend with token
        getRedirectStrategy().sendRedirect(request, response, "http://localhost:4200/login?token=" + token);
    }
}
