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
package nl.pointpro.boilerpipeng.demo;

import nl.pointpro.boilerpipeng.BoilerpipeExtractor;
import nl.pointpro.boilerpipeng.document.Image;
import nl.pointpro.boilerpipeng.extractors.CommonExtractors;
import nl.pointpro.boilerpipeng.sax.ImageExtractor;
import nl.pointpro.boilerpipeng.sax.JavaBoilerpipeHTMLParser;
import nl.pointpro.boilerpipeng.sax.JavaImageExtractorParser;

import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * Demonstrates how to use Boilerpipe to get the images within the main content.
 */
public final class ImageExtractorDemo {
  public static void main(String[] args) throws Exception {
    URL url = new URL("http://www.spiegel.de/wissenschaft/natur/0,1518,789176,00.html");

    CommonExtractors.instantiate(new JavaBoilerpipeHTMLParser());

    // choose from a set of useful BoilerpipeExtractors...
    final BoilerpipeExtractor extractor = CommonExtractors.getArticleExtractor();
//     final BoilerpipeExtractor extractor = CommonExtractors.getDefaultExtractor();
//     final BoilerpipeExtractor extractor = CommonExtractors.getCanolaExtractor();
//     final BoilerpipeExtractor extractor = CommonExtractors.getLargestContentExtractor();

    final ImageExtractor ie = new ImageExtractor(new JavaImageExtractorParser());

    List<Image> imgUrls = ie.process(url, extractor);

    // automatically sorts them by decreasing area, i.e. most probable true positives come first
    Collections.sort(imgUrls);

    for (Image img : imgUrls) {
      System.out.println("* " + img);
    }

  }
}
