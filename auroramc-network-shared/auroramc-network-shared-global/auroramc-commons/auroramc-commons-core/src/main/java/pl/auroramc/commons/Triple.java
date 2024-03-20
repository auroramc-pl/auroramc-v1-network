package pl.auroramc.commons;

public class Triple<A, B, C> {

  private final A a;
  private final B b;
  private final C c;

  private Triple(final A a, final B b, final C c) {
    this.a = a;
    this.b = b;
    this.c = c;
  }

  public static <A, B, C> Triple<A, B, C> tripleOf(final A a, final B b, final C c) {
    return new Triple<>(a, b, c);
  }

  public A getA() {
    return a;
  }

  public B getB() {
    return b;
  }

  public C getC() {
    return c;
  }
}
