package com.iridium.iridiumcolors.tag;

import static com.iridium.iridiumcolors.IridiumColors.color;
import static java.util.regex.Pattern.compile;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Represents a gradient color pattern which can be applied to a String. */
public class GradientTagResolver implements TagResolver {

  Pattern pattern = compile("<gradient:([0-9A-Fa-f]{6})>(.*?)</gradient:([0-9A-Fa-f]{6})>");

  /**
   * Applies a gradient pattern to the provided String. Output might me the same as the input if
   * this pattern is not present.
   *
   * @param string The String to which this pattern should be applied to
   * @return The new String with applied pattern
   */
  public String resolve(final String string) {
    final Matcher matcher = pattern.matcher(string);
    final StringBuilder result = new StringBuilder();
    int lastMatchEnd = 0;
    while (matcher.find()) {
      final String start = matcher.group(1);
      final String end = matcher.group(3);
      final String content = matcher.group(2);
      result
          .append(string, lastMatchEnd, matcher.start())
          .append(
              color(
                  content,
                  new Color(Integer.parseInt(start, 16)),
                  new Color(Integer.parseInt(end, 16))));
      lastMatchEnd = matcher.end();
    }
    return result.append(string.substring(lastMatchEnd)).toString();
  }
}
