package com.github.edgarespina.handlebars.custom;

public class Comment {
  private String author;

  private String comment;

  public Comment(final String author, final String comment) {
    this.author = author;
    this.comment = comment;
  }

  public Comment() {
  }

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
