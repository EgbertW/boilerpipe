package com.kohlschutter.boilerpipe.sax;


import com.kohlschutter.boilerpipe.document.TextDocument;

import org.xml.sax.InputSource;


public interface BoilerpipeHTMLParser {

    void parse(InputSource inputSource) throws Exception;

    TextDocument toTextDocument();

}
