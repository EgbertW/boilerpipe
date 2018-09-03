package nl.pointpro.boilerpipeng.sax;


import nl.pointpro.boilerpipeng.BoilerpipeProcessingException;
import nl.pointpro.boilerpipeng.document.TextDocument;

import org.xml.sax.InputSource;


public interface MediaExtractorParser {

    void process(final TextDocument doc, final InputSource is, MediaExtractorContentHandler contentHandler) throws BoilerpipeProcessingException;

}
