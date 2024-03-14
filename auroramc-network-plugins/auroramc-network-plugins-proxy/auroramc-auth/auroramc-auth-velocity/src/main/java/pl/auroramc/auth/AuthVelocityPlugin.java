package pl.auroramc.auth;

import static java.lang.String.join;
import static java.time.Duration.ZERO;
import static java.time.Duration.ofSeconds;
import static moe.rafal.juliet.datasource.HikariPooledDataSourceFactory.produceHikariDataSource;
import static pl.auroramc.auth.AuthConfig.AUTH_CONFIG_FILE_NAME;
import static pl.auroramc.auth.BuildManifest.PROJECT_ARTIFACT_ID;
import static pl.auroramc.auth.BuildManifest.PROJECT_VERSION;
import static pl.auroramc.auth.account.AccountFacadeFactory.getAccountFacade;
import static pl.auroramc.auth.mail.MailFacade.getEmailFacade;
import static pl.auroramc.auth.hash.HashingStrategyFactory.getHashingStrategy;
import static pl.auroramc.auth.hash.salt.SaltGeneratorFactory.getSaltGenerator;
import static pl.auroramc.auth.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.auth.identity.generator.IdentityGeneratorFactory.getIdentityGenerator;
import static pl.auroramc.auth.password.PasswordValidatorFactory.getPasswordValidator;
import static pl.auroramc.auth.timeout.TimeoutFacadeFactory.getTimeoutFacade;
import static pl.auroramc.auth.user.UserFacadeFactory.getUserFacade;
import static pl.auroramc.commons.VelocityUtils.registerListeners;
import static pl.auroramc.commons.config.serdes.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;
import static pl.auroramc.commons.duration.DurationFormatterStyle.DEFAULT;
import static pl.auroramc.commons.plural.Pluralizers.getPluralizer;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.schematic.Schematic;
import dev.rollczi.litecommands.velocity.LiteVelocityFactory;
import dev.rollczi.litecommands.velocity.tools.VelocityOnlyPlayerContextual;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import java.nio.file.Path;
import java.util.Locale;
import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;
import moe.rafal.juliet.JulietBuilder;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import pl.auroramc.auth.account.AccountFacade;
import pl.auroramc.auth.account.AccountListener;
import pl.auroramc.auth.authorization.AuthorizationListener;
import pl.auroramc.auth.mail.MailCommand;
import pl.auroramc.auth.command.LoginCommand;
import pl.auroramc.auth.command.PasswordChangeCommand;
import pl.auroramc.auth.command.PasswordController;
import pl.auroramc.auth.recovery.RecoveryCommand;
import pl.auroramc.auth.command.UnregisterCommand;
import pl.auroramc.auth.mail.MailFacade;
import pl.auroramc.auth.hash.HashingStrategy;
import pl.auroramc.auth.hash.salt.SaltGenerator;
import pl.auroramc.auth.message.MessageSource;
import pl.auroramc.auth.command.RegisterCommand;
import pl.auroramc.auth.password.PasswordValidator;
import pl.auroramc.auth.timeout.TimeoutFacade;
import pl.auroramc.auth.timeout.TimeoutListener;
import pl.auroramc.auth.timeout.TimeoutNotifyingTask;
import pl.auroramc.auth.user.UserController;
import pl.auroramc.auth.user.UserFacade;
import pl.auroramc.auth.user.UserListener;
import pl.auroramc.auth.identity.IdentityGenerator;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.juliet.JulietConfig;
import pl.auroramc.commons.config.serdes.juliet.SerdesJuliet;
import pl.auroramc.commons.config.serdes.message.SerdesMessageSource;
import pl.auroramc.commons.duration.DurationFormatter;
import pl.auroramc.commons.integration.litecommands.v2.MutableMessageResultHandler;
import pl.auroramc.commons.message.MutableMessage;

@Plugin(id = PROJECT_ARTIFACT_ID, version = PROJECT_VERSION, authors = "shitzuu <hello@rafal.moe>")
public class AuthVelocityPlugin {

  private static final Locale POLISH = new Locale("pl");
  private final ProxyServer server;
  private final Logger logger;
  private final ConfigFactory configFactory;
  private LiteCommands<CommandSource> commands;

