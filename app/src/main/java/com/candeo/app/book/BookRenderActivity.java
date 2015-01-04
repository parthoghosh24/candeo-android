package com.candeo.app.book;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.models.ebook.Book;
import com.candeo.app.models.ebook.TableOfContents;
import com.candeo.app.util.DOMUtil;
import com.candeo.app.util.FileUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class BookRenderActivity extends Activity {

    private static final String TAG = "BookRender";
    private WebView bookView;
    private String mBaseUrl;
    private int mCurrentChapter=1;
    Book unzippedBook;
    ArrayList<TableOfContents.Chapter> chapterList;
    private static final int SCREEN_WIDTH_CORRECTION = 5;
    private static final int SCREEN_HEIGHT_CORRECTION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_render);
        unzippedBook = getIntent().getParcelableExtra(Configuration.INTENTBOOK);
        chapterList =getIntent().getParcelableArrayListExtra(Configuration.INTENTCHAPTERLIST);
        mBaseUrl=getIntent().getStringExtra(Configuration.INTENTBASEURL);
        Log.e(TAG,"BASE URL "+mBaseUrl);
        initWidgets();
        openChapter(mCurrentChapter, bookView);
    }

    private void initWidgets() {
        bookView = (WebView) findViewById(R.id.epub_renderer);
        bookView.clearCache(true);
        bookView.clearHistory();
        bookView.setClickable(false);
        bookView.setSelected(true);
        bookView.setHorizontalScrollBarEnabled(false);
        bookView.setVerticalScrollBarEnabled(false);
        bookView.getSettings().setJavaScriptEnabled(true);
        bookView.getSettings().setUseWideViewPort(true);
        bookView.getSettings().setLoadWithOverviewMode(true);
        bookView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.e(TAG,"INCOMING ALERT MESSAGE IS "+message);
                result.confirm();
                return true;
            }
        });
    }



    private void openChapter(int currentChapter, WebView webview)
    {
        String baseUrl= "file://"+mBaseUrl+ File.separator;
        Log.i(TAG, "base url " + baseUrl);
        String chapterPath = mBaseUrl+File.separator+chapterList.get(currentChapter-1).getUrl();
        Log.i(TAG, "chapterPath "+chapterPath);
        getWindowManager().getDefaultDisplay().getMetrics(CandeoApplication.displayMetrics);
        Log.e(TAG, "Screen Width is "+CandeoApplication.displayMetrics.widthPixels);
        Log.e(TAG, "Screen Height is "+CandeoApplication.displayMetrics.heightPixels);
        int width = CandeoApplication.displayMetrics.widthPixels+SCREEN_WIDTH_CORRECTION;
        int height = CandeoApplication.displayMetrics.heightPixels+SCREEN_HEIGHT_CORRECTION;
        String data=preprocess(chapterPath,width,height);
        Log.i(TAG, data);
        bookView.loadDataWithBaseURL(baseUrl, data, "text/html", "UTF-8", null);
        mCurrentChapter = currentChapter;
    }


    private String generateHTML(String chapterPath) {
        String output = "";
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(chapterPath));

            StringWriter response = new StringWriter();
            StreamResult result = new StreamResult(response);
            Properties properties = new Properties();
            properties.put(OutputKeys.METHOD, "html");
            TransformerFactory tfactory = TransformerFactory.newInstance();
            Transformer transformer = tfactory.newTransformer();
            transformer.setOutputProperties(properties);
            transformer.transform(new DOMSource(doc), result);
            output=response.toString();

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return output;
    }
    private void nextPage()
    {
        Log.i(TAG, "current chap "+mCurrentChapter);
        if(mCurrentChapter+1>chapterList.size())
        {
            Toast.makeText(getApplicationContext(), "Last page", Toast.LENGTH_SHORT).show();
        }
        else
        {
            openChapter(++mCurrentChapter, bookView);
        }
        bookView.loadUrl("javascript:nextPage()");

    }

    private void prevPage()
    {
        Log.i(TAG, "current chap "+mCurrentChapter);
        if(mCurrentChapter-1<=0)
        {
            Toast.makeText(getApplicationContext(), "First page", Toast.LENGTH_SHORT).show();
        }
        else
        {
            openChapter(--mCurrentChapter, bookView);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "Stopped");
        deleteDir();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Stopped");
    }

    private void deleteDir()
    {
        String zipDir= mBaseUrl.substring(0, mBaseUrl.lastIndexOf(File.separator));
        Log.i(TAG, "zipDir "+zipDir);
        FileUtil mBbFileUtil= new FileUtil();
        File zipDirFile = new File(zipDir);
        mBbFileUtil.deleteDir(zipDirFile);
    }

    private static void addMetaLink(Document doc, Element element)
    {
        Element metaElement = doc.createElement("meta");
        metaElement.setAttribute("name","viewport");
        metaElement.setAttribute("content","width=device-width, initial-scale=1.0, user-scalable=no");
        element.appendChild(metaElement);
        element.appendChild(doc.createTextNode("\n"));
    }

    private static void addJavaScriptLink(Document doc, Element element, String path) {
        Element scriptElement = doc.createElement("script");
        scriptElement.setAttribute("type", "text/javascript");
//        scriptElement.setAttribute("src", "url('file:///android_asset/" + path+"')");
        scriptElement.setAttribute("src",path);
        element.appendChild(scriptElement);
        element.appendChild(doc.createTextNode("\n"));
    }

    private static void addCssLink(Document doc, Element element, String path)
    {
        Element linkElement = doc.createElement("link");
//        linkElement.setAttribute("href","url('file:///android_asset/" + path+"')");
        linkElement.setAttribute("href",path);
        linkElement.setAttribute("rel","stylesheet");
        linkElement.setAttribute("type","text/css");
        element.appendChild(linkElement);
        element.appendChild(doc.createTextNode("\n"));

    }


    public static String preprocess(String chapter, int width, int height)  {

        /*
         * 1. prepare dom
         */
        // get dom
        Document doc = null;
        try {
            doc = DOMUtil.getDom(chapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
         * 2. handle dom tree
         */
        NodeList nodeList = doc.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Document.ELEMENT_NODE) {
                if ("head".equalsIgnoreCase(node.getNodeName())) {
                    Element headElement = (Element) node;

                    addMetaLink(doc, headElement);
                    //Enabling Monocle js
                    addJavaScriptLink(doc, headElement, "js/monocle/src/monocore.js");
                    // append monocle interface script
                    addJavaScriptLink(doc, headElement, "js/ui.js");
                    // Monocle core css
                    addCssLink(doc,headElement,"js/monocle/styles/monocore.css");
                }
                /*
                 * <body> Element
                 *
                 * 1. insert div for monocle
                 * 2. set font size
                 */
                else if ("body".equalsIgnoreCase(node.getNodeName())) {
                    Element bodyElement = (Element) node;

                    // 1. insert div for monocle
                    Element divElement = doc.createElement("div");
                    divElement.setAttribute("id", "reader");
                    divElement.setAttribute("style", "width:" + width + "px; height:" + height + "px; border:none;");

                    NodeList bodyChildList = bodyElement.getChildNodes();
                    for (int j = 0; j < bodyChildList.getLength(); j++) {
                        Node bodyChild = (Node) bodyChildList.item(j);
                        divElement.appendChild(bodyChild);
                    }
                    bodyElement.appendChild(divElement);

                    // 2. clear attributes
                    bodyElement.removeAttribute("xml:lang");
                }
                /*
                 * <img> Element
                 *
                 * 1. image max size
                 */
                else if ("img".equalsIgnoreCase(node.getNodeName())) {
                    Element imgElement = (Element) node;

                    // 1. image max size
                    int maxImageWidth = width - 30;
                    int maxImageHeight = height - 80;
                    Log.d(TAG, "maxImageWidth: " + maxImageWidth);
                    Log.d(TAG, "maxImageHeight: " + maxImageHeight);

                    imgElement.setAttribute("style", "max-width:" + maxImageWidth + "px; max-height:" + maxImageHeight + "px;");
                }
            }
        }

        /*
         * 3. DOM to string
         */
        StringWriter outText = new StringWriter();
        StreamResult sr = new StreamResult(outText);

        Properties oprops = new Properties();
        oprops.put(OutputKeys.METHOD, "html");
        // oprops.put("indent-amount", "4");

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = null;
        try {
            trans = tf.newTransformer();
            trans.setOutputProperties(oprops);
            trans.transform(new DOMSource(doc), sr);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return outText.toString();
    }
}
