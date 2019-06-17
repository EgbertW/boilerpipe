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
package nl.pointpro.boilerpipeng.filters.english;

import java.util.Iterator;

import nl.pointpro.boilerpipeng.BoilerpipeFilter;
import nl.pointpro.boilerpipeng.BoilerpipeProcessingException;
import nl.pointpro.boilerpipeng.document.TextBlock;
import nl.pointpro.boilerpipeng.document.TextDocument;
import nl.pointpro.boilerpipeng.labels.DefaultLabels;

/**
 * Marks all blocks as "non-content" that occur after blocks that have been marked
 * {@link DefaultLabels#INDICATES_END_OF_TEXT}. These marks are ignored unless a minimum number of
 * words in content blocks occur before this mark (default: 60). This can be used in conjunction
 * with an upstream {@link TerminatingBlocksFinder}.
 * 
 * @see TerminatingBlocksFinder
 */
public final class IgnoreBlocksAfterContentFilter extends HeuristicFilterBase implements
    BoilerpipeFilter
{
  public static final IgnoreBlocksAfterContentFilter DEFAULT_INSTANCE =
      new IgnoreBlocksAfterContentFilter(60);
  public static final IgnoreBlocksAfterContentFilter INSTANCE_200 =
      new IgnoreBlocksAfterContentFilter(200);
  private final int minNumWords;

  /**
   * @return the singleton instance for DeleteBlocksAfterContentFilter.
   */
  public static IgnoreBlocksAfterContentFilter getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  public IgnoreBlocksAfterContentFilter(final int minNumWords) {
    this.minNumWords = minNumWords;
  }

  public boolean process(TextDocument doc) throws BoilerpipeProcessingException
  {
    boolean changes = false;

    int numWords = 0;
    boolean foundEndOfText = false;
    for (Iterator<TextBlock> it = doc.getTextBlocks().iterator(); it.hasNext();) {
      TextBlock block = it.next();

      final boolean endOfText = block.hasLabel(DefaultLabels.INDICATES_END_OF_TEXT);
      if (block.isContent()) {
        numWords += getNumFullTextWords(block);
      }
      if (endOfText && numWords >= minNumWords) {
        foundEndOfText = true;
      }
      if (foundEndOfText) {
        changes = true;
        block.setIsContent(false);
      }
    }

    return changes;
  }
}
