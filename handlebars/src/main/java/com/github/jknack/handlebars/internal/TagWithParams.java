package com.github.jknack.handlebars.internal;

import java.util.List;

public class TagWithParams {
    private String tag;
    private List<Param> params;
    public TagWithParams(String tag, List<Param> params) {
        this.tag = tag;
        this.params = params;

    }
    public String getTag() {
        return tag;
    }

    public List<Param> getParams() {
        return params;
    }

}
