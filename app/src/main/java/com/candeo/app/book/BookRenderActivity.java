package com.candeo.app.book;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.models.ebook.Book;
import com.candeo.app.models.ebook.TableOfContents;
import com.candeo.app.util.FileUtil;

import org.w3c.dom.Document;
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
    private Button leftButton;
    private Button rightButton;
    private String mBaseUrl;
    private int mCurrentChapter=1;
    Book unzippedBook;
    ArrayList<TableOfContents.Chapter> chapterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_render);
        unzippedBook = getIntent().getParcelableExtra(Configuration.INTENTBOOK);
        chapterList =getIntent().getParcelableArrayListExtra(Configuration.INTENTCHAPTERLIST);
        mBaseUrl=getIntent().getStringExtra(Configuration.INTENTBASEURL);
        Log.e(TAG,"BASE URL "+mBaseUrl);
        initWidgets();
        activateButtons();
        //new BookOpenTask().execute();
        openChapter(mCurrentChapter, bookView);
    }

    private void initWidgets() {
        bookView = (WebView) findViewById(R.id.epub_renderer);
        leftButton = (Button) findViewById(R.id.left_btn);
        rightButton = (Button) findViewById(R.id.right_btn);
    }


    private void activateButtons() {
        leftButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                prevPage();
            }

        });

        rightButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                nextPage();
            }
        });
    }


    private void openChapter(int currentChapter, WebView webview)
    {
        String baseUrl= "file://"+mBaseUrl+ File.separator;
        Log.i(TAG, "base url " + baseUrl);
        String chapterPath = mBaseUrl+File.separator+chapterList.get(currentChapter-1).getUrl();
        Log.i(TAG, "chapterPath "+chapterPath);
        String data=generateHTML(chapterPath);
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
    class BookOpenTask extends AsyncTask<Void, Void, Void>
    {


        @Override
        protected Void doInBackground(Void... params) {
            BookRenderActivity.this.openChapter(mCurrentChapter, bookView);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }
}
