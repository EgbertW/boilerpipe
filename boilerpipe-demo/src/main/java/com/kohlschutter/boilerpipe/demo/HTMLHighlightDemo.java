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
package com.kohlschutter.boilerpipe.demo;

import com.kohlschutter.boilerpipe.BoilerpipeExtractor;
import com.kohlschutter.boilerpipe.extractors.CommonExtractors;
import com.kohlschutter.boilerpipe.sax.HTMLHighlighter;
import com.kohlschutter.boilerpipe.sax.JavaBoilerpipeHTMLParser;
import com.kohlschutter.boilerpipe.sax.JavaHTMLHighlighterParser;

import java.io.PrintWriter;
import java.net.URL;

/**
 * Demonstrates how to use Boilerpipe to get the main content, highlighted as HTML.
 * 
 * @see Oneliner if you only need the plain text.
 */
public class HTMLHighlightDemo {
  public static void main(String[] args) throws Exception {
    URL url =
        new URL(
            "https://blog.openshift.com/day-18-boilerpipe-article-extraction-for-java-developers/");

    CommonExtractors.instantiate(new JavaBoilerpipeHTMLParser());

    // choose from a set of useful BoilerpipeExtractors...
    final BoilerpipeExtractor extractor = CommonExtractors.getArticleExtractor();
//     final BoilerpipeExtractor extractor = CommonExtractors.getDefaultExtractor();
//     final BoilerpipeExtractor extractor = CommonExtractors.getCanolaExtractor();
//     final BoilerpipeExtractor extractor = CommonExtractors.getLargestContentExtractor();

    // choose the operation mode (i.e., highlighting or extraction)
    final HTMLHighlighter hh = HTMLHighlighter.newHighlightingInstance(new JavaHTMLHighlighterParser());
    // final HTMLHighlighter hh = HTMLHighlighter.newExtractingInstance(new JavaHTMLHighlighterParser());

    String extractedText = hh.process(url, extractor);
    System.out.println("Extracted text: " + extractedText);

    PrintWriter out = new PrintWriter("/tmp/highlighted.html", "UTF-8");
    out.println("<base href=\"" + url + "\" >");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text-html; charset=utf-8\" />");
    out.println(extractedText);
    out.close();

    System.out.println("Now open file:///tmp/highlighted.html in your web browser");
  }
}
