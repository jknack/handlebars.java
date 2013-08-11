describe("Handlebars Helpers", function() {
  it("root", function() {
    var template = Handlebars.templates[this.description],
      found = template('world');
    expect(found).toBe('Hello world!');
  });

  it("partial/base", function() {
    var template = Handlebars.templates[this.description],
      found = template({});
    expect(found).toBe("I'm the base partial\nI'm the child partial");
  });

  it("level1/level1", function() {
    var template = Handlebars.templates[this.description],
      found = template({});
    expect(found).toBe("<h1>I'm </h1>");
  });

  it("i18njs", function() {
    var template = Handlebars.templates[this.description],
      found = template({});
    expect(found.replace(/^\s+|\s+$/g, '')).toBe("Hi\nHi Edgar!\n[a, b, c]");
  });
});
