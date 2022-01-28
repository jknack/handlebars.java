package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.cache.ConcurrentMapTemplateCache;
import com.github.jknack.handlebars.io.StringTemplateSource;
import com.github.jknack.handlebars.io.TemplateSource;
import com.github.jknack.handlebars.io.URLTemplateSource;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class Issue686 {

    @Test
    public void compileStringsWithSameHashCode() throws IOException {
        Handlebars handlebars = new Handlebars().with(new ConcurrentMapTemplateCache());

        // Strings "Aa" and "BB" have same hashCode
        assertEquals("Aa", handlebars.compileInline("Aa").apply(new Object()));
        assertEquals("BB", handlebars.compileInline("BB").apply(new Object()));
    }

    @Test
    public void compileTemplateSourcesWithSameFilename() throws IOException {
        Handlebars handlebars = new Handlebars().with(new ConcurrentMapTemplateCache());

        TemplateSource stringTemplateSource = new StringTemplateSource("filename", "string");
        TemplateSource urlTemplateSource = new URLTemplateSource("filename", getClass().getResource(
                "/template.hbs"));

        assertEquals("string", handlebars.compile(stringTemplateSource).apply(new Object()));
        assertEquals("template.hbs", handlebars.compile(urlTemplateSource).apply(new Object()));
    }
}
