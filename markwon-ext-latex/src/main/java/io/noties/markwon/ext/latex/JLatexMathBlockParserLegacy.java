package io.noties.markwon.ext.latex;

import android.util.Log;

import org.commonmark.node.Block;
import org.commonmark.parser.block.AbstractBlockParser;
import org.commonmark.parser.block.AbstractBlockParserFactory;
import org.commonmark.parser.block.BlockContinue;
import org.commonmark.parser.block.BlockStart;
import org.commonmark.parser.block.MatchedBlockParser;
import org.commonmark.parser.block.ParserState;

/**
 * @since 4.3.0 (although it is just renamed parser from previous versions)
 */
class JLatexMathBlockParserLegacy extends AbstractBlockParser {

    private static final String TAG = "JLatexMathBlockParserLe";

    private final JLatexMathBlock block = new JLatexMathBlock();

    private final StringBuilder builder = new StringBuilder();

    private boolean isClosed;

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public BlockContinue tryContinue(ParserState parserState) {

        if (isClosed) {
            return BlockContinue.finished();
        }

        return BlockContinue.atIndex(parserState.getIndex());
    }

    @Override
    public void addLine(CharSequence line) {
        Log.d(TAG, "addLine: " + line);

        if (builder.length() > 0) {
            builder.append('\n');
        }

        builder.append(line);

        final int length = builder.length();
        if (length > 1) {
            isClosed = isEndWithDoubleDollar(length)
                    || isEndWithSlashSummaryIssue(length);
            if (isClosed) {
                builder.replace(length - 2, length, "");
            }
        }
    }

    private boolean isEndWithDoubleDollar(int length) {
        return '$' == builder.charAt(length - 1)
                && '$' == builder.charAt(length - 2);
    }

    private boolean isEndWithSlashSummaryIssue(int length) {
        return ']' == builder.charAt(length - 1)
                && '\\' == builder.charAt(length - 2);
    }

    @Override
    public void closeBlock() {
        block.latex(builder.toString());
    }

    public static class Factory extends AbstractBlockParserFactory {

        @Override
        public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {
            final CharSequence line = state.getLine();
            Log.d(TAG, "tryStart: " + line);
            final int length = line != null ? line.length() : 0;

            if (length > 1) {
                if (isStartWithDoubleDollar(line.toString().trim())
                        || isStartWithSlashSummaryIssue(line.toString().trim())) {
                    int startIndex = 2;
                    for (int i = 0; i < line.length(); i++) {
                        if (' ' == line.charAt(i)) {
                            startIndex++;
                        } else {
                            break;
                        }
                    }
                    return BlockStart.of(new JLatexMathBlockParserLegacy())
                            .atIndex(state.getIndex() + startIndex);
                }
            }

            return BlockStart.none();
        }

        private static boolean isStartWithDoubleDollar(CharSequence line) {
            return '$' == line.charAt(0)
                    && '$' == line.charAt(1);
        }

        private static boolean isStartWithSlashSummaryIssue(CharSequence line) {
            return '\\' == line.charAt(0)
                    && '[' == line.charAt(1);
        }
    }
}
