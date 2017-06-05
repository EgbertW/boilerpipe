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
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.kohlschutter.boilerpipe.BoilerpipeExtractor;
import com.kohlschutter.boilerpipe.BoilerpipeProcessingException;
import com.kohlschutter.boilerpipe.document.Image;
import com.kohlschutter.boilerpipe.document.TextDocument;

/**
 * Extracts the images that are enclosed by extracted content.
 */
public final class ImageExtractor {


  private ImageExtractorParser parser = null;

  private ImageExtractorContentHandler contentHandler;


  public ImageExtractor(ImageExtractorParser parser) {
      this.parser = parser;
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
    parser.process(doc, is, contentHandler);

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

    BoilerpipeSAXInput saxInput = new BoilerpipeSAXInput(htmlDoc.toInputSource());
    final TextDocument doc = saxInput.getTextDocument(extractor.getHtmlParser());
    extractor.process(doc);

    final InputSource is = htmlDoc.toInputSource();

    return process(doc, is);
  }

}
