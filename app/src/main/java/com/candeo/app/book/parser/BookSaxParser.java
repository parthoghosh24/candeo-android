package com.candeo.app.book.parser;

import android.util.Log;

import com.candeo.app.models.ebook.Book;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Partho on 21/12/14.
 */
public class BookSaxParser extends DefaultHandler {

    private static final String TAG="BookSaxParser";
    Book mCurrentBook;
    String mStartTag;

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        Log.i(TAG, "startElement: " + localName);
        mStartTag = localName;
        if("item".equalsIgnoreCase(mStartTag))
        {
            String id = attributes.getValue("id");
            String href = attributes.getValue("href");
            String mediaType = attributes.getValue("media-type");
            mCurrentBook.addManifestItem(mCurrentBook.new Manifest(id, mediaType, href));
            if("css".equalsIgnoreCase(id))
            {
                mCurrentBook.setCssPath(href);
            }
        }
        else if("itemRef".equalsIgnoreCase(mStartTag))
        {
            String idref = attributes.getValue("idref");
            mCurrentBook.addSpineItem(idref);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)throws SAXException {
        StringBuilder mStringBuilder = new StringBuilder();
        for(int count=start;count<start+length;++count)
        {
            mStringBuilder.append(ch[count]);
        }

        String text = mStringBuilder.toString();

        if("title".equalsIgnoreCase(mStartTag))
        {
            mCurrentBook.setTitle(text);
        }
        if("identifier".equalsIgnoreCase(mStartTag))
        {
            mCurrentBook.setIdentifier(text);
        }
        if("creator".equalsIgnoreCase(mStartTag))
        {
            mCurrentBook.setAuthor(text);
        }
        if("publisher".equalsIgnoreCase(mStartTag))
        {
            mCurrentBook.setPublisher(text);
        }
        if("language".equalsIgnoreCase(mStartTag))
        {
            mCurrentBook.setLanguage(text);
        }
        if("date".equalsIgnoreCase(mStartTag))
        {
            mCurrentBook.setDate(text);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        Log.i(TAG, "endElement: "+localName);
        if(mStartTag.equalsIgnoreCase(localName))
        {
            mStartTag="";
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    public Book getBook(String opfPath,Book currentBook)
    {
        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader mXMReader = parser.getXMLReader();
            mCurrentBook = currentBook;
            mXMReader.setContentHandler(this);
            mXMReader.parse(new InputSource(new FileReader(new File(opfPath))));

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return mCurrentBook;
    }

}
