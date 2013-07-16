package com.github.jknack.handlebars;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class Issue200 extends AbstractTest {

    @Test
    public void actualBug() throws IOException {
        Handlebars h = newHandlebars();
        h.registerHelper("replaceHelperTest", new Helper<String>() {
            public CharSequence apply(final String text,
                final Options options) {
                    return "foo";
            }
        });
        Template t = h.compileInline("hello world: {{replaceHelperTest \"foobar\"}}");            
        assertEquals("hello world: foo", t.apply(null));
        
        h.registerHelpers(new DynamicHelperExample());

        assertEquals("hello world: bar", t.apply(null));
        
    }

}

