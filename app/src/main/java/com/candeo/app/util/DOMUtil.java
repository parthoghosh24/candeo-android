package com.candeo.app.util;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Partho on 21/12/14.
 */

public class DOMUtil {

    private static final String TAG="DOM Util";

    public static Document getDom(String fileName)
    {
        return getDom(new File(fileName));
    }

    public static Document getDom(File file)
    {
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(file);
        }
        catch(FileNotFoundException fe)
        {
            fe.printStackTrace();
        }
        return getDom(fis);

    }

    public static Document getDom(InputStream istream)
    {
        Document document=null;
        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(istream);
        }
        catch(ParserConfigurationException pce)
        {
            pce.printStackTrace();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        catch(SAXException se)
        {
            se.printStackTrace();
        }
        return document;


    }
}
