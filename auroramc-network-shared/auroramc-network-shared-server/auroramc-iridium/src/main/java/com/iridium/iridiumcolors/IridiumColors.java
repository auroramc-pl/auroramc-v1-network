package com.iridium.iridiumcolors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.util.Arrays.asList;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumcolors.tag.GradientTagResolver;
import com.iridium.iridiumcolors.tag.RainbowTagResolver;
import com.iridium.iridiumcolors.tag.StaticTagResolver;
import com.iridium.iridiumcolors.tag.TagResolver;
import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

public class IridiumColors {

  /**
   * The current version of the server in the form of a major version. If the static initialization
   * for these fails, you know something's wrong with the server software.
   *
   * @since 1.0.0
   */
  private static final int VERSION = getVersion();

  /**
   * Cached result if the server version is after the v1.16 RGB update.
   *
   * @since 1.0.0
   */
  private static final boolean SUPPORTS_RGB = VERSION >= 16 || VERSION == -1;

  private static final List<String> SPECIAL_COLORS = asList(
      "&l",
      "&n",
      "&o",
      "&k",
      "&m",
      "§l",
      "§n",
      "§o",
      "§k",
      "§m"
  );

  /**
   * Cached result of all legacy colors.
   *
   * @since 1.0.0
   */
  private static final Map<Color, ChatColor> COLORS =
      ImmutableMap.<Color, ChatColor>builder()
          .put(new Color(0), ChatColor.getByChar('0'))
          .put(new Color(170), ChatColor.getByChar('1'))
          .put(new Color(43520), ChatColor.getByChar('2'))
          .put(new Color(43690), ChatColor.getByChar('3'))
          .put(new Color(11141120), ChatColor.getByChar('4'))
          .put(new Color(11141290), ChatColor.getByChar('5'))
          .put(new Color(16755200), ChatColor.getByChar('6'))
          .put(new Color(11184810), ChatColor.getByChar('7'))
          .put(new Color(5592405), ChatColor.getByChar('8'))
          .put(new Color(5592575), ChatColor.getByChar('9'))
          .put(new Color(5635925), ChatColor.getByChar('a'))
          .put(new Color(5636095), ChatColor.getByChar('b'))
          .put(new Color(16733525), ChatColor.getByChar('c'))
          .put(new Color(16733695), ChatColor.getByChar('d'))
          .put(new Color(16777045), ChatColor.getByChar('e'))
          .put(new Color(16777215), ChatColor.getByChar('f'))
          .build();

  /**
   * Cached result of patterns.
   *
   * @since 1.0.2
   */
  private static final List<TagResolver> TAG_RESOLVERS = asList(
      new StaticTagResolver(),
      new RainbowTagResolver(),
      new GradientTagResolver()
  );

  /**
   * Compiled pattern for stripping color codes.
   */
  private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile(
      "<#[0-9A-F]{6}>|[&§][a-f0-9lnokm]|<(rainbow|gradient|static)(:[0-9A-F]{6})?[0-9]*>|</(rainbow|gradient|static)(:[0-9A-F]{6})?>"
  );

  private IridiumColors() {

  }

  /**
   * Processes a string to add color to it. Thanks to Distressing for helping with the regex <3
   *
   * @param string The string we want to process
   * @since 1.0.0
   */
  public static @Nonnull String process(@Nonnull String string) {
    for (final TagResolver tagResolver : TAG_RESOLVERS) {
      string = tagResolver.resolve(string);
    }

    return translateAlternateColorCodes('&', string);
  }

  /**
   * Processes multiple strings in a collection.
   *
   * @param strings The collection of the strings we are processing
   * @return The list of processed strings
   * @since 1.0.3
   */
  public static @Nonnull List<String> process(final @Nonnull Collection<String> strings) {
    return strings.stream()
        .map(IridiumColors::process)
        .toList();
  }

  /**
   * Colors a String.
   *
   * @param string The string we want to color
   * @param color  The color we want to set it to
   * @since 1.0.0
   */
  public static @Nonnull String color(final @Nonnull String string, final @Nonnull Color color) {
    return (
        SUPPORTS_RGB
            ? ChatColor.of(color)
            : getClosestColor(color)
    ) + string;
  }

  /**
   * Colors a String with a gradiant.
   *
   * @param string The string we want to color
   * @param start  The starting gradiant
   * @param end    The ending gradiant
   * @since 1.0.0
   */
  public static @Nonnull String color(
      final @Nonnull String string,
      final @Nonnull Color start,
      final @Nonnull Color end
  ) {
    final ChatColor[] colors = createGradient(start, end, withoutSpecialChar(string).length());
    return apply(string, colors);
  }

  /**
   * Colors a String with rainbow colors.
   *
   * @param string     The string which should have rainbow colors
   * @param saturation The saturation of the rainbow colors
   * @since 1.0.3
   */
  public static @Nonnull String rainbow(final @Nonnull String string, @Nonnull float saturation) {
    final ChatColor[] colors = createRainbow(withoutSpecialChar(string).length(), saturation);
    return apply(string, colors);
  }

  /**
   * Gets a color from hex code.
   *
   * @param string The hex code of the color
   * @since 1.0.0
   */
  public static @Nonnull ChatColor getColor(final @Nonnull String string) {
    return SUPPORTS_RGB
        ? ChatColor.of(new Color(parseInt(string, 16)))
        : getClosestColor(new Color(parseInt(string, 16)));
  }

