package pl.auroramc.bounties.progress;

import java.time.LocalDate;

public class BountyProgress {

  private Long id;
  private Long userId;
  private Long day;
  private LocalDate acquisitionDate;

  public BountyProgress(final Long id, final Long userId, final Long day, final LocalDate acquisitionDate) {
    this.id = id;
    this.userId = userId;
    this.day = day;
    this.acquisitionDate = acquisitionDate;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(final Long userId) {
    this.userId = userId;
  }

  public Long getDay() {
    return day;
  }

  public void setDay(final Long day) {
    this.day = day;
  }

  public LocalDate getAcquisitionDate() {
    return acquisitionDate;
  }

  public void setAcquisitionDate(final LocalDate acquisitionDate) {
    this.acquisitionDate = acquisitionDate;
  }
}
