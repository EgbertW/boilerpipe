package nl.pointpro.boilerpipeng.sax;


import nl.pointpro.boilerpipeng.document.TextDocument;

import org.xml.sax.InputSource;


public interface BoilerpipeHTMLParser {

    void parse(InputSource inputSource) throws Exception;

    TextDocument toTextDocument();

}
