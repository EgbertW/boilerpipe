package com.kohlschutter.boilerpipe.sax;

import android.support.test.runner.AndroidJUnit4;

import com.kohlschutter.boilerpipe.BoilerpipeExtractor;
import com.kohlschutter.boilerpipe.extractors.CommonExtractors;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.PrintWriter;
import java.net.URL;


@RunWith(AndroidJUnit4.class)
public class HTMLHighlighterTest {

    @Test
    public void testHTMLHighlighter() throws Exception {
        URL url =
            new URL(
                "https://blog.openshift.com/day-18-boilerpipe-article-extraction-for-java-developers/");

        CommonExtractors.instantiate(new AndroidBoilerpipeHTMLParser());

        // choose from a set of useful BoilerpipeExtractors...
        final BoilerpipeExtractor extractor = CommonExtractors.getArticleExtractor();
//     final BoilerpipeExtractor extractor = CommonExtractors.getDefaultExtractor();
//     final BoilerpipeExtractor extractor = CommonExtractors.getCanolaExtractor();
//     final BoilerpipeExtractor extractor = CommonExtractors.getLargestContentExtractor();

        // choose the operation mode (i.e., highlighting or extraction)
        final HTMLHighlighter hh = HTMLHighlighter.newHighlightingInstance(new AndroidHTMLHighlighterParser());
        // final HTMLHighlighter hh = HTMLHighlighter.newExtractingInstance();

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
