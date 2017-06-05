package com.kohlschutter.boilerpipe.sax;

import com.kohlschutter.boilerpipe.document.Image;
import com.kohlschutter.boilerpipe.document.Media;
import com.kohlschutter.boilerpipe.document.VimeoVideo;
import com.kohlschutter.boilerpipe.document.YoutubeVideo;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MediaExtractorContentHandler implements ContentHandler {

    List<Media> linksHighlight = new ArrayList<Media>();
    private List<Media> linksBuffer = new ArrayList<Media>();

    private int inIgnorableElement = 0;
    private int characterElementIdx = 0;
    private final BitSet contentBitSet = new BitSet();

    private boolean inHighlight = false;


    public List<Media> getLinksHighlight() {
        return linksHighlight;
    }

    public BitSet getContentBitSet() {
        return contentBitSet;
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

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        TagAction ta = TAG_ACTIONS.get(localName);
        if (ta != null) {
            ta.beforeStart(this, localName);
        }

        try {
            if (inIgnorableElement == 0) {
                if (inHighlight && "IFRAME".equalsIgnoreCase(localName)) {
                    String src = atts.getValue("src");
                    if (src != null) {
                        src = src.replaceAll("\\\\\"", "");
                    }
                    if (src != null && src.length() > 0 && src.contains("youtube.com/embed/")) {
                        String originUrl = null;
                        if (!src.startsWith("http:")) {
                            src = "http:" + src;
                        }
                        try {
                            URL url = new URL(src);
                            String path = url.getPath();
                            String[] pathParts = path.split("/");
                            originUrl = "http://www.youtube.com/watch?v=" + pathParts[pathParts.length - 1];
                            linksBuffer.add(new YoutubeVideo(originUrl, src));
                        } catch (MalformedURLException e) {
                        }

                    }

                    if (src != null && src.length() > 0 && src.contains("player.vimeo.com")) {
                        String originUrl = null;
                        if (!src.startsWith("http:")) {
                            src = "http:" + src;
                        }
                        try {
                            URL url = new URL(src);
                            String path = url.getPath();
                            String[] pathParts = path.split("/");
                            originUrl = "http://vimeo.com/" + pathParts[pathParts.length - 1];
                            linksBuffer.add(new VimeoVideo(originUrl, src));
                        } catch (MalformedURLException e) {
                        }

                    }
                }

                if (inHighlight && "IMG".equalsIgnoreCase(localName)) {
                    String src = atts.getValue("src");
                    try {
                        URI image = new URI(src);
                        if (src != null && src.length() > 0) {
                            linksBuffer.add(new Image(src, atts.getValue("width"), atts.getValue("height"), atts
                                .getValue("alt")));
                        }
                    } catch (URISyntaxException e) {
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


    @SuppressWarnings("synthetic-access")
    private static final TagAction TA_IGNORABLE_ELEMENT = new TagAction() {
        @Override
        void beforeStart(final MediaExtractorContentHandler instance, final String localName) {
            instance.inIgnorableElement++;
        }

        @Override
        void afterEnd(final MediaExtractorContentHandler instance, final String localName) {
            instance.inIgnorableElement--;
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
        void beforeStart(final MediaExtractorContentHandler instance, final String localName) {
        }

        void afterStart(final MediaExtractorContentHandler instance, final String localName) {
        }

        void beforeEnd(final MediaExtractorContentHandler instance, final String localName) {
        }

        void afterEnd(final MediaExtractorContentHandler instance, final String localName) {
        }
    }

}
