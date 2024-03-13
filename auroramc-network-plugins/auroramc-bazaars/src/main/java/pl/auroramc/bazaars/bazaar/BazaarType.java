package pl.auroramc.bazaars.bazaar;

public enum BazaarType {

  BUY('B'),
  SELL('S');

  final char shortcut;

  BazaarType(final char shortcut) {
    this.shortcut = shortcut;
  }

  public char getShortcut() {
    return shortcut;
  }

  public static BazaarType getBazaarTypeByShortcut(final char shortcut) {
    for (final BazaarType type : values()) {
      if (type.shortcut == shortcut) {
        return type;
      }
    }

    return null;
  }
}
