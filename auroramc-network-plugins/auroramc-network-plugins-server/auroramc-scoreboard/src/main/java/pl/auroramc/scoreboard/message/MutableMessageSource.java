package pl.auroramc.scoreboard.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import java.util.List;
import pl.auroramc.commons.message.MutableMessage;

public class MutableMessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "translation.yml";

  public MutableMessage title = MutableMessage.of(
      "<bold><gradient:#0bf57c:#10de73>Aurora<gradient:#00dbfc:#07c6e3>MC"
  );

  public List<MutableMessage> lines = List.of(
      MutableMessage.of(
          "<gray>Nazwa: <white>%player_name%"
      ),
      MutableMessage.of(
          "<gray>Ranga: <white>%luckperms_primary_group_name%"
      )
  );

  public MutableMessageSourceQuest quest = new MutableMessageSourceQuest();
}
