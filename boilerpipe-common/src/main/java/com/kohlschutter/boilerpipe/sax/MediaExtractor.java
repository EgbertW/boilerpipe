/**
 * Copyright (C) 2013 Christian Kohlschütter (ckkohl79@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kohlschutter.boilerpipe.sax;

import com.kohlschutter.boilerpipe.BoilerpipeExtractor;
import com.kohlschutter.boilerpipe.BoilerpipeProcessingException;
import com.kohlschutter.boilerpipe.document.Image;
import com.kohlschutter.boilerpipe.document.Media;
import com.kohlschutter.boilerpipe.document.TextDocument;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Extracts youtube and vimeo videos that are enclosed by extracted content.
 * 
 * @author Christian Kohlschütter, manuel.codiga@gmail.com
 */
public final class MediaExtractor {


	private MediaExtractorParser parser = null;

	private MediaExtractorContentHandler contentHandler = new MediaExtractorContentHandler();


	public MediaExtractor(MediaExtractorParser parser) {
		this.parser = parser;
	}


	/**
	 * Processes the given {@link TextDocument} and the original HTML text (as a String).
	 * 
	 * @param doc The processed {@link TextDocument}.
	 * @param origHTML The original HTML document.
	 * @return A List of enclosed {@link Image}s
	 * @throws BoilerpipeProcessingException if an error during extraction occure
	 */
	public List<Media> process(final TextDocument doc, final String origHTML) throws BoilerpipeProcessingException {
		return process(doc, new InputSource(new StringReader(origHTML)));
	}

	/**
	 * Processes the given {@link TextDocument} and the original HTML text (as an {@link InputSource}).
	 * 
	 * @param doc The processed {@link TextDocument}. The original HTML document.
	 * @return A List of enclosed {@link Image}s
	 * @throws BoilerpipeProcessingException
	 */
	public List<Media> process(final TextDocument doc, final InputSource is) throws BoilerpipeProcessingException {
		parser.process(doc, is, contentHandler);

		return contentHandler.getLinksHighlight();
	}

	/**
	 * Fetches the given {@link URL} using {@link HTMLFetcher} and processes the retrieved HTML using the specified
	 * {@link BoilerpipeExtractor}.
	 * 
	 * @param url the url of the document to fetch
	 * @param extractor extractor to use
	 * 
	 * @return A List of enclosed {@link Image}s
	 * @throws IOException
	 * @throws BoilerpipeProcessingException
	 * @throws SAXException
	 */
	@SuppressWarnings("javadoc")
	public List<Media> process(final URL url, final BoilerpipeExtractor extractor) throws IOException,
			BoilerpipeProcessingException, SAXException {
		final HTMLDocument htmlDoc = HTMLFetcher.fetch(url);

		BoilerpipeSAXInput saxInput = new BoilerpipeSAXInput(htmlDoc.toInputSource());
		final TextDocument doc = saxInput.getTextDocument(extractor.getHtmlParser());
		extractor.process(doc);

		final InputSource is = htmlDoc.toInputSource();

		return process(doc, is);
	}

	/**
	 * parses the media (picture, video) out of doc
	 * 
	 * @param doc document to parse the media out
	 * @param extractor extractor to use
	 * @return list of extracted media, with size = 0 if no media found
	 */
	public List<Media> process(String doc, final BoilerpipeExtractor extractor) {
		final HTMLDocument htmlDoc = new HTMLDocument(doc);
		List<Media> media = new ArrayList<Media>();
		TextDocument tdoc;

		try {
			BoilerpipeSAXInput saxInput = new BoilerpipeSAXInput(htmlDoc.toInputSource());
			tdoc = saxInput.getTextDocument(extractor.getHtmlParser());
			extractor.process(tdoc);
			final InputSource is = htmlDoc.toInputSource();
			media = process(tdoc, is);
		} catch (Exception e) {
			return null;
		}
		return media;
	}

}
