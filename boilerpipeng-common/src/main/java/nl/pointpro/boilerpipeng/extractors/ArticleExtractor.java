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
import nl.pointpro.boilerpipeng.filters.english.IgnoreBlocksAfterContentFilter;
import nl.pointpro.boilerpipeng.filters.english.NumWordsRulesClassifier;
import nl.pointpro.boilerpipeng.filters.english.TerminatingBlocksFinder;
import nl.pointpro.boilerpipeng.filters.heuristics.BlockProximityFusion;
import nl.pointpro.boilerpipeng.filters.heuristics.DocumentTitleMatchClassifier;
import nl.pointpro.boilerpipeng.filters.heuristics.ExpandTitleToContentFilter;
import nl.pointpro.boilerpipeng.filters.heuristics.KeepLargestBlockFilter;
import nl.pointpro.boilerpipeng.filters.heuristics.LargeBlockSameTagLevelToContentFilter;
import nl.pointpro.boilerpipeng.filters.heuristics.ListAtEndFilter;
import nl.pointpro.boilerpipeng.filters.heuristics.TrailingHeadlineToBoilerplateFilter;
import nl.pointpro.boilerpipeng.filters.simple.BoilerplateBlockFilter;
import nl.pointpro.boilerpipeng.sax.BoilerpipeHTMLParser;

/**
 * A full-text extractor which is tuned towards news articles. In this scenario it achieves higher
 * accuracy than {@link DefaultExtractor}.
 */
public final class ArticleExtractor extends ExtractorBase {

  public ArticleExtractor(BoilerpipeHTMLParser htmlParser) {
    super(htmlParser);
  }


  public boolean process(TextDocument doc) throws BoilerpipeProcessingException
  {
    return

    TerminatingBlocksFinder.INSTANCE.process(doc)
        | new DocumentTitleMatchClassifier(doc.getTitle()).process(doc)
        | NumWordsRulesClassifier.INSTANCE.process(doc)
        | IgnoreBlocksAfterContentFilter.DEFAULT_INSTANCE.process(doc)
        | TrailingHeadlineToBoilerplateFilter.INSTANCE.process(doc)
        | BlockProximityFusion.MAX_DISTANCE_1.process(doc)
        | BoilerplateBlockFilter.INSTANCE_KEEP_TITLE.process(doc)
        | BlockProximityFusion.MAX_DISTANCE_1_CONTENT_ONLY_SAME_TAGLEVEL.process(doc)
        | KeepLargestBlockFilter.INSTANCE_EXPAND_TO_SAME_TAGLEVEL_MIN_WORDS.process(doc)
        | ExpandTitleToContentFilter.INSTANCE.process(doc)
        | LargeBlockSameTagLevelToContentFilter.INSTANCE.process(doc)
        | ListAtEndFilter.INSTANCE.process(doc);
  }
}
