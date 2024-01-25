package io.noties.markwon.ext.latex;

import androidx.annotation.Nullable;

import org.commonmark.node.Node;

import java.util.regex.Pattern;

import io.noties.markwon.inlineparser.InlineProcessor;

/**
 * @since 4.3.0
 */
class JLatexMathInlineProcessor extends InlineProcessor {

    // $..$ 和 \(..\)
    private static final Pattern RE =
            Pattern.compile("(\\$)([\\s\\S]+?)(\\$)|(\\\\()([\\s\\S]+?)(\\\\))");

    @Override
    public char specialCharacter() {
        return '$';
    }

    @Nullable
    @Override
    protected Node parse() {

        final String latex = match(RE);
        if (latex == null) {
            return null;
        }

        final JLatexMathNode node = new JLatexMathNode();
        node.latex(trimWrapping(latex));
        return node;
    }

    String trimWrapping(String latex) {
        return latex.startsWith("$") & latex.endsWith("$")
                ? latex.substring(1, latex.length() - 1)
                : latex.substring(2, latex.length() - 2);
    }
}
