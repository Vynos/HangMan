package com.hangman;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.hangman.R;
import com.hangman.word.GlobalWords;
import com.hangman.word.WordStructure;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.view.MotionEvent;

public class SplashScreen extends Activity implements GlobalWords {

    protected boolean active = true;
    protected int time = 5000;
    Intent intent = new Intent();
    XmlPullParser xrParser;
    InputStream iStream;
    XmlPullParserFactory factory;
    DocumentBuilderFactory dbFactory;
    DocumentBuilder builder;
    Document wordDoc;
    NodeList wordList;
    NodeList childList;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        intent.setClass(this, HangManMain.class);

        Thread splashScreen = new Thread(){
            @Override
            public void run(){
                try{                     
                    iStream = getApplicationContext().getAssets().open("hmwords.xml");
//                    factory = XmlPullParserFactory.newInstance();
//                    factory.setNamespaceAware(true);
//                    xrParser = factory.newPullParser();
//                    xrParser.setInput(iStream, "UTF-8");
                    dbFactory = DocumentBuilderFactory.newInstance();
                    builder = dbFactory.newDocumentBuilder();
                    wordDoc = builder.parse(iStream);
                    wordList = wordDoc.getElementsByTagName("word");
//                    int eventType = xrParser.getEventType();
                    String cat = null;
                    String txt = null; 
                    
                    iStream.close();
                    
                    for(int i = 0; i < wordList.getLength(); i++){
                        childList = wordList.item(i).getChildNodes();
                        cat = childList.item(1).getFirstChild().getTextContent();
                        txt = childList.item(3).getFirstChild().getTextContent();
                        if((cat != null && txt != null))
                            structuredWordList.add(new WordStructure(txt, cat));
                        if(!active)
                            break;
                    }

//                    while(eventType != xrParser.END_DOCUMENT && active){                        
//                        if(eventType == XmlPullParser.END_TAG)
//                            eventType = xrParser.next();
//
//                        if(eventType == XmlPullParser.START_TAG){
//                            if("category".equalsIgnoreCase(xrParser.getName())){
//                                eventType = xrParser.next();
//                                if(eventType == XmlPullParser.TEXT)
//                                    cat = xrParser.getText();
//                            }
//                            if("text".equalsIgnoreCase(xrParser.getName())){
//                                eventType = xrParser.next();
//
//                                if(eventType == XmlPullParser.TEXT)
//                                    txt = xrParser.getText();
//
//                                if((cat != null && txt != null))
//                                    structuredWordList.add(new WordStructure(txt, cat));
//                            }
//                        }
//
//                        eventType = xrParser.next();
//
//                    }

                    //                    int waited = 0;
                    //                    while(active && (waited < time)){
                    //                        sleep(100);
                    //                        if(active)
                    //                            waited += 100;
                    //                    }
                    active = false;

                    startActivity(intent);
                } catch(ActivityNotFoundException anf){
                    anf.printStackTrace();
                } catch(Exception ex){
                    ex.printStackTrace();
                } finally {
                    finish();
                }
            }
        };

        splashScreen.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN)
            active = false;

        return true;
    }
}
