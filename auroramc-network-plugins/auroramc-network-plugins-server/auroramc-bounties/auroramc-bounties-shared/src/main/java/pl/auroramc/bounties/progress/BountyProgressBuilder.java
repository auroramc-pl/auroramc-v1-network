package pl.auroramc.bounties.progress;

import java.time.LocalDate;

public final class BountyProgressBuilder {

  private Long userId;
  private Long day;
  private LocalDate acquisitionDate;

  private BountyProgressBuilder() {}

  public static BountyProgressBuilder newBuilder() {
    return new BountyProgressBuilder();
  }

  public BountyProgressBuilder withUserId(final Long userId) {
    this.userId = userId;
    return this;
  }

  public BountyProgressBuilder withDay(final Long day) {
    this.day = day;
    return this;
  }

  public BountyProgressBuilder withAcquisitionDate(final LocalDate acquisitionDate) {
    this.acquisitionDate = acquisitionDate;
    return this;
  }

  public BountyProgress build() {
    return new BountyProgress(null, userId, day, acquisitionDate);
  }
}
