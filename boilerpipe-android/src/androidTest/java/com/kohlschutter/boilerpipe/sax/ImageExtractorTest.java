package com.kohlschutter.boilerpipe.sax;

import android.support.test.runner.AndroidJUnit4;

import com.kohlschutter.boilerpipe.BoilerpipeExtractor;
import com.kohlschutter.boilerpipe.document.Image;
import com.kohlschutter.boilerpipe.extractors.CommonExtractors;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;
import java.util.Collections;
import java.util.List;


@RunWith(AndroidJUnit4.class)
public class ImageExtractorTest {

    @Test
    public void testImageExtractor() throws Exception {
        URL url = new URL("http://www.spiegel.de/wissenschaft/natur/0,1518,789176,00.html");

        CommonExtractors.instantiate(new AndroidBoilerpipeHTMLParser());

        // choose from a set of useful BoilerpipeExtractors...
        final BoilerpipeExtractor extractor = CommonExtractors.getArticleExtractor();
//     final BoilerpipeExtractor extractor = CommonExtractors.getDefaultExtractor();
//     final BoilerpipeExtractor extractor = CommonExtractors.getCanolaExtractor();
//     final BoilerpipeExtractor extractor = CommonExtractors.getLargestContentExtractor();

        final ImageExtractor ie = new ImageExtractor(new AndroidImageExtractorParser());

        List<Image> imgUrls = ie.process(url, extractor);

        // automatically sorts them by decreasing area, i.e. most probable true positives come first
        Collections.sort(imgUrls);

        System.out.println("Extracted images:");
        for (Image img : imgUrls) {
            System.out.println("* " + img);
        }
    }

}
