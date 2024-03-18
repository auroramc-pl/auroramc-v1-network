package com.iridium.iridiumcolors.tag;

/**
 * Represents a color tag which can be applied to a String.
 */
public interface TagResolver {

    /**
     * Applies this pattern to the provided String.
     * Output might be the same as the input if this tag is not present.
     *
     * @param string The String to which this tag should be applied to
     * @return The new String with applied tag
     */
    String resolve(final String string);
}
