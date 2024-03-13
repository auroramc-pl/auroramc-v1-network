package pl.auroramc.scoreboard.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import java.util.List;
import pl.auroramc.commons.message.MutableMessage;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "translation.yml";

  public MutableMessage title = MutableMessage.of(
      "<bold><gradient:#0bf57c:#10de73>Aurora<gradient:#00dbfc:#07c6e3>MC"
  );

  public List<MutableMessage> lines = List.of(
      MutableMessage.of(
          "<gray>Testowa linijka <white>1"
      ),
      MutableMessage.of(
          "<gray>Testowa linijka <white>2"
      ),
      MutableMessage.of(
          "<gray>Testowa linijka <white>3"
      )
  );

  public MessageSourceQuest quest = new MessageSourceQuest();
}
