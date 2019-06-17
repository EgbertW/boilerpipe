package nl.pointpro.boilerpipeng.sax;


import nl.pointpro.boilerpipeng.BoilerpipeProcessingException;
import nl.pointpro.boilerpipeng.document.TextDocument;

import org.xml.sax.InputSource;

public interface HTMLHighlighterParser {

    void process(final TextDocument doc, final InputSource inputSource, HTMLHighlighterContentHandler contentHandler) throws BoilerpipeProcessingException;
}
