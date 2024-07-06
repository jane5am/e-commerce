package sparta.userservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sparta.userservice.domain.User;
import sparta.userservice.user.UserRepository;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        try {
            Integer userIdInt = Integer.parseInt(userId);
            User user = userRepository.findById(userIdInt)
                    .orElseThrow(() -> new UsernameNotFoundException("Not Found " + userId));

            return new UserDetailsImpl(user);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid userId format: " + userId);
        }
    }
}