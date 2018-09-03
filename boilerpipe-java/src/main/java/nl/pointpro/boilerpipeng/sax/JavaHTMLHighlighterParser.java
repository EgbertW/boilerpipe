package nl.pointpro.boilerpipeng.sax;


import nl.pointpro.boilerpipeng.BoilerpipeProcessingException;
import nl.pointpro.boilerpipeng.document.TextBlock;
import nl.pointpro.boilerpipeng.document.TextDocument;

import org.apache.xerces.parsers.AbstractSAXParser;
import org.cyberneko.html.HTMLConfiguration;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.BitSet;

public class JavaHTMLHighlighterParser extends AbstractSAXParser implements HTMLHighlighterParser {


    public JavaHTMLHighlighterParser() {
        super(new HTMLConfiguration());
    }

    public void process(final TextDocument doc, final InputSource inputSource, HTMLHighlighterContentHandler contentHandler) throws BoilerpipeProcessingException
    {
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

            parse(inputSource);
        } catch (SAXException e) {
            throw new BoilerpipeProcessingException(e);
        } catch (IOException e) {
            throw new BoilerpipeProcessingException(e);
        }
    }
}
