package com.backendsems.iam.interfaces.acl;

import com.backendsems.iam.domain.model.aggregates.User;
import com.backendsems.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * IamContextFacadeImpl
 * Implementación del facade para comunicación con el contexto IAM.
 */
@Service
public class IamContextFacadeImpl implements IamContextFacade {
    private static final Logger logger = LoggerFactory.getLogger(IamContextFacadeImpl.class);
    private final UserRepository userRepository;

    public IamContextFacadeImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean updateUserEmail(Long userId, String newEmail) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                logger.warn("User with ID {} not found", userId);
                return false;
            }

            User user = userOptional.get();
            user.updateEmail(newEmail);
            userRepository.save(user);
            
            logger.info("Successfully updated email for user ID {}", userId);
            return true;
        } catch (Exception e) {
            logger.error("Error updating email for user ID {}: {}", userId, e.getMessage());
            return false;
        }
    }
}
