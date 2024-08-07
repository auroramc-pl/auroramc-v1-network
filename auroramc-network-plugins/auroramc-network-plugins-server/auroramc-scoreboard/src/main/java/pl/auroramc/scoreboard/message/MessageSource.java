package pl.auroramc.scoreboard.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import java.util.List;
import pl.auroramc.messages.message.MutableMessage;
import pl.auroramc.scoreboard.quest.QuestMessageSource;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage title =
      MutableMessage.of("<bold><gradient:#0bf57c:#10de73>Aurora<gradient:#00dbfc:#07c6e3>MC");

  public List<MutableMessage> lines =
      List.of(
          MutableMessage.of(
              "<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Nazwa: <gradient:#f7cf78:#fbd06a:#f4b352:#fbd06a:#f7cf78>%player_name%"),
          MutableMessage.of(
              "<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Ranga: <gradient:#f7cf78:#fbd06a:#f4b352:#fbd06a:#f7cf78>%luckperms_primary_group_name%"));

  public QuestMessageSource quest = new QuestMessageSource();
}
