package com.kohlschutter.boilerpipe.sax;


import com.kohlschutter.boilerpipe.BoilerpipeProcessingException;
import com.kohlschutter.boilerpipe.document.TextBlock;
import com.kohlschutter.boilerpipe.document.TextDocument;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.util.BitSet;

public class AndroidImageExtractorParser implements ImageExtractorParser {

    @Override
    public void process(TextDocument doc, InputSource inputSource, ImageExtractorContentHandler contentHandler) throws BoilerpipeProcessingException {
        for (TextBlock block : doc.getTextBlocks()) {
            if (block.isContent()) {
                final BitSet bs = block.getContainedTextElements();
                if (bs != null) {
                    contentHandler.getContentBitSet().or(bs);
                }
            }
        }

        try {
            XMLReader parser = XMLReaderFactory.createXMLReader("org.ccil.cowan.tagsoup.Parser");

            parser.setContentHandler(contentHandler);

            parser.parse(inputSource);
        } catch(Exception e) {
            throw new BoilerpipeProcessingException(e);
        }
    }

}
