package br.com.leticiav.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.leticiav.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var servletPath = request.getServletPath();

        if (servletPath.startsWith("/tasks/")) {

            // Pega auth
            var authorization = request.getHeader("Authorization");

            if (authorization == null || !authorization.startsWith("Basic ")) {
                response.sendError(401, "Missing or invalid Authorization header");
                return;
            }

            var authEncoded = authorization.substring("Basic".length()).trim();

            byte[] authDecode = Base64.getDecoder().decode(authEncoded);

            var authString = new String(authDecode);

            String[] credentials = authString.split(":", 2);
            if (credentials.length != 2) {
                response.sendError(400, "Invalid Authorization format");
                return;
            }
            String username = credentials[0];
            String password = credentials[1];

            // Valida usuário
            var user = this.userRepository.findByUsername(username);
            if (user == null || user.getPassword() == null) {
                response.sendError(401, "Unauthorized: Invalid credentials");
                return;
            }

            // Valida senha
            var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
            if (passwordVerify.verified) {
                //liberado
                request.setAttribute("idUser", user.getId());
                filterChain.doFilter(request, response);
            } else {
                response.sendError(401, "Unauthorized: Invalid password");
            }

        } else {
            filterChain.doFilter(request, response);
        }
    }
}
