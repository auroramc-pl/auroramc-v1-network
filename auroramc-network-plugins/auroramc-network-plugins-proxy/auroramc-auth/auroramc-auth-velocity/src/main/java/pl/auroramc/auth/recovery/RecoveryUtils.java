package pl.auroramc.auth.recovery;

import java.util.concurrent.ThreadLocalRandom;

final class RecoveryUtils {

  private static final String RECOVERY_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final int RECOVERY_CODE_LENGTH = 6;

  private RecoveryUtils() {

  }

  static String generateRecoveryCode(final int length) {
    final StringBuilder builder = new StringBuilder();
    for (int index = 0; index < length; index++) {
      builder.append(RECOVERY_CODE_CHARS.charAt(ThreadLocalRandom.current().nextInt(RECOVERY_CODE_CHARS.length())));
    }
    return builder.toString();
  }

  static String generateRecoveryCode() {
    return generateRecoveryCode(RECOVERY_CODE_LENGTH);
  }
}
