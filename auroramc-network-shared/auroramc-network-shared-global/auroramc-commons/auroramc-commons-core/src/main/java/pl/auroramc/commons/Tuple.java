package pl.auroramc.commons;

public class Tuple<A, B> {

  private final A a;
  private final B b;

  private Tuple(final A a, final B b) {
    this.a = a;
    this.b = b;
  }

  public static <A, B> Tuple<A, B> tupleOf(final A a, final B b) {
    return new Tuple<>(a, b);
  }

  public A getA() {
    return a;
  }

  public B getB() {
    return b;
  }
}
