package com.github.jknack.handlebars.i177;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue177 extends AbstractTest {

  // model classes
  public class Model1 {
    private List<String> listOfValues1 = new ArrayList<String>();

    public List<String> getListOfValues1() {
      return listOfValues1;
    }

    public void setListOfValues1(final List<String> listOfValues1) {
      this.listOfValues1 = listOfValues1;
    }
  }

  public class User {
    private String name;

    public String getName() {
      return name;
    }

    public void setName(final String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  public class Model2 {
    private List<User> users = new ArrayList<User>();

    public List<User> getUsers() {
      return users;
    }

    public void setUsers(final List<User> users) {
      this.users = users;
    }
  }

  // model map
  Map<String, Object> modelMap;

  @Before
  public void before() {
    // model map
    modelMap = new HashMap<String, Object>();

    // model1
    Model1 model1 = new Model1();
    List<String> listOfValues1 = new ArrayList<String>();
    listOfValues1.add("m1-1");
    listOfValues1.add("m1-2");
    listOfValues1.add("m1-3");
    model1.setListOfValues1(listOfValues1);
    modelMap.put("model1", model1);

    // model2
    Model2 model2 = new Model2();
    List<User> users = new ArrayList<User>();
    User u1 = new User();
    u1.setName("User 1");
    users.add(u1);
    User u2 = new User();
    u2.setName("User 2");
    users.add(u2);
    model2.setUsers(users);
    modelMap.put("model2", model2);

    // model3
    modelMap.put("model3", true);
  }

  @Test
  public void test1() throws IOException {
    shouldCompileTo("{{model1.listOfValues1.[0]}}", modelMap, "m1-1");
  }

  @Test
  public void test2() throws IOException {
    shouldCompileTo("{{model1.listOfValues1.[0]}}\n{{model2.users.[0]}}", modelMap, "m1-1\nUser 1");
  }

  @Test
  public void test3() throws IOException {
    shouldCompileTo("{{model1.listOfValues1.[0]}}\n{{model2.users.[0].name}}", modelMap,
        "m1-1\nUser 1");
  }

  @Test
  public void test3a() throws IOException {
    shouldCompileTo("{{model2.users.[0].name}}", modelMap, "User 1");
  }

  @Test
  public void test4() throws IOException {
    shouldCompileTo(
        "{{model1.listOfValues1.[0]}}\n{{#if model3}}\n{{model2.users.[0].name}}\n{{/if}}",
        modelMap, "m1-1\n\nUser 1\n");
  }
}