  @Inject
  public AuthVelocityPlugin(final ProxyServer server, final Logger logger, final @DataDirectory Path dataPath) {
    this.server = server;
    this.logger = logger;
    this.configFactory = new ConfigFactory(dataPath, YamlSnakeYamlConfigurer::new);
  }

  @Subscribe
  public void onProxyInitialize(final ProxyInitializeEvent event) {
    final AuthConfig authConfig = configFactory.produceConfig(
        AuthConfig.class, AUTH_CONFIG_FILE_NAME
    );

    final JulietConfig julietConfig = configFactory.produceConfig(
        JulietConfig.class, JULIET_CONFIG_FILE_NAME, new SerdesJuliet()
    );
    final Juliet juliet = JulietBuilder.newBuilder()
        .withDataSource(produceHikariDataSource(julietConfig.hikari))
        .build();

    final MessageSource messageSource = configFactory.produceConfig(
        MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessageSource()
    );

    final DurationFormatter durationFormatter = new DurationFormatter(
        getPluralizer(POLISH), DEFAULT
    );

    final AccountFacade accountFacade = getAccountFacade();
    final UserFacade userFacade = getUserFacade(logger, juliet);
    final UserController userController = new UserController(
        logger, server, authConfig, userFacade, accountFacade
    );
    final IdentityGenerator identityGenerator = getIdentityGenerator(accountFacade);

    registerListeners(this, server,
        new UserListener(
            logger, messageSource, userFacade, userController, identityGenerator, authConfig.usernamePattern
        ),
        new AccountListener(logger, userFacade, accountFacade),
        new AuthorizationListener(logger, userFacade, authConfig.defaultCommands)
    );

    final SaltGenerator saltGenerator = getSaltGenerator();
    final HashingStrategy hashingStrategy = getHashingStrategy(saltGenerator);
    final PasswordValidator passwordValidator = getPasswordValidator(authConfig.passwordPattern);
    final PasswordController passwordController = new PasswordController(logger, messageSource, userFacade, userController, hashingStrategy, passwordValidator);

    final TimeoutFacade timeoutFacade = getTimeoutFacade();
    registerListeners(this, server, new TimeoutListener(userFacade, timeoutFacade));

    final Mailer mailer = MailerBuilder.withSMTPServer(
        authConfig.mailConfig.host,
        authConfig.mailConfig.port,
        authConfig.mailConfig.username,
        authConfig.mailConfig.password
    ).buildMailer();
    final MailFacade mailFacade = getEmailFacade(authConfig.mailConfig, messageSource);

    server.getScheduler()
        .buildTask(
            this,
            new TimeoutNotifyingTask(server, messageSource, timeoutFacade, durationFormatter)
        )
        .repeat(ofSeconds(1))
        .delay(ZERO)
        .schedule();

    commands = LiteVelocityFactory.builder(server)
        .contextualBind(Player.class,
            new VelocityOnlyPlayerContextual<>(messageSource.executionFromConsoleIsUnsupported)
        )
        .commandInstance(
            new MailCommand(logger, messageSource, userFacade, authConfig.emailPattern)
        )
        .commandInstance(
            new LoginCommand(logger, messageSource, userFacade, userController, hashingStrategy, timeoutFacade)
        )
        .commandInstance(
            new PasswordChangeCommand(logger, messageSource, hashingStrategy, passwordController)
        )
        .commandInstance(
            new RecoveryCommand(logger, mailer, mailFacade, userFacade)
        )
        .commandInstance(
            new RegisterCommand(logger, messageSource, timeoutFacade, passwordController)
        )
        .commandInstance(
            new UnregisterCommand(logger, messageSource, userFacade, hashingStrategy)
        )
        .redirectResult(RequiredPermissions.class, MutableMessage.class,
            context -> messageSource.executionOfCommandIsNotPermitted
        )
        .redirectResult(Schematic.class, MutableMessage.class,
            context -> messageSource.availableSchematicsSuggestion
                .with("schematics", join("<newline>", context.getSchematics()))
        )
        .resultHandler(MutableMessage.class, new MutableMessageResultHandler())
        .register();
  }

  @Subscribe
  public void onProxyShutdown(final ProxyShutdownEvent event) {
    commands.getPlatform().unregisterAll();
  }
}
