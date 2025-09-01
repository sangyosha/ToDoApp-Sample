package todo_demo.service;

import todo_demo.entity.User;
import todo_demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public User register(String username, String password) {
        // すでにユーザーが存在するか確認
        if (repo.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("ユーザー名は既に使用されています");
        }        
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        return repo.save(user);
    }
}
