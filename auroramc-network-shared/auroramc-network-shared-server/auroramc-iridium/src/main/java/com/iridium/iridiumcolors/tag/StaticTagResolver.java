package com.iridium.iridiumcolors.tag;

import static com.iridium.iridiumcolors.IridiumColors.getColor;
import static java.util.regex.Pattern.compile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaticTagResolver implements TagResolver {

  Pattern pattern = compile("<static:([0-9A-Fa-f]{6})>|#\\{([0-9A-Fa-f]{6})}");

  /**
   * Applies a solid RGB color to the provided String. Output might me the same as the input if this
   * pattern is not present.
   *
   * @param string The String to which this pattern should be applied to
   * @return The new String with applied pattern
   */
  public String resolve(final String string) {
    final Matcher matcher = pattern.matcher(string);
    final StringBuilder result = new StringBuilder();
    int lastMatchEnd = 0;
    while (matcher.find()) {
      String color = matcher.group(1);
      if (color == null) {
        color = matcher.group(2);
      }
      result.append(string, lastMatchEnd, matcher.start()).append(getColor(color));
      lastMatchEnd = matcher.end();
    }
    return result.append(string.substring(lastMatchEnd)).toString();
  }
}
