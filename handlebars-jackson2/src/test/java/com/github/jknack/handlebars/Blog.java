/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

public class Blog {

  public static class Views {
    public static class Public {}
  }

  @JsonView(Views.Public.class)
  private String title;

  private String body;

  private List<Comment> comments = new ArrayList<Comment>();

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
}
