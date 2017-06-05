package com.kohlschutter.boilerpipe.sax;


import com.kohlschutter.boilerpipe.BoilerpipeProcessingException;
import com.kohlschutter.boilerpipe.document.TextBlock;
import com.kohlschutter.boilerpipe.document.TextDocument;

import org.apache.xerces.parsers.AbstractSAXParser;
import org.cyberneko.html.HTMLConfiguration;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.BitSet;

public class JavaImageExtractorParser extends AbstractSAXParser implements ImageExtractorParser {


    public JavaImageExtractorParser() {
        super(new HTMLConfiguration());
    }

    public void process(final TextDocument doc, final InputSource is, final ImageExtractorContentHandler contentHandler) throws BoilerpipeProcessingException {
        for (TextBlock block : doc.getTextBlocks()) {
            if (block.isContent()) {
                final BitSet bs = block.getContainedTextElements();
                if (bs != null) {
                    contentHandler.getContentBitSet().or(bs);
                }
            }
        }

        try {
            setContentHandler(contentHandler);

            parse(is);
        } catch (SAXException e) {
            throw new BoilerpipeProcessingException(e);
        } catch (IOException e) {
            throw new BoilerpipeProcessingException(e);
        }
    }

}
