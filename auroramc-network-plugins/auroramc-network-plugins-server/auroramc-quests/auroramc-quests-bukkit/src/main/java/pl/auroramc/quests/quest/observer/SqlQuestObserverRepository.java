package pl.auroramc.quests.quest.observer;

import static java.sql.Types.BIGINT;
import static pl.auroramc.quests.quest.observer.SqlQuestObserverRepositoryQuery.CREATE_QUEST_OBSERVER;
import static pl.auroramc.quests.quest.observer.SqlQuestObserverRepositoryQuery.CREATE_QUEST_OBSERVER_SCHEMA;
import static pl.auroramc.quests.quest.observer.SqlQuestObserverRepositoryQuery.FIND_QUEST_OBSERVER_BY_USER_UNIQUE_ID;
import static pl.auroramc.quests.quest.observer.SqlQuestObserverRepositoryQuery.UPDATE_QUEST_OBSERVER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import moe.rafal.juliet.Juliet;

class SqlQuestObserverRepository implements QuestObserverRepository {

  private final Juliet juliet;

  SqlQuestObserverRepository(final Juliet juliet) {
    this.juliet = juliet;
  }

  void createQuestObserverSchemaIfRequired() {
    try (
        final Connection connection = juliet.borrowConnection();
        final Statement statement = connection.createStatement()
    ) {
      statement.execute(CREATE_QUEST_OBSERVER_SCHEMA);
    } catch (final SQLException exception) {
      throw new QuestObserverRepositoryException(
          "Could not create schema for quest observers, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public QuestObserver findQuestObserverByUniqueId(final UUID uniqueId) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(FIND_QUEST_OBSERVER_BY_USER_UNIQUE_ID)
    ) {
      statement.setObject(1, uniqueId);
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToQuestObserver(resultSet);
        }
      }
      return null;
    } catch (final SQLException exception) {
      throw new QuestObserverRepositoryException(
          "Could not find quest observer by user id, because of unexpected exception.",
          exception
      );
    }
  }

  private QuestObserver mapResultSetToQuestObserver(final ResultSet resultSet) throws SQLException {
    return new QuestObserver(
        resultSet.getLong("user_id"),
        resultSet.getLong("quest_id")
    );
  }

  @Override
  public void createQuestObserver(final QuestObserver questObserver) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(CREATE_QUEST_OBSERVER)
    ) {
      statement.setLong(1, questObserver.getUserId());
      setLongOrNull(statement, 2, questObserver.getQuestId());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new QuestObserverRepositoryException(
          "Could not create quest observer, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public void updateQuestObserver(final QuestObserver questObserver) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(UPDATE_QUEST_OBSERVER)
    ) {
      setLongOrNull(statement, 1, questObserver.getQuestId());
      statement.setLong(2, questObserver.getUserId());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new QuestObserverRepositoryException(
          "Could not update quest observer, because of unexpected exception.",
          exception
      );
    }
  }

  private void setLongOrNull(
      final PreparedStatement statement,
      final int parameterIndex,
      final Long value
  ) throws SQLException {
    if (value == null) {
      statement.setNull(parameterIndex, BIGINT);
    } else {
      statement.setLong(parameterIndex, value);
    }
  }
}
