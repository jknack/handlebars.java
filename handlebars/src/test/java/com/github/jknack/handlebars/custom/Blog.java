/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.custom;

import java.util.ArrayList;
import java.util.List;

public class Blog {
  private String title;

  private String body;

  private List<Comment> comments = new ArrayList<>();

  public Blog(final String title, final String body) {
    this.title = title;
    this.body = body;
  }

  public Blog() {}

  public String getTitle() {
    return title;
  }

  public String getBody() {
    return body;
  }

  public Blog add(final Comment comment) {
    comments.add(comment);
    return this;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public void setBody(final String body) {
    this.body = body;
  }

  public void setComments(final List<Comment> comments) {
    this.comments = comments;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  @Override
  public String toString() {
    return title;
  }
}
