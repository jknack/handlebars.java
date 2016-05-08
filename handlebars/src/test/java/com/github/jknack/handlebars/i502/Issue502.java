package com.github.jknack.handlebars.i502;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue502 extends v4Test {

	@Test
	public void testMapOfArraysStringKey() throws IOException {
		Map<String,Collection<String>> data = new TreeMap<>();
		Collection<String> d1 = new ArrayList<>();
		d1.add("1");
		d1.add("2");
		d1.add("3");
		
		Collection<String> d2 = new ArrayList<>();
		d2.add("4");
		d2.add("5");
		d2.add("6");
		
		data.put("a", d1);
		data.put("b", d2);
		
		shouldCompileTo("{{#each data}}{{ @key }} - {{#each . }}Val:{{.}}{{/each}}{{/each}}", $("hash",$("data", data)), 
		            "a - Val:1Val:2Val:3b - Val:4Val:5Val:6");
	}

	@Test
	public void testMapOfArraysObjectKey() throws IOException {
		Map<Object,Collection<String>> data = new TreeMap<>();
		Collection<String> d1 = new ArrayList<>();
		d1.add("1");
		d1.add("2");
		d1.add("3");
		
		Collection<String> d2 = new ArrayList<>();
		d2.add("4");
		d2.add("5");
		d2.add("6");
		
		data.put(123, d1);
		data.put(456, d2);
		
		shouldCompileTo("{{#each data}}{{ @key }} - {{#each . }}Val:{{.}}{{/each}}{{/each}}", $("hash",$("data", data)), 
		            "123 - Val:1Val:2Val:3456 - Val:4Val:5Val:6");
	}
}
