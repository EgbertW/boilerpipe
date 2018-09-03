package nl.pointpro.boilerpipeng.sax;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class HTMLHighlighterContentHandler implements ContentHandler {

    StringBuilder html = new StringBuilder();

    private boolean outputHighlightOnly = false;

    private String extraStyleSheet = "\n<style type=\"text/css\">\n" + ".x-boilerpipe-mark1 {"
        + " text-decoration:none; " + "background-color: #ffff42 !important; "
        + "color: black !important; " + "display:inline !important; "
        + "visibility:visible !important; }\n" + //
        "</style>\n";

    private String preHighlight = "<span class=\"x-boilerpipe-mark1\">";
    private String postHighlight = "</span>";

    private int inIgnorableElement = 0;
    private int characterElementIdx = 0;
    private BitSet contentBitSet = new BitSet();

    private Map<String, Set<String>> tagWhitelist = null;


    public StringBuilder getHtml() {
        return html;
    }

    public void setHtml(StringBuilder html) {
        this.html = html;
    }

    public BitSet getContentBitSet() {
        return contentBitSet;
    }

    public void setContentBitSet(BitSet contentBitSet) {
        this.contentBitSet = contentBitSet;
    }

    /**
     * @return When true, only HTML enclosed within highlighted content will be returned
     */
    public boolean isOutputHighlightOnly() {
        return outputHighlightOnly;
    }

    /**
     * Sets whether only HTML enclosed within highlighted content will be returned, or the whole HTML
     * document.
     *
     * @param outputHighlightOnly True to return highlighted HTML only
     */
    public void setOutputHighlightOnly(boolean outputHighlightOnly) {
        this.outputHighlightOnly = outputHighlightOnly;
    }

    /**
     * Returns the extra stylesheet definition that will be inserted in the HEAD element.
     *
     * By default, this corresponds to a simple definition that marks text in class
     * "x-boilerpipe-mark1" as inline text with yellow background.
     *
     * @return The extra stylesheet for the HEAD
     */
    public String getExtraStyleSheet() {
        return extraStyleSheet;
    }

    /**
     * Sets the extra stylesheet definition that will be inserted in the HEAD element.
     *
     * To disable, set it to the empty string: ""
     *
     * @param extraStyleSheet Plain HTML
     */
    public void setExtraStyleSheet(String extraStyleSheet) {
        this.extraStyleSheet = extraStyleSheet;
    }

    /**
     * Returns the string that will be inserted before any highlighted HTML block.
     *
     * By default, this corresponds to <code>&lt;span class=&quot;x-boilerpipe-mark1&quot;&gt;</code>
     *
     * @return The text inserted prior to any highlighted HTML
     */
    public String getPreHighlight() {
        return preHighlight;
    }

    /**
     * Sets the string that will be inserted prior to any highlighted HTML block.
     *
     * To disable, set it to the empty string: ""
     *
     * @param preHighlight The text to insert prior to any highlighted HTML
     */
    public void setPreHighlight(String preHighlight) {
        this.preHighlight = preHighlight;
    }

    /**
     * Returns the string that will be inserted after any highlighted HTML block.
     *
     * By default, this corresponds to <code>&lt;/span&gt;</code>
     *
     * @return The string that will be inserted after any highlight HTML.
     */
    public String getPostHighlight() {
        return postHighlight;
    }

    /**
     * Sets the string that will be inserted after any highlighted HTML block.
     *
     * To disable, set it to the empty string: ""
     *
     * @param postHighlight The string inserted after highlighted HTML
     */
    public void setPostHighlight(String postHighlight) {
        this.postHighlight = postHighlight;
    }

    public void incrementInIgnorableElement() {
        inIgnorableElement++;
    }

    public void decrementInIgnorableElement() {
        inIgnorableElement--;
    }


    public void endDocument() throws SAXException {
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    public void startDocument() throws SAXException {
    }

    public void startElement(String uri, String localName, String qName, Attributes atts)
        throws SAXException {
        TagAction ta = TAG_ACTIONS.get(localName);
        if (ta != null) {
            ta.beforeStart(this, localName);
        }

        // HACK: remove existing highlight
        boolean ignoreAttrs = false;
        if ("SPAN".equalsIgnoreCase(localName)) {
            String classVal = atts.getValue("class");
            if ("x-boilerpipe-mark1".equals(classVal)) {
                ignoreAttrs = true;
            }
        }

        try {
            if (inIgnorableElement == 0) {
                if (outputHighlightOnly) {
                    // boolean highlight = contentBitSet
                    // .get(characterElementIdx);

                    // if (!highlight) {
                    // return;
                    // }
                }

                final Set<String> whitelistAttributes;
                if (tagWhitelist == null) {
                    whitelistAttributes = null;
                } else {
                    whitelistAttributes = tagWhitelist.get(qName);
                    if (whitelistAttributes == null) {
                        // skip
                        return;
                    }
                }

                html.append('<');
                html.append(qName);
                if (!ignoreAttrs) {
                    final int numAtts = atts.getLength();
                    for (int i = 0; i < numAtts; i++) {
                        final String attr = atts.getQName(i);

                        if (whitelistAttributes != null && !whitelistAttributes.contains(attr)) {
                            // skip
                            continue;
                        }

                        final String value = atts.getValue(i);
                        html.append(' ');
                        html.append(attr);
                        html.append("=\"");
                        html.append(xmlEncode(value));
                        html.append("\"");
                    }
                }
                html.append('>');
            }
        } finally {
            if (ta != null) {
                ta.afterStart(this, localName);
            }
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        TagAction ta = TAG_ACTIONS.get(localName);
        if (ta != null) {
            ta.beforeEnd(this, localName);
        }

        try {
            if (inIgnorableElement == 0) {
                if (outputHighlightOnly) {
                    // boolean highlight = contentBitSet
                    // .get(characterElementIdx);

                    // if (!highlight) {
                    // return;
                    // }
                }

                if (tagWhitelist != null && !tagWhitelist.containsKey(qName)) {
                    // skip
                    return;
                }

                html.append("</");
                html.append(qName);
                html.append('>');
            }
        } finally {
            if (ta != null) {
                ta.afterEnd(this, localName);
            }
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        characterElementIdx++;
        if (inIgnorableElement == 0) {

            boolean highlight = contentBitSet.get(characterElementIdx);

            if (!highlight && outputHighlightOnly) {
                return;
            }

            if (highlight) {
                html.append(preHighlight);
            }
            html.append(xmlEncode(String.valueOf(ch, start, length)));
            if (highlight) {
                html.append(postHighlight);
            }
        }
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }


    private static String xmlEncode(final String in) {
        if (in == null) {
            return "";
        }
        char c;
        StringBuilder out = new StringBuilder(in.length());

        for (int i = 0; i < in.length(); i++) {
            c = in.charAt(i);
            switch (c) {
                case '<':
                    out.append("&lt;");
                    break;
                case '>':
                    out.append("&gt;");
                    break;
                case '&':
                    out.append("&amp;");
                    break;
                case '"':
                    out.append("&quot;");
                    break;
                default:
                    out.append(c);
            }
        }

        return out.toString();
    }

    public Map<String, Set<String>> getTagWhitelist() {
        return tagWhitelist;
    }

    public void setTagWhitelist(Map<String, Set<String>> tagWhitelist) {
        this.tagWhitelist = tagWhitelist;
    }

    public abstract static class TagAction {
        void beforeStart(final HTMLHighlighterContentHandler contentHandler, final String localName) {
        }

        void afterStart(final HTMLHighlighterContentHandler contentHandler, final String localName) {
        }

        void beforeEnd(final HTMLHighlighterContentHandler contentHandler, final String localName) {
        }

        void afterEnd(final HTMLHighlighterContentHandler contentHandler, final String localName) {
        }
    }

    private static final TagAction TA_IGNORABLE_ELEMENT = new TagAction() {
        void beforeStart(final HTMLHighlighterContentHandler contentHandler, final String localName) {
            contentHandler.incrementInIgnorableElement();
        }

        void afterEnd(final HTMLHighlighterContentHandler contentHandler, final String localName) {
            contentHandler.decrementInIgnorableElement();
        }
    };

    private static final TagAction TA_HEAD = new TagAction() {
        void beforeStart(final HTMLHighlighterContentHandler contentHandler, final String localName) {
            contentHandler.incrementInIgnorableElement();
        }

        void beforeEnd(final HTMLHighlighterContentHandler contentHandler, String localName) {
            contentHandler.getHtml().append(contentHandler.getExtraStyleSheet());
        }

        void afterEnd(final HTMLHighlighterContentHandler contentHandler, final String localName) {
            contentHandler.decrementInIgnorableElement();
        }
    };

    public static Map<String, TagAction> TAG_ACTIONS = new HashMap<String, TagAction>();
    static {
        TAG_ACTIONS.put("STYLE", TA_IGNORABLE_ELEMENT);
        TAG_ACTIONS.put("SCRIPT", TA_IGNORABLE_ELEMENT);
        TAG_ACTIONS.put("OPTION", TA_IGNORABLE_ELEMENT);
        TAG_ACTIONS.put("NOSCRIPT", TA_IGNORABLE_ELEMENT);
        TAG_ACTIONS.put("OBJECT", TA_IGNORABLE_ELEMENT);
        TAG_ACTIONS.put("EMBED", TA_IGNORABLE_ELEMENT);
        TAG_ACTIONS.put("APPLET", TA_IGNORABLE_ELEMENT);
        // NOTE: you might want to comment this out:
        TAG_ACTIONS.put("LINK", TA_IGNORABLE_ELEMENT);

        TAG_ACTIONS.put("HEAD", TA_HEAD);
    }

}
