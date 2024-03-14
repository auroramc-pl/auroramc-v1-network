package pl.auroramc.auctions.message.viewer;

import static pl.auroramc.auctions.message.viewer.SqlMessageViewerRepositoryQuery.CREATE_MESSAGE_VIEWER;
import static pl.auroramc.auctions.message.viewer.SqlMessageViewerRepositoryQuery.CREATE_MESSAGE_VIEWER_SCHEMA;
import static pl.auroramc.auctions.message.viewer.SqlMessageViewerRepositoryQuery.FIND_MESSAGE_VIEWER_BY_USER_UNIQUE_ID;
import static pl.auroramc.auctions.message.viewer.SqlMessageViewerRepositoryQuery.UPDATE_MESSAGE_VIEWER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import moe.rafal.juliet.Juliet;

class SqlMessageViewerRepository implements MessageViewerRepository {

  private final Juliet juliet;

  SqlMessageViewerRepository(final Juliet juliet) {
    this.juliet = juliet;
  }

  void createMessageViewerSchemaIfRequired() {
    try (
        final Connection connection = juliet.borrowConnection();
        final Statement statement = connection.createStatement()
    ) {
      statement.execute(CREATE_MESSAGE_VIEWER_SCHEMA);
    } catch (final SQLException exception) {
      throw new MessageViewerRepositoryException(
          "Could not create schema for message viewer entity, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public MessageViewer findMessageViewerByUserUniqueId(final UUID uniqueId) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(FIND_MESSAGE_VIEWER_BY_USER_UNIQUE_ID)
    ) {
      statement.setString(1, uniqueId.toString());
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToMessageViewer(resultSet);
        }
      }
      return null;
    } catch (final SQLException exception) {
      throw new MessageViewerRepositoryException(
          "Could not find message viewer by user id, because of unexpected exception.",
          exception);
    }
  }

  @Override
  public void createMessageViewer(final MessageViewer messageViewer) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(CREATE_MESSAGE_VIEWER)
    ) {
      statement.setLong(1, messageViewer.getUserId());
      statement.setBoolean(2, messageViewer.isWhetherReceiveMessages());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new MessageViewerRepositoryException(
          "Could not create message viewer, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public void updateMessageViewer(final MessageViewer messageViewer) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(UPDATE_MESSAGE_VIEWER)
    ) {
      statement.setBoolean(1, messageViewer.isWhetherReceiveMessages());
      statement.setLong(2, messageViewer.getUserId());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new MessageViewerRepositoryException(
          "Could not update message viewer, because of unexpected exception.",
          exception
      );
    }
  }

  private MessageViewer mapResultSetToMessageViewer(
      final ResultSet resultSet
  ) throws SQLException {
    return new MessageViewer(
        resultSet.getLong("user_id"),
        resultSet.getBoolean("receive_messages")
    );
  }
}
