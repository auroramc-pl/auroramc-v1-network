package pl.auroramc.commons.config.serdes.juliet;

import com.zaxxer.hikari.HikariConfig;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.jetbrains.annotations.NotNull;

class HikariConfigSerializer implements ObjectSerializer<HikariConfig> {

  private static final String DRIVER_CLASS_NAME_VARIABLE_KEY = "driver-class-name";
  private static final String JDBC_URL_VARIABLE_KEY = "jdbc-url";
  private static final String USERNAME_VARIABLE_KEY = "username";
  private static final String PASSWORD_VARIABLE_KEY = "password";

  HikariConfigSerializer() {}

  @Override
  public boolean supports(final @NotNull Class<? super HikariConfig> type) {
    return HikariConfig.class.isAssignableFrom(type);
  }

  @Override
  public void serialize(
      final @NotNull HikariConfig object,
      final @NotNull SerializationData data,
      final @NotNull GenericsDeclaration generics) {
    data.add(JDBC_URL_VARIABLE_KEY, object.getJdbcUrl());

    final boolean whetherDriverClassNameIsSpecified = object.getDriverClassName() != null;
    if (whetherDriverClassNameIsSpecified) {
      data.add(DRIVER_CLASS_NAME_VARIABLE_KEY, object.getDriverClassName());
    }

    final boolean whetherUsernameIsSpecified = object.getUsername() != null;
    if (whetherUsernameIsSpecified) {
      data.add(USERNAME_VARIABLE_KEY, object.getUsername());
    }

    final boolean whetherPasswordIsSpecified = object.getPassword() != null;
    if (whetherPasswordIsSpecified) {
      data.add(PASSWORD_VARIABLE_KEY, object.getPassword());
    }
  }

  @Override
  public HikariConfig deserialize(
      final @NotNull DeserializationData data, final @NotNull GenericsDeclaration generics) {
    final HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl(data.get(JDBC_URL_VARIABLE_KEY, String.class));

    final boolean whetherDriverClassNameIsSpecified =
        data.containsKey(DRIVER_CLASS_NAME_VARIABLE_KEY);
    if (whetherDriverClassNameIsSpecified) {
      hikariConfig.setDriverClassName(data.get(DRIVER_CLASS_NAME_VARIABLE_KEY, String.class));
    }

    final boolean whetherUsernameIsSpecified = data.containsKey(USERNAME_VARIABLE_KEY);
    if (whetherUsernameIsSpecified) {
      hikariConfig.setUsername(data.get(USERNAME_VARIABLE_KEY, String.class));
    }

    final boolean whetherPasswordIsSpecified = data.containsKey(PASSWORD_VARIABLE_KEY);
    if (whetherPasswordIsSpecified) {
      hikariConfig.setPassword(data.get(PASSWORD_VARIABLE_KEY, String.class));
    }

    return hikariConfig;
  }
}
