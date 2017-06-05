package com.kohlschutter.boilerpipe.sax;

import android.support.test.runner.AndroidJUnit4;

import com.kohlschutter.boilerpipe.extractors.ArticleExtractor;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;


@RunWith(AndroidJUnit4.class)
public class ArticleExtractorTest {

    @Test
    public void testArticleExtractor() throws Exception {
        final URL url =
            new URL(
                "https://blog.openshift.com/day-18-boilerpipe-article-extraction-for-java-developers/"
                // "http://www.dn.se/nyheter/vetenskap/annu-godare-choklad-med-hjalp-av-dna-teknik"
            );

        System.out.println(new ArticleExtractor(new AndroidBoilerpipeHTMLParser()).getText(url));
    }

}
