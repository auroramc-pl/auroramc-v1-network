package pl.auroramc.auth.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.commons.message.MutableMessage;

public class MutableMessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage availableSchematicsSuggestion =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Poprawne użycie: <gradient:#ffad33:#ff8052:#ffdb57:#ff8052:#ffdb57:#ffad33><newline><schematics>");

  public MutableMessage executionOfCommandIsNotPermitted =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie posiadasz wystarczających uprawnień aby użyć tej komendy.");

  public MutableMessage executionFromConsoleIsUnsupported =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie możesz użyć tej konsoli z poziomu konsoli.");

  public MutableMessage notAllowedBecauseOfPremiumAccount =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie możesz tego zrobić, ponieważ posiadasz włączone automatyczne uwierzytelnianie!");

  public MutableMessage notAllowedBecauseOfRegisteredAccount =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie możesz tego zrobić, ponieważ jesteś już zarejestrowany!");

  public MutableMessage notAllowedBecauseOfNonRegisteredAccount =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie możesz tego zrobić, ponieważ nie jesteś zarejestrowany, użyj <gradient:#ffad33:#ff8052:#ffdb57:#ff8052:#ffdb57:#ffad33>/register<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>, aby to zrobić!");

  public MutableMessage notAllowedBecauseOfAuthorization =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie możesz tego zrobić, ponieważ jesteś już uwierzytelniony!");

  public MutableMessage notAllowedBecauseOfMissingAuthorization =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie możesz tego zrobić, ponieważ nie jesteś zalogowany!");

  public MutableMessage specifiedPasswordsDiffers =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wprowadzone przez ciebie hasła różnią się!");

  public MutableMessage specifiedPasswordIsInvalid =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wprowadzone przez ciebie hasło jest nieprawidłowe!");

  public MutableMessage specifiedPasswordIsSame =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie możesz tego zrobić, ponieważ wprowadzone hasło nie różni się od bieżącego.");

  public MutableMessage specifiedPasswordIsUnsafe =
      MutableMessage.of(
          """
      <gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wprowadzone przez ciebie hasło nie spełnia wymagań!
      <#7c5058>Wymagania dotyczące hasła:
      <#990000>► <gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Hasło może zawierać tylko znaki alfanumeryczne
      <#990000>► <gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Hasło musi zawierać co najmniej jedną małą literę
      <#990000>► <gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Hasło musi zawierać co najmniej jedną dużą literę
      <#990000>► <gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Hasło musi zawierać co najmniej 8 znaków
      <#990000>► <gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Hasło musi zawierać co najwyżej 32 znaki
      """
              .trim());

  public MutableMessage specifiedUsernameIsInvalid =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nazwa użytkownika, która jest przypisana do twojego konta jest nieprawidłowa.");

  public MutableMessage specifiedEmailIsInvalid =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wprowadzony przez ciebie adres email jest nieprawidłowy.");

  public MutableMessage specifiedEmailIsTheSame =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wprowadzony przez ciebie adres email jest taki sam jak aktualny.");

  public MutableMessage specifiedEmailIsClaimed =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wprowadzony przez ciebie adres email jest już zajęty.");

  public MutableMessage loginSuccessful =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Uwierzytelniłeś się pomyślnie, wkrótce zostaniesz przekierowany na serwer docelowy.");

  public MutableMessage passwordChanged =
      MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Twoje hasło zostało zmienione.");

  public MutableMessage registeredAccount =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Twoje konto zostało zarejestrowane.");

  public MutableMessage unregisterAccount =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Twoje konto zostało wyrejestrowane.");

  public MutableMessage authorizationTicking =
      MutableMessage.of(
          "<#7c5058>► <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Na uwierzytelnienie pozostało ci <#f4a9ba><period><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>.");

  public MutableMessage authorizationTimeout =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Skończył ci się czas na uwierzytelnienie.");

  public MutableMessage authorizedWithPremium =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Zostałeś zalogowany automatycznie.");

  public MutableMessage suggestRegistration =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Aby przejść do rozgrywki musisz się zarejestrować, użyj <#f4a9ba>/register<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>, aby to zrobić.");

  public MutableMessage suggestAuthorization =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Aby przejść do rozgrywki musisz się uwierzytelnić, użyj <#f4a9ba>/login<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>, aby to zrobić.");

  public MutableMessage tooManyLoginAttempts =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Przekroczyłeś limit prób na wpisanie hasła, jeśli chcesz spróbować ponownie, dołącz na serwer.");

  public MutableMessage emailHasBeenChanged =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Ustawiłeś aktualny adres e-mail na <#f4a9ba><email><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>.");

  public MutableMessage recoveryEmailSubject = MutableMessage.of("Odzyskiwanie hasła - AuroraMC");
}
