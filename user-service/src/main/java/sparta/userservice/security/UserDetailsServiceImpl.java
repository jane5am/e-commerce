package sparta.userservice.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sparta.userservice.domain.User;
import sparta.userservice.user.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        log.info("Loading user by userId: {}", userId);
        try {
            Integer userIdInt = Integer.parseInt(userId);
            User user = userRepository.findById(userIdInt)
                    .orElseThrow(() -> new UsernameNotFoundException("Not Found " + userId));
            log.info("User loaded: {}", user);
            return new UserDetailsImpl(user);
        } catch (Exception e) {
            log.error("Error loading user by userId: {}", userId, e);
            throw new UsernameNotFoundException("Invalid userId format: " + userId);
        }
    }
}
