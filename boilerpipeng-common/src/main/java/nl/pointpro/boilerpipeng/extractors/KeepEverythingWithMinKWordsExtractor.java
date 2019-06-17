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

import nl.pointpro.boilerpipeng.BoilerpipeProcessingException;
import nl.pointpro.boilerpipeng.document.TextDocument;
import nl.pointpro.boilerpipeng.filters.heuristics.SimpleBlockFusionProcessor;
import nl.pointpro.boilerpipeng.filters.simple.MarkEverythingContentFilter;
import nl.pointpro.boilerpipeng.filters.simple.MinWordsFilter;
import nl.pointpro.boilerpipeng.sax.BoilerpipeHTMLParser;

/**
 * A full-text extractor which extracts the largest text component of a page. For news articles, it
 * may perform better than the {@link DefaultExtractor}, but usually worse than
 * {@link ArticleExtractor}.
 */
public final class KeepEverythingWithMinKWordsExtractor extends ExtractorBase {

  private final MinWordsFilter filter;

  public KeepEverythingWithMinKWordsExtractor(BoilerpipeHTMLParser parser, final int kMin) {
    super(parser);
    this.filter = new MinWordsFilter(kMin);
  }

  public boolean process(TextDocument doc) throws BoilerpipeProcessingException
  {
    return SimpleBlockFusionProcessor.INSTANCE.process(doc)
        | MarkEverythingContentFilter.INSTANCE.process(doc) | filter.process(doc);
  }

}
