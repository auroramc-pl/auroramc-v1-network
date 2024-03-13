package pl.auroramc.registry.user;

import java.util.UUID;

interface UserRepository {

  User findUserByUniqueId(final UUID uniqueId);

  User findUserByUsername(final String username);

  void createUser(final User user);

  void updateUser(final User user);
}
