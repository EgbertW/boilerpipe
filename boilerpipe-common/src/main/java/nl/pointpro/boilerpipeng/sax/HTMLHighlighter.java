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
package nl.pointpro.boilerpipeng.sax;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.pointpro.boilerpipeng.BoilerpipeExtractor;
import nl.pointpro.boilerpipeng.BoilerpipeProcessingException;
import nl.pointpro.boilerpipeng.document.TextDocument;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Highlights text blocks in an HTML document that have been marked as "content" in the
 * corresponding {@link TextDocument}.
 */
public final class HTMLHighlighter {

  /**
   * Creates a new {@link HTMLHighlighter}, which is set-up to return the full HTML text, with the
   * extracted text portion <b>highlighted</b>.
   */
  public static HTMLHighlighter newHighlightingInstance(HTMLHighlighterParser parser) {
    return new HTMLHighlighter(parser, false);
  }

  /**
   * Creates a new {@link HTMLHighlighter}, which is set-up to return only the extracted HTML text,
   * including enclosed markup.
   */
  public static HTMLHighlighter newExtractingInstance(HTMLHighlighterParser parser) {
    return new HTMLHighlighter(parser, true);
  }


  private HTMLHighlighterParser parser = null;

  private HTMLHighlighterContentHandler contentHandler = new HTMLHighlighterContentHandler();


  private HTMLHighlighter(HTMLHighlighterParser parser, final boolean extractHTML) {
    this.parser = parser;

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
    parser.process(doc, is, contentHandler);

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
   * @param url The URL to process
   * @param extractor The extractor to use
   * @return The highlighted HTML.
   * @throws BoilerpipeProcessingException
   */
  public String process(final URL url, final BoilerpipeExtractor extractor) throws IOException,
      BoilerpipeProcessingException, SAXException {
    final HTMLDocument htmlDoc = HTMLFetcher.fetch(url);

    BoilerpipeSAXInput saxInput = new BoilerpipeSAXInput(htmlDoc.toInputSource());
    final TextDocument doc = saxInput.getTextDocument(extractor.getHtmlParser());
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
   * @return the string that will be inserted before any highlighted HTML block.
   * 
   * By default, this corresponds to <code>&lt;span class=&quot;x-boilerpipe-mark1&quot;&gt;</code>
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
   * @return the string that will be inserted after any highlighted HTML block.
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

}
