/**
 * Copyright (c) 2011-2016, Qulice.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the Qulice.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Check for empty line at the start and at the end of javadoc.
 * @author Denys Skalenko (d.skalenko@gmail.com)
 * @version $Id$
 * @since 0.17
 */
public final class JavadocEmptyLineCheck extends Check {

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.CLASS_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.VARIABLE_DEF,
            TokenTypes.CTOR_DEF,
            TokenTypes.METHOD_DEF,
        };
    }

    @Override
    public void visitToken(final DetailAST ast) {
        final String[] lines = this.getLines();
        final int start = ast.getLineNo();
        final int cstart =
            JavadocEmptyLineCheck.findCommentStart(lines, start) + 1;
        if (JavadocEmptyLineCheck.isNodeHasJavaDoc(ast, cstart)) {
            if (JavadocEmptyLineCheck.isJavadocLineEmpty(lines[cstart])) {
                this.log(cstart + 1, "Empty javadoc line at the beginning");
            }
            final int cend =
                JavadocEmptyLineCheck.findCommentEnd(lines, start) - 1;
            if (JavadocEmptyLineCheck.isJavadocLineEmpty(lines[cend])) {
                this.log(cend + 1, "Empty javadoc line at the end");
            }
        }
    }

    /**
     * Check if javadoc Line Empty.
     * @param line Java doc line
     * @return Is Javadoc Line Empty
     */
    private static boolean isJavadocLineEmpty(final String line) {
        return null != line && "*".equals(line.trim());
    }

    /**
     * Check if node has java doc.
     * @param node Node to be checked for Java docs.
     * @param start Line number where comment starts.
     * @return Is node has java doc
     */
    private static boolean isNodeHasJavaDoc(final DetailAST node,
        final int start) {
        int cprevious = 0;
        final DetailAST previous = node.getPreviousSibling();
        if (null != previous) {
            cprevious = previous.getLineNo();
        }
        return start > cprevious;
    }

    /**
     * Find javadoc starting comment.
     * @param lines List of lines to check.
     * @param start Start searching from this line number.
     * @return Line number with found starting comment or -1 otherwise.
     */
    private static int findCommentStart(final String[] lines, final int start) {
        return JavadocEmptyLineCheck.findTrimmedTextUp(lines, start, "/**");
    }

    /**
     * Find javadoc ending comment.
     * @param lines List of lines to check.
     * @param start Start searching from this line number.
     * @return Line number with found ending comment, or -1 if it wasn't found.
     */
    private static int findCommentEnd(final String[] lines, final int start) {
        return JavadocEmptyLineCheck.findTrimmedTextUp(lines, start, "*/");
    }

    /**
     * Find a text in lines, by going up.
     * @param lines List of lines to check.
     * @param start Start searching from this line number.
     * @param text Text to find.
     * @return Line number with found text, or -1 if it wasn't found.
     */
    private static int findTrimmedTextUp(final String[] lines,
        final int start, final String text) {
        int found = -1;
        for (int pos = start - 1; pos >= 0; pos -= 1) {
            if (lines[pos].trim().equals(text)) {
                found = pos;
                break;
            }
        }
        return found;
    }
}
