package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Help extends AppCompatActivity {

    public static String KEYWORD = "";

    ImageView helpBackIv;
    RecyclerView helpRecycler;
    TextView accountSearchEt;
    FaqsAdapter faqsAdapter;

    ArrayList<Faqs> faqsArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        getSupportActionBar().hide();

        KEYWORD = "";

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        helpBackIv = (ImageView) findViewById(R.id.helpBackIv);
        helpRecycler = (RecyclerView) findViewById(R.id.helpRecycler);

        helpRecycler = (RecyclerView) findViewById(R.id.helpRecycler);
        helpRecycler.setLayoutManager(new LinearLayoutManager(this));
        helpRecycler.setHasFixedSize(true);

        accountSearchEt = findViewById(R.id.accountSearchEt);

        getData();
        manualAddSearchListener();
    }

    private void manualAddSearchListener(){
        accountSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                KEYWORD = accountSearchEt.getText().toString();
                displayContent();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void getData() {

        try {
            //read xml file
            InputStream stream = getAssets().open("selfhelp.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stream);

            Element element = doc.getDocumentElement();
            element.normalize();

            //get all articles from xml file
            NodeList nList = doc.getElementsByTagName("article");

            for (int i=0; i<nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;

                    String title = getValue("title", element2);
                    String body = getValue("body", element2);

                    faqsArrayList.add(new Faqs(title, body));

                }
            }

            displayContent();

        } catch (Exception e) {
            Log.d("DEBUGGER>>>", e.getMessage());
        }

    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    private void displayContent(){
        faqsAdapter = new FaqsAdapter(this, faqsArrayList);
        helpRecycler.setAdapter(faqsAdapter);
        faqsAdapter.notifyDataSetChanged();
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}