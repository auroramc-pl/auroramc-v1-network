package pl.auroramc.auth.account;

public final class AccountFacadeFactory {

  private AccountFacadeFactory() {}

  public static AccountFacade getAccountFacade() {
    return new HttpAccountService();
  }
}
