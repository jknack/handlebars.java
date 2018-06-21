package com.github.jknack.handlebars.internal;
import com.github.jknack.handlebars.TagType;

import java.util.List;

public class TagWithParams {
    private String tag;
    private List<Param> params;
    private TagType tagType;

    TagWithParams(String tag, List<Param> params, TagType tagType) {
        this.tag = tag;
        this.params = params;
        this.tagType = tagType;
    }

    public String getTag() {
        return tag;
    }
    public List<Param> getParams() {
        return params;
    }
    public TagType getTagType() { return tagType; }

    @Override
    public String toString() {
        return "TagWithParams(" + this.tag + ", " + this.params.toString() + ", " + this.tagType + ")";
    }
}
