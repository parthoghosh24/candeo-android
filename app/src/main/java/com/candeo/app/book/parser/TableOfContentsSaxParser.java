package com.candeo.app.book.parser;

import android.util.Log;

import com.candeo.app.models.ebook.TableOfContents;

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
public class TableOfContentsSaxParser extends DefaultHandler {
    private static final String TAG="TOCSaxParser";
    String mStartTag;
    private TableOfContents mTableOfContents = new TableOfContents();
    private TableOfContents.Chapter chapter = new TableOfContents.Chapter();
    int sequence=0;
    String prevUrl="";

    public void startDocument() throws SAXException {
        super.startDocument();
    }

    public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws SAXException {
        Log.i(TAG, "StartTag " + localName);
        mStartTag =localName;
        if("content".equalsIgnoreCase(mStartTag))
        {
            String src= attributes.getValue("src");
            int index = src.lastIndexOf("#");

            String url =(index>0)?src.substring(0, index):src;
            String anchor=(index>0)?src.substring(index+1):"";

            Log.i(TAG, "Url: "+url);
            Log.i(TAG,"prevUrl: "+prevUrl);
            Log.i(TAG,"anchor: "+anchor);

            if(!url.equalsIgnoreCase(prevUrl))
            {
                chapter.setSequence(Integer.toString(++sequence));
                chapter.setUrl(url);
                chapter.setAnchor(anchor);
                Log.i(TAG, "Chapter"+ chapter);
                mTableOfContents.addChapterItem(chapter);

                chapter = new TableOfContents.Chapter();
            }

            prevUrl=url;

        }

    }

    public void characters(char[] ch, int start, int length) throws SAXException {

        super.characters(ch, start, length);
        StringBuilder builder = new StringBuilder();
        for(int count=0; count<(start+length);count++)
        {
            builder.append(ch[count]);
        }

        String text = builder.toString();
        if("text".equalsIgnoreCase(mStartTag))
        {

            chapter.setTitle(text);
            Log.i(TAG, "Chapter: "+chapter);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        Log.i(TAG, "EndTag "+localName);
        if(mStartTag.equalsIgnoreCase(localName))
        {
            mStartTag="";
        }

    }

    public void endDocument() throws SAXException {
        super.endDocument();
    }


    public TableOfContents getToc(String tocPath)
    {
        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader mXMReader = parser.getXMLReader();
            mXMReader.setContentHandler(this);
            mXMReader.parse(new InputSource(new FileReader(new File(tocPath))));

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
        Log.i(TAG, "TOC: "+mTableOfContents);
        return mTableOfContents;
    }
}
