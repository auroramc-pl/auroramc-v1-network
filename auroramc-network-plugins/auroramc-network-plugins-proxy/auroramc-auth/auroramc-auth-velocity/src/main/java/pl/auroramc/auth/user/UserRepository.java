package pl.auroramc.auth.user;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  Optional<User> findUserByUniqueId(final UUID uniqueId);

  Optional<User> findUserByUsername(final String username);

  Optional<User> findUserByEmail(final String email);

  void createUser(final User user);

  void updateUser(final User user);

  void deleteUser(final User user);
}
