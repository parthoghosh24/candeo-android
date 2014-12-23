package com.candeo.app.book;

import org.w3c.dom.Element;
import android.util.Log;

import com.candeo.app.Configuration;
import com.candeo.app.book.parser.BookSaxParser;
import com.candeo.app.book.parser.TableOfContentsSaxParser;
import com.candeo.app.models.ebook.Book;
import com.candeo.app.models.ebook.TableOfContents;
import com.candeo.app.util.FileUtil;
import com.candeo.app.util.ZipUtil;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Partho on 21/12/14.
 */

public class EpubCore {

    private static final String TAG = "EpubCore";
    FileUtil mFileUtil;
    ZipUtil mZipUtil;
    TableOfContents mTableofContents = new TableOfContents();
    String mBaseUrl = "";

    public EpubCore() {
        initUtils();
    }

    private void initUtils() {
        mFileUtil = new FileUtil();
        mZipUtil = new ZipUtil();
    }

    public String unzipEpub(String bookPath, String fileName) {
        String unzippedPath = "";
        unzippedPath = mZipUtil.unzipBook(bookPath, Configuration.CANDEO_BOOKSROOT
                + File.separator + fileName);
        return unzippedPath;
    }

    public boolean removeUnzippedEpubDir(String unzippedPath) {
        boolean result = false;
        return result;
    }

    public String getOPFFilePath(File containerFile, String unzippedBook) {
        String filePath = "";

        try {
            InputStream reader = new FileInputStream(containerFile);
            XmlPullParser containerFileParser = XmlPullParserFactory
                    .newInstance().newPullParser();
            containerFileParser.setInput(reader, "UTF_8");
            int eventType = containerFileParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (containerFileParser.getName().equalsIgnoreCase(
                                "rootfile")) {
                            Log.v(TAG,
                                    "Container path: "
                                            + unzippedBook
                                            + File.separator
                                            + containerFileParser
                                            .getAttributeValue(null,
                                                    "full-path"));
                            filePath = unzippedBook
                                    + File.separator
                                    + containerFileParser.getAttributeValue(null,
                                    "full-path");
                        }
                        break;
                }

                eventType = containerFileParser.next();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return filePath;

    }

    public Book loadBook(String unzippedBook, Book currentBook) {

        // get Content path
        Log.v(TAG, "unzippedBook " + unzippedBook);
        Log.v(TAG, "unzippedBook Meta " + unzippedBook
                + Configuration.CONTAINERFOLDER);
        File containerFile = new File(unzippedBook
                + Configuration.CONTAINERFOLDER);

        // Get content opf file
        String opfFilePath = getOPFFilePath(containerFile, unzippedBook);
        Log.i(TAG, "opffilePath " + opfFilePath);
        mBaseUrl = opfFilePath.substring(0, opfFilePath.lastIndexOf("/"));
        Log.i(TAG, "Base url " + mBaseUrl);
        String mTocPath = "";

        // Populate book bean
        if (!opfFilePath.isEmpty()) {
            currentBook.setOpfFileName(opfFilePath);
            BookSaxParser bookParser = new BookSaxParser();
            currentBook = bookParser.getBook(opfFilePath, currentBook);
        }

        // get Toc path

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(opfFilePath));

            Element ncxElement = document.getElementById("ncx");
            mTocPath = mBaseUrl + File.separator
                    + ncxElement.getAttributeNode("href").getValue();
            Log.i(TAG, "TOC path:" + mTocPath);

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

        if (!mTocPath.isEmpty()) {
            TableOfContentsSaxParser tocSaxParser = new TableOfContentsSaxParser();
            mTableofContents = tocSaxParser.getToc(mTocPath);

        }
        return currentBook;
    }



    public TableOfContents getToc() {
        return mTableofContents;
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }

}
