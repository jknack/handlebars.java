package specs;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import com.github.edgarespina.handlerbars.ResourceLocator;

public class SpecResourceLocator extends ResourceLocator {
  private Map<String, Object> spec;

  public SpecResourceLocator(final Map<String, Object> spec) {
    this.spec = spec;
  }

  @Override
  protected Reader read(final String uri) throws IOException {
    @SuppressWarnings("unchecked")
    Map<String, String> templates =
        (Map<String, String>) spec.get("partials");
    if (templates == null) {
      templates = new HashMap<String, String>();
    }
    templates.put("template", (String) spec.get("template"));
    String template = templates.get(uri);
    return template == null ? null : new StringReader(template);
  }

}
