package specs;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import com.github.edgarespina.handlebars.TemplateLoader;

public class SpecResourceLocator extends TemplateLoader {
  private Map<String, String> templates;

  public SpecResourceLocator(final Spec spec) {
    templates = spec.partials();
    if (templates == null) {
      templates = new HashMap<String, String>();
    }
    templates.put("template", spec.template());
  }

  @Override
  public String resolve(final String uri) {
    return uri;
  }

  @Override
  protected Reader read(final String uri) throws IOException {
    String template = templates.get(uri);
    return template == null ? null : new StringReader(template);
  }

}
