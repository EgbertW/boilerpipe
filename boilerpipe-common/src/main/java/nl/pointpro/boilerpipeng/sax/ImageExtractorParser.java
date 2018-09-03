package nl.pointpro.boilerpipeng.sax;


import nl.pointpro.boilerpipeng.BoilerpipeProcessingException;
import nl.pointpro.boilerpipeng.document.TextDocument;

import org.xml.sax.InputSource;

public interface ImageExtractorParser {

    void process(final TextDocument doc, final InputSource is, final ImageExtractorContentHandler contentHandler) throws BoilerpipeProcessingException;

}
