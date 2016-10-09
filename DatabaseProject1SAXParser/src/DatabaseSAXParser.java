/**
 * Created by paulchery on 10/6/16.
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.opencsv.CSVWriter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DatabaseSAXParser {
    public static void main(String[] args) {

        try {
            File inputFile = new File("dblp.xml");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(true);
            SAXParser saxParser = factory.newSAXParser();
            UserHandler userhandler = new UserHandler();
            saxParser.parse(inputFile, userhandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class UserHandler extends DefaultHandler {

    boolean nonsense = false;
    int primaryIndex = 1;
    int authCount = 0;

    File fieldsFile = new File("fieldsFile.csv");
    File authorsFile = new File("authorsFile.csv");
    FileWriter fileWriter = null;
    CSVWriter articleWriter = null;
    CSVWriter authorWriter = null;
    String[] nextFieldsLine;
    String[] nextAuthorLine;
    StringBuilder chars = new StringBuilder();
    boolean yearSet;
    boolean titleSet;
    boolean journalSet;
    long startTime = System.currentTimeMillis();

    @Override
    public void startDocument() {
        try {
            articleWriter = new CSVWriter(new FileWriter(fieldsFile));
            authorWriter = new CSVWriter(new FileWriter(authorsFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        unsetBools();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        if (isPublished(qName)) {
            newEntry();
        } else if (qName.equals("author")) {
            chars.setLength(0);
        }else if (qName.equals("title")) {
            if (!titleSet) {
                titleSet = true;
            } else
                nonsense = true;
            chars.setLength(0);
        } else if (qName.equals("year")) {
            if (!yearSet) {
                yearSet = true;
            } else
                nonsense = true;
            chars.setLength(0);
        } else if (inConference(qName)) {
            if (!journalSet) {
                journalSet = true;
            } else
                nonsense = true;
            chars.setLength(0);
        }else if (otherPublication(qName)){
            nonsense=true;
        }

    }

    private boolean isPublished(String s) {
        if (s.equals("article") || s.equals("inproceedings")
                || s.equals("proceedings") || s.equals("book")
                || s.equals("incollection"))
            return true;
        return false;
    }

    private void newEntry() {
        chars.setLength(0);
        nonsense = false;
        nextAuthorLine = new String[300];
        nextFieldsLine = new String[5];
        nextFieldsLine[0] = primaryIndex + "";
        authCount = 0;

    }

    public boolean otherPublication(String s){
        if(s.equals("www") || s.equals("mastersthesis") || s.equals("phdthesis") || s.equals("data")){
            return true;
        }
        return false;
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equals("author")) {
            // some articles have over 250 authors
            nextAuthorLine[authCount] = chars.toString();
            authCount++;
            if(authCount>300)
                System.out.println(primaryIndex);
        } else if (qName.equals("year")) {
            nextFieldsLine[2] = chars.toString();
        } else if (qName.equals("title")) {
            nextFieldsLine[4] = chars.toString();
        } else if (inConference(qName)) {
            nextFieldsLine[3] = chars.toString();
        } else if ((!nonsense) && isPublished(qName)) {
            unsetBools();
            primaryIndex++;
            nextFieldsLine[1] = qName;
            articleWriter.writeNext(nextFieldsLine);
            authorWriter.writeNext(dynamicArray());
        } else if (nonsense) {
            unsetBools();
            authCount=0;
        }


    }

    private String[] dynamicArray() {
        String[] arr = new String[authCount];
        for(int i=0; i<authCount; i++){
            arr[i]=nextAuthorLine[i];
        }
        return arr;
    }

    private boolean inConference(String qName) {
        if (qName.equals("journal") || qName.equals("booktitle"))
            return true;
        return false;
    }

    private void unsetBools() {
        yearSet = false;
        titleSet = false;
        journalSet = false;

    }

    @Override
    public void characters(char ch[], int start, int length)
            throws SAXException {
        chars.append(new String(ch, start, length));
    }

    @Override
    public void endDocument() {
        try {
            articleWriter.close();
            authorWriter.close();
            System.out.println("End of document reached!");
            long endTime = System.currentTimeMillis()-startTime;
            System.out.println("Program took: "+endTime/1000 + " seconds to run");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
