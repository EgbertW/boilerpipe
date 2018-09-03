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
package nl.pointpro.boilerpipeng.extractors;

import nl.pointpro.boilerpipeng.BoilerpipeExtractor;
import nl.pointpro.boilerpipeng.BoilerpipeProcessingException;
import nl.pointpro.boilerpipeng.document.TextDocument;
import nl.pointpro.boilerpipeng.sax.BoilerpipeHTMLParser;
import nl.pointpro.boilerpipeng.sax.BoilerpipeSAXInput;
import nl.pointpro.boilerpipeng.sax.HTMLFetcher;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

/**
 * The base class of Extractors. Also provides some helper methods to quickly retrieve the text that
 * remained after processing.
 */
public abstract class ExtractorBase implements BoilerpipeExtractor
{

  private BoilerpipeHTMLParser htmlParser;


  public ExtractorBase(BoilerpipeHTMLParser htmlParser) {
    this.htmlParser = htmlParser;
  }


  /**
   * Extracts text from the HTML code given as a String.
   * 
   * @param html The HTML code as a String.
   * @return The extracted text.
   * @throws BoilerpipeProcessingException Wraps caught SAXExceptions
   */
  public String getText(final String html) throws BoilerpipeProcessingException {
    try {
      return getText(new BoilerpipeSAXInput(new InputSource(new StringReader(html)))
          .getTextDocument(htmlParser));
    } catch (SAXException e) {
      throw new BoilerpipeProcessingException(e);
    }
  }

  /**
   * Extracts text from the HTML code available from the given {@link InputSource}.
   * 
   * @param is The InputSource containing the HTML
   * @return The extracted text.
   * @throws BoilerpipeProcessingException Wraps caught SAXExceptions
   */
  public String getText(final InputSource is) throws BoilerpipeProcessingException {
    try {
      return getText(new BoilerpipeSAXInput(is).getTextDocument(htmlParser));
    } catch (SAXException e) {
      throw new BoilerpipeProcessingException(e);
    }
  }

  /**
   * Extracts text from the HTML code available from the given {@link URL}. NOTE: This method is
   * mainly to be used for show case purposes. If you are going to crawl the Web, consider using
   * {@link #getText(InputSource)} instead.
   * 
   * @param url The URL pointing to the HTML code.
   * @return The extracted text.
   * @throws BoilerpipeProcessingException When an IOException is caught and rethrown
   */
  public String getText(final URL url) throws BoilerpipeProcessingException {
    try {
      return getText(HTMLFetcher.fetch(url).toInputSource());
    } catch (IOException e) {
      throw new BoilerpipeProcessingException(e);
    }
  }

  /**
   * Extracts text from the HTML code available from the given {@link Reader}.
   * 
   * @param r The Reader containing the HTML
   * @return The extracted text.
   * @throws BoilerpipeProcessingException When an IOException occurs
   */
  public String getText(final Reader r) throws BoilerpipeProcessingException {
    return getText(new InputSource(r));
  }

  /**
   * Extracts text from the given {@link TextDocument} object.
   * 
   * @param doc The {@link TextDocument}.
   * @return The extracted text.
   * @throws BoilerpipeProcessingException Wraps caught errors
   */
  public String getText(TextDocument doc) throws BoilerpipeProcessingException {
    process(doc);
    return doc.getContent();
  }

  public BoilerpipeHTMLParser getHtmlParser() {
    return htmlParser;
  }

}
