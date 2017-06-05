package com.kohlschutter.boilerpipe.sax;


import com.kohlschutter.boilerpipe.document.Image;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageExtractorContentHandler implements ContentHandler {

    List<Image> linksHighlight = new ArrayList<Image>();

    private final BitSet contentBitSet = new BitSet();

    private List<Image> linksBuffer = new ArrayList<Image>();

    private int inIgnorableElement = 0;
    private int characterElementIdx = 0;

    private boolean inHighlight = false;


    public List<Image> getLinksHighlight() {
        return linksHighlight;
    }

    public BitSet getContentBitSet() {
        return contentBitSet;
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

        try {
            if (inIgnorableElement == 0) {
                if (inHighlight && "IMG".equalsIgnoreCase(localName)) {
                    String src = atts.getValue("src");
                    if (src != null && src.length() > 0) {
                        linksBuffer.add(new Image(src, atts.getValue("width"), atts.getValue("height"), atts
                            .getValue("alt")));
                    }
                }
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
                //
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
            if (!highlight) {
                if (length == 0) {
                    return;
                }
                boolean justWhitespace = true;
                for (int i = start; i < start + length; i++) {
                    if (!Character.isWhitespace(ch[i])) {
                        justWhitespace = false;
                        break;
                    }
                }
                if (justWhitespace) {
                    return;
                }
            }

            inHighlight = highlight;
            if (inHighlight) {
                linksHighlight.addAll(linksBuffer);
                linksBuffer.clear();
            }
        }
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }


    private static final TagAction TA_IGNORABLE_ELEMENT = new TagAction() {
        void beforeStart(final ImageExtractorContentHandler contentHandler, final String localName) {
            contentHandler.incrementInIgnorableElement();
        }

        void afterEnd(final ImageExtractorContentHandler contentHandler, final String localName) {
            contentHandler.decrementInIgnorableElement();
        }
    };

    private static Map<String, TagAction> TAG_ACTIONS = new HashMap<String, TagAction>();
    static {
        TAG_ACTIONS.put("STYLE", TA_IGNORABLE_ELEMENT);
        TAG_ACTIONS.put("SCRIPT", TA_IGNORABLE_ELEMENT);
        TAG_ACTIONS.put("OPTION", TA_IGNORABLE_ELEMENT);
        TAG_ACTIONS.put("NOSCRIPT", TA_IGNORABLE_ELEMENT);
        TAG_ACTIONS.put("EMBED", TA_IGNORABLE_ELEMENT);
        TAG_ACTIONS.put("APPLET", TA_IGNORABLE_ELEMENT);
        TAG_ACTIONS.put("LINK", TA_IGNORABLE_ELEMENT);

        TAG_ACTIONS.put("HEAD", TA_IGNORABLE_ELEMENT);
    }

    private abstract static class TagAction {
        void beforeStart(final ImageExtractorContentHandler contentHandler, final String localName) {
        }

        void afterStart(final ImageExtractorContentHandler contentHandler, final String localName) {
        }

        void beforeEnd(final ImageExtractorContentHandler contentHandler, final String localName) {
        }

        void afterEnd(final ImageExtractorContentHandler contentHandler, final String localName) {
        }
    }

}
