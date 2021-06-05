package example.application;

import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

public class SessionContext {
    public static String user() {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        return principal.getName();
    }
}