  /**
   * Removes all color codes from the provided String, including IridiumColorAPI patterns.
   *
   * @param string The String which should be stripped
   * @return The stripped string without color codes
   * @since 1.0.5
   */
  public static @Nonnull String stripColorFormatting(final @Nonnull String string) {
    return STRIP_COLOR_PATTERN.matcher(string).replaceAll("");
  }

  private static @Nonnull String apply(final @Nonnull String source, final ChatColor[] colors) {
    final StringBuilder specialColors = new StringBuilder();
    final StringBuilder stringBuilder = new StringBuilder();
    int outIndex = 0;

    for (int index = 0; index < source.length(); index++) {
      final char currentChar = source.charAt(index);
      if (('&' != currentChar && '§' != currentChar) || index + 1 >= source.length()) {
        stringBuilder.append(colors[outIndex++]).append(specialColors).append(currentChar);
        continue;
      }

      final char nextChar = source.charAt(index + 1);
      if ('r' == nextChar || 'R' == nextChar) {
        specialColors.setLength(0);
      } else {
        specialColors.append(currentChar).append(nextChar);
      }

      index++;
    }
    return stringBuilder.toString();
  }

  private static @Nonnull String withoutSpecialChar(final @Nonnull String source) {
    String workingString = source;
    for (final String color : SPECIAL_COLORS) {
      if (workingString.contains(color)) {
        workingString = workingString.replace(color, "");
      }
    }
    return workingString;
  }

  /**
   * Returns a rainbow array of chat colors.
   *
   * @param step       How many colors we return
   * @param saturation The saturation of the rainbow
   * @return The array of colors
   * @since 1.0.3
   */
  private static @Nonnull ChatColor[] createRainbow(final int step, final float saturation) {
    final ChatColor[] colors = new ChatColor[step];
    final double colorStep = (1.00 / step);
    for (int index = 0; index < step; index++) {
      Color color = Color.getHSBColor((float) (colorStep * index), saturation, saturation);
      if (SUPPORTS_RGB) {
        colors[index] = ChatColor.of(color);
      } else {
        colors[index] = getClosestColor(color);
      }
    }
    return colors;
  }

  /**
   * Returns a gradient array of chat colors.
   *
   * @param start The starting color.
   * @param end   The ending color.
   * @param step  How many colors we return.
   * @author TheViperShow
   * @since 1.0.0
   */
  private static @Nonnull ChatColor[] createGradient(
      final @Nonnull Color start,
      final @Nonnull Color end,
      int step
  ) {
    step = max(step, 2);

    final ChatColor[] colors = new ChatColor[step];
    final int stepR = abs(start.getRed() - end.getRed()) / (step - 1);
    final int stepG = abs(start.getGreen() - end.getGreen()) / (step - 1);
    final int stepB = abs(start.getBlue() - end.getBlue()) / (step - 1);
    final int[] direction = new int[]{
        start.getRed() < end.getRed() ? +1 : -1,
        start.getGreen() < end.getGreen() ? +1 : -1,
        start.getBlue() < end.getBlue() ? +1 : -1
    };

    for (int i = 0; i < step; i++) {
      final Color color = new Color(
          start.getRed() + ((stepR * i) * direction[0]),
          start.getGreen() + ((stepG * i) * direction[1]),
          start.getBlue() + ((stepB * i) * direction[2])
      );
      if (SUPPORTS_RGB) {
        colors[i] = ChatColor.of(color);
      } else {
        colors[i] = getClosestColor(color);
      }
    }

    return colors;
  }

  /**
   * Returns the closest legacy color from an rgb color
   *
   * @param color The color we want to transform
   * @since 1.0.0
   */
  private static @Nonnull ChatColor getClosestColor(Color color) {
    Color nearestColor = null;
    double nearestDistance = Integer.MAX_VALUE;

    for (final Color constantColor : COLORS.keySet()) {
      final double distance = pow(color.getRed() - constantColor.getRed(), 2)
          + pow(color.getGreen() - constantColor.getGreen(), 2)
          + pow(color.getBlue() - constantColor.getBlue(), 2);
      if (nearestDistance > distance) {
        nearestColor = constantColor;
        nearestDistance = distance;
      }
    }
    return COLORS.get(nearestColor);
  }

  /**
   * Gets a simplified major version (..., 9, 10, ..., 14). In most cases, you shouldn't be using
   * this method.
   *
   * @return the simplified major version, or -1 for bungeecord
   * @since 1.0.0
   */
  private static int getVersion() {
    if (!isClassAvailableInClasspath("org.bukkit.Bukkit") && isClassAvailableInClasspath(
        "net.md_5.bungee.api.ChatColor")) {
      return -1;
    }

    String version = Bukkit.getVersion();
    checkArgument(
        !version.isEmpty(),
        "Cannot get major Minecraft version from null or empty string"
    );

    // getVersion()
    int index = version.lastIndexOf("MC:");
    if (index != -1) {
      version = version.substring(index + 4, version.length() - 1);
    } else if (version.endsWith("SNAPSHOT")) {
      // getBukkitVersion()
      index = version.indexOf('-');
      version = version.substring(0, index);
    }

    // 1.13.2, 1.14.4, etc...
    final int lastDot = version.lastIndexOf('.');
    if (version.indexOf('.') != lastDot) {
      version = version.substring(0, lastDot);
    }

    return parseInt(version.substring(2));
  }

  /**
   * Checks if a class exists in the current server
   *
   * @param path The path of that class
   * @return true if the class exists, false if it doesn't
   * @since 1.0.7
   */
  private static boolean isClassAvailableInClasspath(final String path) {
    try {
      Class.forName(path);
      return true;
    } catch (final ClassNotFoundException exception) {
      return false;
    }
  }
}
