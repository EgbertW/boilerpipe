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
import nl.pointpro.boilerpipeng.filters.english.NumWordsRulesClassifier;
import nl.pointpro.boilerpipeng.sax.BoilerpipeHTMLParser;

/**
 * A quite generic full-text extractor solely based upon the number of words per block (the current,
 * the previous and the next block).
 */
public class NumWordsRulesExtractor extends ExtractorBase {

  public NumWordsRulesExtractor(BoilerpipeHTMLParser htmlParser) {
    super(htmlParser);
  }


  public boolean process(TextDocument doc) throws BoilerpipeProcessingException
  {

    return NumWordsRulesClassifier.INSTANCE.process(doc);
  }

}
