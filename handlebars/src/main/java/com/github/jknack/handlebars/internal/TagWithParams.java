/**
 * Copyright (c) 2012-2015 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.internal;
import com.github.jknack.handlebars.TagType;

import java.util.List;

/**
 * A Handlebars tag with its associated parameters and tag type.
 *
 */
public class TagWithParams {

    /**
     * The tag itself.
     */
    private String tag;

    /**
     * A list of params for the tag.
     */
    private List<Param> params;

    /**
     * The tag type of the tag.
     */
    private TagType tagType;

    /**
     * Creates a new {@link TagWithParam}.
     *
     * @param tag The tag itself.
     * @param params The associated parameters for the tag.
     * @param tagType The tag type for the tag.
     */
    TagWithParams(final String tag, final List<Param> params, final TagType tagType) {
        this.tag = tag;
        this.params = params;
        this.tagType = tagType;
    }

    /**
     * The tag.
     * @return The tag.
     */
    public String getTag() {
        return tag;
    }

    /**
     * The tag's parameters.
     * @return The tag's parameters.
     */
    public List<Param> getParams() {
        return params;
    }

    /**
     * The tag type.
     * @return The tag's type.
     */
    public TagType getTagType() {
        return tagType;
    }

    @Override
    public String toString() {
        return "TagWithParams(" + this.tag + ", "
                + this.params.toString() + ", " + this.tagType + ")";
    }

}
