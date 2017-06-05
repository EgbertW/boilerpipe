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
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xerces.parsers.AbstractSAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.kohlschutter.boilerpipe.BoilerpipeExtractor;
import com.kohlschutter.boilerpipe.BoilerpipeProcessingException;
import com.kohlschutter.boilerpipe.document.Image;
import com.kohlschutter.boilerpipe.document.TextBlock;
import com.kohlschutter.boilerpipe.document.TextDocument;
import org.cyberneko.html.HTMLConfiguration;

/**
 * Extracts the images that are enclosed by extracted content.
 */
public final class ImageExtractor {
  public static final ImageExtractor INSTANCE = new ImageExtractor();

  /**
   * Returns the singleton instance of {@link ImageExtractor}.
   * 
   * @return
   */
  public static ImageExtractor getInstance() {
    return INSTANCE;
  }


  private ImageExtractorContentHandler contentHandler;

  private ImageExtractor() {
      contentHandler = new ImageExtractorContentHandler();
  }

  /**
   * Processes the given {@link TextDocument} and the original HTML text (as a String).
   * 
   * @param doc The processed {@link TextDocument}.
   * @param origHTML The original HTML document.
   * @return A List of enclosed {@link Image}s
   * @throws BoilerpipeProcessingException
   */
  public List<Image> process(final TextDocument doc, final String origHTML)
      throws BoilerpipeProcessingException {
    return process(doc, new InputSource(new StringReader(origHTML)));
  }

  /**
   * Processes the given {@link TextDocument} and the original HTML text (as an {@link InputSource}
   * ).
   * 
   * @param doc The processed {@link TextDocument}.
   * @param origHTML The original HTML document.
   * @return A List of enclosed {@link Image}s
   * @throws BoilerpipeProcessingException
   */
  public List<Image> process(final TextDocument doc, final InputSource is)
      throws BoilerpipeProcessingException {
    final Implementation implementation = new Implementation();
    implementation.process(doc, is, contentHandler);

    return contentHandler.getLinksHighlight();
  }

  /**
   * Fetches the given {@link URL} using {@link HTMLFetcher} and processes the retrieved HTML using
   * the specified {@link BoilerpipeExtractor}.
   * 
   * @param doc The processed {@link TextDocument}.
   * @param is The original HTML document.
   * @return A List of enclosed {@link Image}s
   * @throws BoilerpipeProcessingException
   */
  public List<Image> process(final URL url, final BoilerpipeExtractor extractor)
      throws IOException, BoilerpipeProcessingException, SAXException {
    final HTMLDocument htmlDoc = HTMLFetcher.fetch(url);

    final TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
    extractor.process(doc);

    final InputSource is = htmlDoc.toInputSource();

    return process(doc, is);
  }

  private final class Implementation extends AbstractSAXParser {

      Implementation() {
          super(new HTMLConfiguration());
      }

      void process(final TextDocument doc, final InputSource is, ImageExtractorContentHandler contentHandler) throws BoilerpipeProcessingException {
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

              parse(is);
          } catch (SAXException e) {
              throw new BoilerpipeProcessingException(e);
          } catch (IOException e) {
              throw new BoilerpipeProcessingException(e);
          }
      }

  }

}
