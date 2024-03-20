package com.iridium.iridiumcolors.tag;

import static com.iridium.iridiumcolors.IridiumColors.rainbow;
import static java.lang.Float.parseFloat;
import static java.util.regex.Pattern.compile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RainbowTagResolver implements TagResolver {

  Pattern pattern = compile("<rainbow([0-9]{1,3})>(.*?)</rainbow>");

  /**
   * Applies a rainbow pattern to the provided String. Output might me the same as the input if this
   * pattern is not present.
   *
   * @param string The String to which this pattern should be applied to
   * @return The new String with applied pattern
   */
  public String resolve(String string) {
    final Matcher matcher = pattern.matcher(string);
    final StringBuilder result = new StringBuilder();
    int lastMatchEnd = 0;
    while (matcher.find()) {
      final String saturation = matcher.group(1);
      final String content = matcher.group(2);
      result
          .append(string, lastMatchEnd, matcher.start())
          .append(rainbow(content, parseFloat(saturation)));
      lastMatchEnd = matcher.end();
    }
    return result.append(string.substring(lastMatchEnd)).toString();
  }
}
