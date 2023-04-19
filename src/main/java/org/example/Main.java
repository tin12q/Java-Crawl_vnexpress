package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static ArrayList<String> cat = new ArrayList<String>();
    private static ArrayList<String> catName = new ArrayList<String>();
    private static ArrayList<String> page = new ArrayList<String>();
    public static String extractSubstring(String url) {
        Pattern pattern = Pattern.compile("[0-9]+\\.html$");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return null;
        }
    }
    private static void getData(String url,String category) throws IOException, ParserConfigurationException, TransformerException {
        //String url = "https://vnexpress.net/ha-noi-yeu-cau-deo-khau-trang-tai-noi-dong-nguoi-4595164.html";
            Document doc = Jsoup.connect(url).get();

            // Extract the title of the article
            String title = doc.title();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Create a new document builder
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Create a new document
        org.w3c.dom.Document document = builder.newDocument();
        org.w3c.dom.Element root = document.createElement("Article");
        document.appendChild(root);
        org.w3c.dom.Element Domtitle = document.createElement("title");
        root.appendChild(Domtitle);
        Domtitle.setTextContent(title);



        System.out.println("Title: " + title+' '+category);
            File file = new File(category+"/"+extractSubstring(url).substring(0,7)+".xml");
            file.createNewFile();

            // Extract the content of the article
            Elements contentElement = doc.getElementsByClass("fck_detail");
            String content = contentElement.text();
            //FileWriter fw = new FileWriter(file);
        org.w3c.dom.Element Domcontent = document.createElement("content");
        root.appendChild(Domcontent);
        Domcontent.setTextContent(content);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
            //fw.write(content);
            //fw.close();
            //System.out.println("Content: " + content);
    }
    public static void main(String[] args) {
        for(int i=1;i<=8;i++)
        {
            //integer to string
            page.add("p"+Integer.toString(i));
        }
        try {
            /*Document doc = Jsoup.connect("https://google.com").get();
            System.out.println(doc.title());
            doc.outputSettings().prettyPrint(true);
            File file = new File("output.html");
            FileWriter writer = new FileWriter(file);
            writer.write(doc.outerHtml());
            writer.close();*/

            //get category
            File catFile = new File("cat.txt");
            Scanner sc = new Scanner(catFile);
            while(sc.hasNextLine()){
                catName.add(sc.nextLine());
                cat.add("https://vnexpress.net/"+catName.get(catName.size()-1));

                File directory = new File(String.valueOf(catName.get(catName.size()-1)));
                if (!directory.exists()) {
                    directory.mkdir();
                }
            }
            /*for (String i :cat
                 ) {
                System.out.println(i);
            }*/
            //get bao
            for (int i = 0; i < cat.size(); i++) {
                for (String p:page){
                    Document crawDoc= Jsoup.connect(cat.get(i)+'-'+p).get();
                    Elements cE = crawDoc.getElementsByClass("title-news");
                    for (Element e:cE
                    ) {
                        String txt= String.valueOf(e.getElementsByTag("a").attr("href"));
                        System.out.println(txt+" "+catName.get(i));
                        getData(txt,catName.get(i));
                    }
                }
            }

        } catch (IOException | ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}