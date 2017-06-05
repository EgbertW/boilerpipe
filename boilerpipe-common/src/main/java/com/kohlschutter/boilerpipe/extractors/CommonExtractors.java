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
package com.kohlschutter.boilerpipe.extractors;

import com.kohlschutter.boilerpipe.BoilerpipeExtractor;
import com.kohlschutter.boilerpipe.sax.BoilerpipeHTMLParser;

/**
 * Provides quick access to common {@link BoilerpipeExtractor}s.
 */
public final class CommonExtractors {
  private CommonExtractors() {
  }


  private static ArticleExtractor articleExtractor = null;

  private static DefaultExtractor defaultExtractor = null;

  private static LargestContentExtractor largestContentExtractor = null;

  private static CanolaExtractor canolaExtractor = null;

  private static KeepEverythingExtractor keepEverythingExtractor = null;


  public static final void instantiate(BoilerpipeHTMLParser htmlParser) {
    articleExtractor = new ArticleExtractor(htmlParser);
    defaultExtractor = new DefaultExtractor(htmlParser);
    largestContentExtractor = new LargestContentExtractor(htmlParser);
    canolaExtractor = new CanolaExtractor(htmlParser);
    keepEverythingExtractor = new KeepEverythingExtractor(htmlParser);
  }

  /**
   * Works very well for most types of Article-like HTML.
   */
  public static ArticleExtractor getArticleExtractor() {
    checkIfInstantiateHasBeenCalled();

    return articleExtractor;
  }

  /**
   * Usually worse than {@link ArticleExtractor}, but simpler/no heuristics.
   */
  public static DefaultExtractor getDefaultExtractor() {
    checkIfInstantiateHasBeenCalled();

    return defaultExtractor;
  }

  /**
   * Like {@link DefaultExtractor}, but keeps the largest text block only.
   */
  public static LargestContentExtractor getLargestContentExtractor() {
    checkIfInstantiateHasBeenCalled();

    return largestContentExtractor;
  }

  /**
   * Trained on krdwrd Canola (different definition of "boilerplate"). You may give it a try.
   */
  public static CanolaExtractor getCanolaExtractor() {
    checkIfInstantiateHasBeenCalled();

    return canolaExtractor;
  }

  /**
   * Dummy Extractor; should return the input text. Use this to double-check that your problem is
   * within a particular {@link BoilerpipeExtractor}, or somewhere else.
   */
  public static KeepEverythingExtractor getKeepEverythingExtractor() {
    checkIfInstantiateHasBeenCalled();

    return keepEverythingExtractor;
  }



  private static void checkIfInstantiateHasBeenCalled() {
    if(articleExtractor == null) {
      throw new RuntimeException("You have to call CommonExtractors.instantiate(BoilerpipeHTMLParser) with JavaBoilerpipeHTMLParser or AndroidBoilerpipeHTMLParser instance before");
    }
  }

}
