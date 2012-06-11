package specs;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.github.edgarespina.handlerbars.ResourceLocator;

public class SpecResourceLocator extends ResourceLocator {
  private Spec spec;

  public SpecResourceLocator(final Spec spec) {
    this.spec = spec;
  }

  @Override
  protected Reader read(final URI uri) throws IOException {
    Map<String, String> templates =
        spec.partials();
    if (templates == null) {
      templates = new HashMap<String, String>();
    }
    templates.put("template", spec.template());
    String template = templates.get(uri.toString());
    return template == null ? null : new StringReader(template);
  }

}
