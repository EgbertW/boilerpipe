package com.kohlschutter.boilerpipe.sax;


import com.kohlschutter.boilerpipe.BoilerpipeProcessingException;
import com.kohlschutter.boilerpipe.document.TextDocument;

import org.xml.sax.InputSource;

public interface ImageExtractorParser {

    void process(final TextDocument doc, final InputSource is, final ImageExtractorContentHandler contentHandler) throws BoilerpipeProcessingException;

}
