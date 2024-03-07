/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.jackson;

public class Comment {
  private String author;

  private String comment;

  public Comment(final String author, final String comment) {
    this.author = author;
    this.comment = comment;
  }

  public Comment() {}

  public String getAuthor() {
    return author;
  }

  public String getComment() {
    return comment;
  }

  public void setAuthor(final String author) {
    this.author = author;
  }

  public void setComment(final String comment) {
    this.comment = comment;
  }
}
