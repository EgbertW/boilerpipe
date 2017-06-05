package com.kohlschutter.boilerpipe.sax;


import com.kohlschutter.boilerpipe.BoilerpipeProcessingException;
import com.kohlschutter.boilerpipe.document.TextDocument;

import org.xml.sax.InputSource;


public interface MediaExtractorParser {

    void process(final TextDocument doc, final InputSource is, MediaExtractorContentHandler contentHandler) throws BoilerpipeProcessingException;

}
