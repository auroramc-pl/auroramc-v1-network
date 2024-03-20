package pl.auroramc.auth.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.commons.message.MutableMessage;

public class MutableMessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage availableSchematicsSuggestion =
      MutableMessage.of("<red>Poprawne użycie: <yellow><newline>{schematics}");

  public MutableMessage executionOfCommandIsNotPermitted =
      MutableMessage.of("<red>Nie posiadasz wystarczających uprawnień aby użyć tej komendy.");

  public MutableMessage executionFromConsoleIsUnsupported =
      MutableMessage.of("<red>Nie możesz użyć tej konsoli z poziomu konsoli.");

  public MutableMessage notAllowedBecauseOfPremiumAccount =
      MutableMessage.of(
          "<red>Nie możesz tego zrobić, ponieważ posiadasz włączone automatyczne uwierzytelnianie!");

  public MutableMessage notAllowedBecauseOfRegisteredAccount =
      MutableMessage.of("<red>Nie możesz tego zrobić, ponieważ jesteś już zarejestrowany!");

  public MutableMessage notAllowedBecauseOfNonRegisteredAccount =
      MutableMessage.of(
          "<red>Nie możesz tego zrobić, ponieważ nie jesteś zarejestrowany, użyj <yellow>/register<red>, aby to zrobić!");

  public MutableMessage notAllowedBecauseOfAuthorization =
      MutableMessage.of("<red>Nie możesz tego zrobić, ponieważ jesteś już uwierzytelniony!");

  public MutableMessage notAllowedBecauseOfMissingAuthorization =
      MutableMessage.of("<red>Nie możesz tego zrobić, ponieważ nie jesteś zalogowany!");

  public MutableMessage specifiedPasswordsDiffers =
      MutableMessage.of("<red>Wprowadzone przez ciebie hasła różnią się!");

  public MutableMessage specifiedPasswordIsInvalid =
      MutableMessage.of("<red>Wprowadzone przez ciebie hasło jest nieprawidłowe!");

  public MutableMessage specifiedPasswordIsSame =
      MutableMessage.of(
          "<red>Nie możesz tego zrobić, ponieważ wprowadzone hasło nie różni się od bieżącego.");

  public MutableMessage specifiedPasswordIsUnsafe =
      MutableMessage.of(
              """
      <red>Wprowadzone przez ciebie hasło nie spełnia wymagań!
      <dark_gray>Wymagania dotyczące hasła:
      <dark_red>► <red>Hasło może zawierać tylko znaki alfanumeryczne
      <dark_red>► <red>Hasło musi zawierać co najmniej jedną małą literę
      <dark_red>► <red>Hasło musi zawierać co najmniej jedną dużą literę
      <dark_red>► <red>Hasło musi zawierać co najmniej 8 znaków
      <dark_red>► <red>Hasło musi zawierać co najwyżej 32 znaki
      """
              .trim());

  public MutableMessage specifiedUsernameIsInvalid =
      MutableMessage.of(
          "<red>Nazwa użytkownika, która jest przypisana do twojego konta jest nieprawidłowa.");

  public MutableMessage specifiedEmailIsInvalid =
      MutableMessage.of("<red>Wprowadzony przez ciebie adres email jest nieprawidłowy.");

  public MutableMessage specifiedEmailIsTheSame =
      MutableMessage.of("<red>Wprowadzony przez ciebie adres email jest taki sam jak aktualny.");

  public MutableMessage specifiedEmailIsClaimed =
      MutableMessage.of("<red>Wprowadzony przez ciebie adres email jest już zajęty.");

  public MutableMessage loginSuccessful =
      MutableMessage.of(
          "<gray>Uwierzytelniłeś się pomyślnie, wkrótce zostaniesz przekierowany na serwer docelowy.");

  public MutableMessage passwordChanged = MutableMessage.of("<gray>Twoje hasło zostało zmienione.");

  public MutableMessage registeredAccount =
      MutableMessage.of("<gray>Twoje konto zostało zarejestrowane.");

  public MutableMessage unregisterAccount =
      MutableMessage.of("<gray>Twoje konto zostało wyrejestrowane.");

  public MutableMessage authorizationTicking =
      MutableMessage.of(
          "<dark_gray>► <gray>Na uwierzytelnienie pozostało ci <white>{period}<gray>.");

  public MutableMessage authorizationTimeout =
      MutableMessage.of("<red>Skończył ci się czas na uwierzytelnienie.");

  public MutableMessage authorizedWithPremium =
      MutableMessage.of("<gray>Zostałeś zalogowany automatycznie.");

  public MutableMessage suggestRegistration =
      MutableMessage.of(
          "<gray>Aby przejść do rozgrywki musisz się zarejestrować, użyj <white>/register<gray>, aby to zrobić.");

  public MutableMessage suggestAuthorization =
      MutableMessage.of(
          "<gray>Aby przejść do rozgrywki musisz się uwierzytelnić, użyj <white>/login<gray>, aby to zrobić.");

  public MutableMessage tooManyLoginAttempts =
      MutableMessage.of(
          "<red>Przekroczyłeś limit prób na wpisanie hasła, jeśli chcesz spróbować ponownie, dołącz na serwer.");

  public MutableMessage emailHasBeenChanged =
      MutableMessage.of("<gray>Ustawiłeś aktualny adres e-mail na <white>{email}<gray>.");

  public MutableMessage recoveryEmailSubject = MutableMessage.of("Odzyskiwanie hasła - AuroraMC");
}
