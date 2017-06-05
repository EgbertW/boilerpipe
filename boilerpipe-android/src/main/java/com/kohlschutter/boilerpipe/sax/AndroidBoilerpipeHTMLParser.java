package com.kohlschutter.boilerpipe.sax;

import com.kohlschutter.boilerpipe.BoilerpipeDocumentSource;
import com.kohlschutter.boilerpipe.document.TextDocument;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


public class AndroidBoilerpipeHTMLParser implements BoilerpipeDocumentSource, BoilerpipeHTMLParser {

    private BoilerpipeHTMLContentHandler contentHandler;


    public AndroidBoilerpipeHTMLParser() {
        this(new BoilerpipeHTMLContentHandler());
    }

    public AndroidBoilerpipeHTMLParser(BoilerpipeHTMLContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }


    @Override
    public void parse(InputSource inputSource) throws Exception {
        XMLReader parser = XMLReaderFactory.createXMLReader("org.ccil.cowan.tagsoup.Parser");

        parser.setContentHandler(contentHandler);

        parser.parse(inputSource);
    }

    @Override
    public TextDocument toTextDocument() {
        return contentHandler.toTextDocument();
    }

}
