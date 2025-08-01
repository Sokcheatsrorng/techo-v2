package co.istad.techco.techco.features.user;

import co.istad.techco.techco.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserCleanupService {

    private final UserRepository userRepository;

    @Scheduled(fixedRate = 60000)
    void deleteUnverifiedUsers() {

        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);

        List<User> usersToDeleted = userRepository.findAll().stream()
                .filter(user -> ((!user.isEmailVerified()))
                && user.getCreatedAt().isBefore(fiveMinutesAgo) )
                .toList();
        userRepository.deleteAll(usersToDeleted);
        System.out.println("Deleted " + usersToDeleted.size() + " unverified users.");
    }

}
