/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package full;

import java.util.Objects;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.misc.Interval;

public abstract class HbsLexerBase extends Lexer {

    protected String startTag = "{{";
    protected String endTag = "}}";

    private String tempStartDelim = null;
    private String tempEndDelim = null;

    protected int currentTagType = 0;

    public HbsLexerBase(CharStream input) {
        super(input);
    }

    public void setDelimiters(String start, String end) {
        if (Objects.requireNonNull(start, "Start delimiter").isEmpty()
                || Objects.requireNonNull(end, "End delimiter").isEmpty()) {
            throw new IllegalArgumentException("Delimiters must not be empty");
        }
        this.startTag = start;
        this.endTag = end;
    }

    protected void captureDelimiter(String delim) {
        if (tempStartDelim == null) {
            tempStartDelim = delim;
        } else if (tempEndDelim == null) {
            tempEndDelim = delim;
        }
    }

    public void handleEscapeSequence() {
        var text = getText();
        int count = text.length();

        // Handlebars Spec: Halve the slashes. Odd = escape tag. Even = evaluate tag.
        int outSlashes = count / 2;
        boolean isEscaped = (count % 2) != 0;

        var sb = new StringBuilder();
        for (int i = 0; i < outSlashes; i++) {
            sb.append("\\");
        }

        if (isEscaped) {
            // WE MUST CONSUME THE TAG to hide it from the Parser.
            // By consuming it here, we degrade the Handlebars tag into pure literal text.
            int start = _input.index();

            // Advance the cursor past the delimiters
            if (peekStart(startTag)) advanceStart(startTag);
            else if (peekStart("{")) advanceStart("{");
            else if (peekStart("!--")) advanceStart("!--");
            else if (peekStart("!")) advanceStart("!");
            else if (peekStart("=")) advanceStart("=");
            else if (peekStart("<")) advanceStart("<");
            else if (peekStart("$")) advanceStart("$");
            else advanceStart(""); // Fallback for standard tags

            int end = _input.index();

            // Extract exactly what we just bypassed and append it to our TEXT token
            var consumedDelim = _input.getText(Interval.of(start, end - 1));
            sb.append(consumedDelim);
        }

        setText(sb.toString());
    }

    protected void applyDelimiters() {
        if (tempStartDelim != null && tempEndDelim != null) {
            // Overwrite the actual variables your peek/advance methods use!
            this.startTag = tempStartDelim;
            this.endTag = tempEndDelim;
        }

        // Reset for the next time delimiters are changed
        tempStartDelim = null;
        tempEndDelim = null;
    }

    /** Determines if the current rule being evaluated belongs to a comment boundary. */
    private boolean isCommentRule(String suffixOrPrefix, boolean isStart) {
        if (isStart) {
            // "!--" is START_BLOCK_COMMENT, "!" is START_COMMENT
            return "!--".equals(suffixOrPrefix) || "!".equals(suffixOrPrefix);
        } else {
            // "--" is END_BLOCK_COMMENT, or empty string when inside COMMENT_MODE
            return "--".equals(suffixOrPrefix)
                    || ("".equals(suffixOrPrefix) && "COMMENT_MODE".equals(getModeNames()[_mode]));
        }
    }

    private boolean isTripleVar(String suffixOrPrefix, boolean isStart) {
        if (isStart) {
            return "{".equals(suffixOrPrefix);
        } else {
            return "}".equals(suffixOrPrefix);
        }
    }

    // --- PEEK ACTIONS ---

    protected boolean peekStart(String suffix) {
        if (isCommentRule(suffix, true)) {
            // Comments are allowed to lazily match with an optional tilde
            return peekAhead(this.startTag + suffix) || peekAhead(this.startTag + "~" + suffix);
        }
        if (isTripleVar(suffix, true)) {
            return peekAhead(this.startTag + "~" + suffix) || peekAhead(this.startTag + suffix);
        }
        // Standard tags must use the strict untouched matching
        return peekAhead(this.startTag + suffix);
    }

    protected boolean peekEnd(String prefix) {
        if (isCommentRule(prefix, false)) {
            // Comments are allowed to lazily match with an optional tilde
            return peekAhead(prefix + this.endTag) || peekAhead(prefix + "~" + this.endTag);
        }
        if (isTripleVar(prefix, false)) {
            return peekAhead(prefix + "~" + this.endTag) || peekAhead(prefix + this.endTag);
        }
        // Standard tags must use the strict untouched matching
        return peekAhead(prefix + this.endTag);
    }

    private boolean peekAhead(String text) {
        if (text.isEmpty()) return true;
        for (int i = 0; i < text.length(); i++) {
            if (_input.LA(i + 1) != text.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    // --- CONSUME ACTIONS ---

    protected void advanceStart(String suffix) {
        switch (suffix) {
            case "{{" -> currentTagType = 2;
            case "{" -> currentTagType = 1;
            default -> currentTagType = 0;
        }
        if (isCommentRule(suffix, true) || isTripleVar(suffix, true)) {
            advanceWithTile(this.startTag, suffix);
        } else {
            // Preserves the tilde so it can be emitted as a WS_CONTROL token
            int advanceBy = this.startTag.length() + suffix.length() - 1;
            if (advanceBy > 0) _input.seek(_input.index() + advanceBy);
        }
    }

    private void advanceWithTile(String startTag, String suffix) {
        // Special advanced logic to safely strip the tilde from comment text streams
        var fullWithout = startTag + suffix;
        var fullWith = startTag + "~" + suffix;

        var remainWithout = fullWithout.substring(1);
        var remainWith = fullWith.substring(1);

        int advanceBy = 0;
        if (peekAhead(remainWith)) {
            advanceBy = remainWith.length();
        } else if (peekAhead(remainWithout)) {
            advanceBy = remainWithout.length();
        }

        if (advanceBy > 0) _input.seek(_input.index() + advanceBy);
    }

    protected void advanceEnd(String prefix) {
        if (isCommentRule(prefix, false) || isTripleVar(prefix, false)) {
            advanceWithTile(prefix, this.endTag);
        } else {
            // Preserves the tilde so it can be emitted as a WS_CONTROL token
            int advanceBy = prefix.length() + this.endTag.length() - 1;
            if (advanceBy > 0) _input.seek(_input.index() + advanceBy);
        }
    }
}
