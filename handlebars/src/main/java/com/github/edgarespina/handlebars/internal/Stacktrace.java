package com.github.edgarespina.handlebars.internal;

/**
 * Utitlity class for tracking template stack.
 *
 * @author edgar.espina
 * @since 0.2.1
 */
class Stacktrace {

  /**
   * The line number.
   */
  private final int line;

  /**
   * The column number.
   */
  private final int column;

  /**
   * The file's name.
   */
  private final String filename;

  /**
   * Creates a new {@link Stacktrace}.
   *
   * @param line The line number.
   * @param column The column number.
   * @param filename The file's name.
   */
  public Stacktrace(final int line, final int column,
      final String filename) {
    this.line = line;
    this.column = column;
    this.filename = filename;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + column;
    result = prime * result + (filename == null ? 0 : filename.hashCode());
    result = prime * result + line;
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Stacktrace other = (Stacktrace) obj;
    if (column != other.column) {
      return false;
    }
    if (filename == null) {
      if (other.filename != null) {
        return false;
      }
    } else if (!filename.equals(other.filename)) {
      return false;
    }
    if (line != other.line) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    buffer
        .append("at ").append(filename).append(":")
        .append(line).append(":").append(column);
    return buffer.toString();
  }
}
