package com.github.jknack.handlebars.internal;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 */
public class PartialBlockForwardingTemplate extends BaseTemplate {
    /**
     * The block to be passed as partial-block.
     */
    private final Template block;

    /**
     * The previous partial-block definition of the template which contains this partial.
     */
    private final Template parentPartialBlock;

    /**
     * The callee of the parent partial.
     */
    private final Template callee;

    /**
     * Constructs a PartialBlockForwardingTemplate.
     *
     * @param parent the parent partial
     * @param block the block to be passed as partial-block.
     * @param parentPartialBlock the previous partial-block definition of
     *                          the template which contains this partial.
     * @param callee the template that renders the parent
     * @param handlebars handlebars
     */
    public PartialBlockForwardingTemplate(
            final Template parent,
            final Template block,
            final Template parentPartialBlock,
            final Template callee,
            final Handlebars handlebars
    ) {
        super(handlebars);
        this.block = block;
        this.parentPartialBlock = parentPartialBlock;
        this.callee = callee;
        this.filename(block.filename());
        this.position(parent.position()[0], parent.position()[1]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void merge(final Context context, final Writer writer) throws IOException {
        LinkedList<Map<String, Template>> partials = context.data(Context.INLINE_PARTIALS);
        Map<String, Template> inlineTemplates = partials.getLast();
        Template oldPartialBlock = inlineTemplates.get("@partial-block");
        Template oldCallee = context.data("callee");

        context.data("callee", callee);
        inlineTemplates.put("@partial-block", parentPartialBlock);
        block.apply(context, writer);
        inlineTemplates.put("@partial-block", oldPartialBlock);
        context.data("callee", oldCallee);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String text() {
        return block.text();
    }
}
