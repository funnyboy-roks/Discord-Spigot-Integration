package com.funnyboyroks.markdown;

import net.md_5.bungee.api.ChatColor;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownProcessor {
    private static final CharacterProtector HTML_PROTECTOR = new CharacterProtector();
    private static final CharacterProtector CHAR_PROTECTOR = new CharacterProtector();

    /**
     * Perform the conversion from Markdown to HTML.
     *
     * @param txt - input in markdown format
     * @return HTML block corresponding to txt passed in.
     */
    public String markdown(String txt) {
        if (txt == null) {
            txt = "";
        }
        TextEditor text = new TextEditor(txt);

        text.detabify();
        text.deleteAll("^[ ]+$");
        text = runBlockGamut(text);
        unEscapeSpecialChars(text);

        return text.toString();
    }

    private TextEditor encodeBackslashEscapes(TextEditor text) {
        char[] normalChars = "`_>!".toCharArray();
        char[] escapedChars = "*{}[]()#+-.".toCharArray();

        // Two backslashes in a row
        text.replaceAllLiteral("\\\\\\\\", CHAR_PROTECTOR.encode("\\"));

        // Normal characters don't require a backslash in the regular expression
        encodeEscapes(text, normalChars, "\\\\");
        encodeEscapes(text, escapedChars, "\\\\\\");

        return text;
    }

    private TextEditor encodeEscapes(TextEditor text, char[] chars, String slashes) {
        for (char ch : chars) {
            String regex = slashes + ch;
            text.replaceAllLiteral(regex, CHAR_PROTECTOR.encode(String.valueOf(ch)));
        }
        return text;
    }

    public TextEditor runBlockGamut(TextEditor text) {
        doCodeBlocks(text);
        return formParagraphs(text);
    }

    private TextEditor formParagraphs(TextEditor markup) {
        markup.deleteAll("\\A\\n+");
        markup.deleteAll("\\n+\\z");

        String paragraph = markup.toString();
        String decoded = HTML_PROTECTOR.decode(paragraph);
        if (decoded != null) {
            paragraph = decoded;
        } else {
            paragraph = runSpanGamut(new TextEditor(paragraph)).toString();
        }
        return new TextEditor(paragraph);
    }


    private TextEditor doAutoLinks(TextEditor markup) {
        markup.replaceAll("(https?:[^'\\\">\\\\\\s]+)", ChatColor.AQUA + "$1" + ChatColor.RESET);
        return markup;
    }

    private void unEscapeSpecialChars(TextEditor ed) {
        for (String hash : CHAR_PROTECTOR.getAllEncodedTokens()) {
            String plaintext = CHAR_PROTECTOR.decode(hash);
            ed.replaceAllLiteral(hash, plaintext);
        }
    }

    private TextEditor doCodeBlocks(TextEditor markup) {
        Pattern p = Pattern.compile("" +
                "(?:\\n\\n|\\A)" +
                "((?:" +
                "(?:[ ]{4})" +
                ".*\\n+" +
                ")+" +
                ")" +
                "((?=^[ ]{0,4}\\S)|\\Z)", Pattern.MULTILINE);
        return markup.replaceAll(p, new Replacement() {
        			private static final String LANG_IDENTIFIER = "lang:";
                    public String replacement(Matcher m) {
                        String codeBlock = m.group(1);
                        TextEditor ed = new TextEditor(codeBlock);
                        ed.outdent();
                        encodeCode(ed);
                        ed.detabify().deleteAll("\\A\\n+").deleteAll("\\s+\\z");
                        String text = ed.toString();
                        String out;
                        out = genericCodeBlock(text);
                        return out;
                    }

                    public String genericCodeBlock(String text)
                    {
                    	String codeBlockTemplate = ChatColor.GRAY + "%s" + ChatColor.RESET;
                        return String.format(codeBlockTemplate, text);
                    }
                });
    }

    private void encodeCode(TextEditor ed) {
        ed.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
        ed.replaceAll("_", CHAR_PROTECTOR.encode("_"));
        ed.replaceAll("\\{", CHAR_PROTECTOR.encode("{"));
        ed.replaceAll("\\}", CHAR_PROTECTOR.encode("}"));
        ed.replaceAll("\\[", CHAR_PROTECTOR.encode("["));
        ed.replaceAll("\\]", CHAR_PROTECTOR.encode("]"));
        ed.replaceAll("\\\\", CHAR_PROTECTOR.encode("\\"));
    }

    private String join(String separator, String[] strings) {
        int length = strings.length;
        StringBuilder buf = new StringBuilder();
        if (length > 0) {
            buf.append(strings[0]);
            for (int i = 1; i < length; i++) {
                buf.append(separator).append(strings[i]);
            }
        }
        return buf.toString();
    }

    public TextEditor runSpanGamut(TextEditor text) {
        text = escapeSpecialCharsWithinTagAttributes(text);
        text = doCodeSpans(text);
        text = encodeBackslashEscapes(text);

        doAutoLinks(text);

        text = escapeSpecialCharsWithinTagAttributes(text);

        doMCFormat(text);

        return text;
    }

    private TextEditor escapeSpecialCharsWithinTagAttributes(TextEditor text) {
        Collection<com.funnyboyroks.markdown.HTMLToken> tokens = text.tokenizeHTML();
        TextEditor newText = new TextEditor("");

        for (HTMLToken token : tokens) {
            String value = token.getText();
            if (token.isTag()) {
                value = value.replaceAll("\\\\", CHAR_PROTECTOR.encode("\\"));
                value = value.replaceAll("`", CHAR_PROTECTOR.encode("`"));
                value = value.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
                value = value.replaceAll("_", CHAR_PROTECTOR.encode("_"));
            }
            newText.append(value);
        }

        return newText;
    }

    private TextEditor doMCFormat(TextEditor markup) {
        markup.replaceAll("(\\*\\*)(?=\\S)(.+?[*]*)(?<=\\S)\\1", ChatColor.BOLD + "$2" + ChatColor.RESET);
        markup.replaceAll("(__)(?=\\S)(.+?[_]*)(?<=\\S)\\1", ChatColor.UNDERLINE + "$2" + ChatColor.RESET);
        markup.replaceAll("(~~)(?=\\S)(.+?[~]*)(?<=\\S)\\1", ChatColor.STRIKETHROUGH + "$2" + ChatColor.RESET);
        markup.replaceAll("(\\*|_)(?=\\S)(.+?)(?<=\\S)\\1", ChatColor.ITALIC + "$2" + ChatColor.RESET);
        return markup;
    }

    private TextEditor doCodeSpans(TextEditor markup) {
        return markup.replaceAll(Pattern.compile("(?<!\\\\)(`+)(.+?)(?<!`)\\1(?!`)"), m -> {
            String code = m.group(2);
            TextEditor subEditor = new TextEditor(code);
            subEditor.deleteAll("^[ \\t]+").deleteAll("[ \\t]+$");
            encodeCode(subEditor);
            return ChatColor.GRAY + "" + subEditor.toString() + "" + ChatColor.RESET;
        });
    }

    public static void main(String[] args) {
        StringBuilder buf = new StringBuilder();
        char[] cbuf = new char[1024];
        java.io.Reader in = new java.io.InputStreamReader(System.in);
        try {
            int charsRead = in.read(cbuf);
            while (charsRead >= 0) {
                buf.append(cbuf, 0, charsRead);
                charsRead = in.read(cbuf);
            }
            System.out.println(new MarkdownProcessor().markdown(buf.toString()));
        } catch (java.io.IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
            System.exit(1);
        }
    }
}
