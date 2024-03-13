package pl.auroramc.quests.quest.track;

import static pl.auroramc.quests.quest.track.SqlQuestTrackRepositoryQuery.CREATE_QUEST_TRACK;
import static pl.auroramc.quests.quest.track.SqlQuestTrackRepositoryQuery.CREATE_QUEST_TRACK_SCHEMA;
import static pl.auroramc.quests.quest.track.SqlQuestTrackRepositoryQuery.GET_QUEST_TRACKS_BY_USER_UNIQUE_ID;
import static pl.auroramc.quests.quest.track.SqlQuestTrackRepositoryQuery.UPDATE_QUEST_TRACK;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import moe.rafal.juliet.Juliet;
import pl.auroramc.quests.quest.QuestState;

class SqlQuestTrackRepository implements QuestTrackRepository {

  private final Juliet juliet;

  SqlQuestTrackRepository(final Juliet juliet) {
    this.juliet = juliet;
  }

  void createQuestTrackSchemaIfRequired() {
    try (
        final Connection connection = juliet.borrowConnection();
        final Statement statement = connection.createStatement()
    ) {
      statement.execute(CREATE_QUEST_TRACK_SCHEMA);
    } catch (final SQLException exception) {
      throw new QuestTrackRepositoryException(
          "Could not create schema for quest tracks, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public List<QuestTrack> getQuestTracksByUserUniqueId(final UUID uniqueId) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(GET_QUEST_TRACKS_BY_USER_UNIQUE_ID)
    ) {
      statement.setObject(1, uniqueId);

      final List<QuestTrack> results = new ArrayList<>();
      try (final ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          results.add(getQuestTrackFromResultSet(resultSet));
        }
        return results;
      }
    } catch (final SQLException exception) {
      throw new QuestTrackRepositoryException(
          "Could not get quest tracks by user unique id, because of unexpected exception.",
          exception);
    }
  }

  private QuestTrack getQuestTrackFromResultSet(final ResultSet resultSet) throws SQLException {
    return new QuestTrack(
        resultSet.getLong("user_id"),
        resultSet.getLong("quest_id"),
        QuestState.valueOf(resultSet.getString("quest_state"))
    );
  }

  @Override
  public void createQuestTrack(final QuestTrack questTrack) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(CREATE_QUEST_TRACK)) {
      statement.setLong(1, questTrack.getUserId());
      statement.setLong(2, questTrack.getQuestId());
      statement.setString(3, questTrack.getQuestState().name());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new QuestTrackRepositoryException(
          "Could not create quest track, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public void updateQuestTrack(final QuestTrack questTrack) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(UPDATE_QUEST_TRACK)
    ) {
      statement.setString(1, questTrack.getQuestState().name());
      statement.setLong(2, questTrack.getUserId());
      statement.setLong(3, questTrack.getQuestId());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new QuestTrackRepositoryException(
          "Could not update quest track, because of unexpected exception.",
          exception
      );
    }
  }
}
