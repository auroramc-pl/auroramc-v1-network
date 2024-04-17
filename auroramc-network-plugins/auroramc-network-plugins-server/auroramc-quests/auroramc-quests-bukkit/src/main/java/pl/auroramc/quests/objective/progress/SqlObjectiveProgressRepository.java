package pl.auroramc.quests.objective.progress;

import static pl.auroramc.quests.objective.progress.SqlObjectiveProgressRepositoryQuery.CREATE_OBJECTIVE_PROGRESS;
import static pl.auroramc.quests.objective.progress.SqlObjectiveProgressRepositoryQuery.CREATE_OBJECTIVE_PROGRESS_SCHEMA;
import static pl.auroramc.quests.objective.progress.SqlObjectiveProgressRepositoryQuery.DELETE_OBJECTIVE_PROGRESS_BY_USER_ID_AND_QUEST_ID;
import static pl.auroramc.quests.objective.progress.SqlObjectiveProgressRepositoryQuery.GET_OBJECTIVE_PROGRESS;
import static pl.auroramc.quests.objective.progress.SqlObjectiveProgressRepositoryQuery.GET_OBJECTIVE_PROGRESSES;
import static pl.auroramc.quests.objective.progress.SqlObjectiveProgressRepositoryQuery.UPDATE_OBJECTIVE_PROGRESS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import moe.rafal.juliet.Juliet;

class SqlObjectiveProgressRepository implements ObjectiveProgressRepository {

  private final Juliet juliet;

  SqlObjectiveProgressRepository(final Juliet juliet) {
    this.juliet = juliet;
  }

  void createObjectiveProgressSchemaIfRequired() {
    try (final Connection connection = juliet.borrowConnection();
        final Statement statement = connection.createStatement()) {
      statement.execute(CREATE_OBJECTIVE_PROGRESS_SCHEMA);
    } catch (final SQLException exception) {
      throw new ObjectiveProgressRepositoryException(
          "Could not create schema for objective progresses, because of unexpected exception.",
          exception);
    }
  }

  @Override
  public List<ObjectiveProgress> getObjectiveProgresses(
      final ObjectiveProgressCompositeKey objectiveProgressesKey) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(GET_OBJECTIVE_PROGRESSES)) {
      statement.setLong(1, objectiveProgressesKey.userId());
      statement.setLong(2, objectiveProgressesKey.questId());

      final List<ObjectiveProgress> results = new ArrayList<>();
      try (final ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          results.add(mapResultSetToObjectiveProgress(resultSet));
        }
      }

      return results;
    } catch (final SQLException exception) {
      throw new ObjectiveProgressRepositoryException(
          "Could not get objective progresses, because of unexpected exception.", exception);
    }
  }

  @Override
  public ObjectiveProgress getObjectiveProgress(final ObjectiveProgressKey objectiveProgressKey) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(GET_OBJECTIVE_PROGRESS)) {
      statement.setLong(1, objectiveProgressKey.userId());
      statement.setLong(2, objectiveProgressKey.questId());
      statement.setLong(3, objectiveProgressKey.objectiveId());
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToObjectiveProgress(resultSet);
        }
      }
      return null;
    } catch (final SQLException exception) {
      throw new ObjectiveProgressRepositoryException(
          "Could not get objective progress, because of unexpected exception.", exception);
    }
  }

  @Override
  public void createObjectiveProgress(final ObjectiveProgress objectiveProgress) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement =
            connection.prepareStatement(CREATE_OBJECTIVE_PROGRESS)) {
      statement.setLong(1, objectiveProgress.getUserId());
      statement.setLong(2, objectiveProgress.getQuestId());
      statement.setLong(3, objectiveProgress.getObjectiveId());
      statement.setInt(4, objectiveProgress.getData());
      statement.setInt(5, objectiveProgress.getGoal());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new ObjectiveProgressRepositoryException(
          "Could not create objective progress, because of unexpected exception.", exception);
    }
  }

  @Override
  public void updateObjectiveProgress(final ObjectiveProgress objectiveProgress) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement =
            connection.prepareStatement(UPDATE_OBJECTIVE_PROGRESS)) {
      statement.setInt(1, objectiveProgress.getData());
      statement.setLong(2, objectiveProgress.getUserId());
      statement.setLong(3, objectiveProgress.getQuestId());
      statement.setLong(4, objectiveProgress.getObjectiveId());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new ObjectiveProgressRepositoryException(
          "Could not update objective progress, because of unexpected exception.", exception);
    }
  }

  @Override
  public void deleteObjectiveProgressByUserIdAndQuestId(final Long userId, final Long questId) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement =
            connection.prepareStatement(DELETE_OBJECTIVE_PROGRESS_BY_USER_ID_AND_QUEST_ID)) {
      statement.setLong(1, userId);
      statement.setLong(2, questId);
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new ObjectiveProgressRepositoryException(
          "Could not delete objective progress, because of unexpected exception.", exception);
    }
  }

  private ObjectiveProgress mapResultSetToObjectiveProgress(final ResultSet resultSet)
      throws SQLException {
    return new ObjectiveProgress(
        resultSet.getLong("user_id"),
        resultSet.getLong("quest_id"),
        resultSet.getLong("objective_id"),
        resultSet.getInt("data"),
        resultSet.getInt("goal"));
  }
}
