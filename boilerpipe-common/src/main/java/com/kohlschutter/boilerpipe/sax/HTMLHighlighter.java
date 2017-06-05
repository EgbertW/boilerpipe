/**
 * boilerpipe
 *
 * Copyright (c) 2009, 2014 Christian Kohlschütter
 *
 * The author licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kohlschutter.boilerpipe.sax;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xerces.parsers.AbstractSAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.kohlschutter.boilerpipe.BoilerpipeExtractor;
import com.kohlschutter.boilerpipe.BoilerpipeProcessingException;
import com.kohlschutter.boilerpipe.document.TextBlock;
import com.kohlschutter.boilerpipe.document.TextDocument;
import org.cyberneko.html.HTMLConfiguration;

/**
 * Highlights text blocks in an HTML document that have been marked as "content" in the
 * corresponding {@link TextDocument}.
 */
public final class HTMLHighlighter {

  private Map<String, Set<String>> tagWhitelist = null;

  /**
   * Creates a new {@link HTMLHighlighter}, which is set-up to return the full HTML text, with the
   * extracted text portion <b>highlighted</b>.
   */
  public static HTMLHighlighter newHighlightingInstance() {
    return new HTMLHighlighter(false);
  }

  /**
   * Creates a new {@link HTMLHighlighter}, which is set-up to return only the extracted HTML text,
   * including enclosed markup.
   */
  public static HTMLHighlighter newExtractingInstance() {
    return new HTMLHighlighter(true);
  }


  private HTMLHighlighterContentHandler contentHandler = new HTMLHighlighterContentHandler();


  private HTMLHighlighter(final boolean extractHTML) {
    if (extractHTML) {
      setOutputHighlightOnly(true);
      setExtraStyleSheet("\n<style type=\"text/css\">\n" + "A:before { content:' '; } \n" //
          + "A:after { content:' '; } \n" //
          + "SPAN:before { content:' '; } \n" //
          + "SPAN:after { content:' '; } \n" //
          + "</style>\n");
      setPreHighlight("");
      setPostHighlight("");
    }
  }

  /**
   * Processes the given {@link TextDocument} and the original HTML text (as a String).
   * 
   * @param doc The processed {@link TextDocument}.
   * @param origHTML The original HTML document.
   * @return The highlighted HTML.
   * @throws BoilerpipeProcessingException
   */
  public String process(final TextDocument doc, final String origHTML)
      throws BoilerpipeProcessingException {
    return process(doc, new InputSource(new StringReader(origHTML)));
  }

  /**
   * Processes the given {@link TextDocument} and the original HTML text (as an {@link InputSource}
   * ).
   * 
   * @param doc The processed {@link TextDocument}.
   * @param is The original HTML document.
   * @return The highlighted HTML.
   * @throws BoilerpipeProcessingException
   */
  public String process(final TextDocument doc, final InputSource is)
      throws BoilerpipeProcessingException {
    final Implementation implementation = new Implementation();
    implementation.process(doc, is, contentHandler);

    String html = contentHandler.html.toString();
    if (contentHandler.isOutputHighlightOnly()) {
      Matcher m;

      boolean repeat = true;
      while (repeat) {
        repeat = false;
        m = PAT_TAG_NO_TEXT.matcher(html);
        if (m.find()) {
          repeat = true;
          html = m.replaceAll("");
        }

        m = PAT_SUPER_TAG.matcher(html);
        if (m.find()) {
          repeat = true;
          html = m.replaceAll(m.group(1));
        }
      }
    }

    return html;
  }

  private static final Pattern PAT_TAG_NO_TEXT = Pattern.compile("<[^/][^>]*></[^>]*>");
  private static final Pattern PAT_SUPER_TAG = Pattern.compile("^<[^>]*>(<.*?>)</[^>]*>$");

  /**
   * Fetches the given {@link URL} using {@link HTMLFetcher} and processes the retrieved HTML using
   * the specified {@link BoilerpipeExtractor}.
   * 
   * @param doc The processed {@link TextDocument}.
   * @param is The original HTML document.
   * @return The highlighted HTML.
   * @throws BoilerpipeProcessingException
   */
  public String process(final URL url, final BoilerpipeExtractor extractor) throws IOException,
      BoilerpipeProcessingException, SAXException {
    final HTMLDocument htmlDoc = HTMLFetcher.fetch(url);

    final TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
    extractor.process(doc);

    final InputSource is = htmlDoc.toInputSource();

    return process(doc, is);
  }


  /**
   * If true, only HTML enclosed within highlighted content will be returned
   */
  public boolean isOutputHighlightOnly() {
    return contentHandler.isOutputHighlightOnly();
  }

  /**
   * Sets whether only HTML enclosed within highlighted content will be returned, or the whole HTML
   * document.
   */
  public void setOutputHighlightOnly(boolean outputHighlightOnly) {
    this.contentHandler.setOutputHighlightOnly(outputHighlightOnly);
  }

  /**
   * Returns the extra stylesheet definition that will be inserted in the HEAD element.
   * 
   * By default, this corresponds to a simple definition that marks text in class
   * "x-boilerpipe-mark1" as inline text with yellow background.
   */
  public String getExtraStyleSheet() {
    return contentHandler.getExtraStyleSheet();
  }

  /**
   * Sets the extra stylesheet definition that will be inserted in the HEAD element.
   * 
   * To disable, set it to the empty string: ""
   * 
   * @param extraStyleSheet Plain HTML
   */
  public void setExtraStyleSheet(String extraStyleSheet) {
    this.contentHandler.setExtraStyleSheet(extraStyleSheet);
  }

  /**
   * Returns the string that will be inserted before any highlighted HTML block.
   * 
   * By default, this corresponds to <code>&lt;span class=&qupt;x-boilerpipe-mark1&quot;&gt;</code>
   */
  public String getPreHighlight() {
    return contentHandler.getPreHighlight();
  }

  /**
   * Sets the string that will be inserted prior to any highlighted HTML block.
   * 
   * To disable, set it to the empty string: ""
   */
  public void setPreHighlight(String preHighlight) {
    this.contentHandler.setPreHighlight(preHighlight);
  }

  /**
   * Returns the string that will be inserted after any highlighted HTML block.
   * 
   * By default, this corresponds to <code>&lt;/span&gt;</code>
   */
  public String getPostHighlight() {
    return contentHandler.getPostHighlight();
  }

  /**
   * Sets the string that will be inserted after any highlighted HTML block.
   * 
   * To disable, set it to the empty string: ""
   */
  public void setPostHighlight(String postHighlight) {
    this.contentHandler.setPostHighlight(postHighlight);
  }


  private final class Implementation extends AbstractSAXParser {

    Implementation() {
      super(new HTMLConfiguration());
    }

    void process(final TextDocument doc, final InputSource inputSource, HTMLHighlighterContentHandler contentHandler) throws BoilerpipeProcessingException {
      for (TextBlock block : doc.getTextBlocks()) {
        if (block.isContent()) {
          final BitSet bs = block.getContainedTextElements();
          if (bs != null) {
            contentHandler.getContentBitSet().or(bs);
          }
        }
      }

      try {
        setContentHandler(contentHandler);

        parse(inputSource);
      } catch (SAXException e) {
        throw new BoilerpipeProcessingException(e);
      } catch (IOException e) {
        throw new BoilerpipeProcessingException(e);
      }
    }

  }

}
