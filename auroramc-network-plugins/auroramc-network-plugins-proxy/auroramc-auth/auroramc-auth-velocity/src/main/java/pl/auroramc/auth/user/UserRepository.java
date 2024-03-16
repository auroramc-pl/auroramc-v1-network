package pl.auroramc.auth.user;

import java.util.UUID;

public interface UserRepository {

  User findUserByUniqueId(final UUID uniqueId);

  User findUserByUsername(final String username);

  User findUserByEmail(final String email);

  void createUser(final User user);

  void updateUser(final User user);

  void deleteUser(final User user);
}
