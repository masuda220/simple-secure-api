package example.application;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    public String user() { ;
        return SessionContext.user();
    }
}
