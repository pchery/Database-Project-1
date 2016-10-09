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
    File titleFile = new File("titleFile.csv");
    FileWriter fileWriter = null;
    CSVWriter articleWriter = null;
    CSVWriter authorWriter = null;
    CSVWriter titleWriter = null;
    String[] nextFieldsLine = new String[4];
    String[] nextTitleLine = new String[1];
    String[] nextAuthorLine = new String[20];
    StringBuilder chars = new StringBuilder();
    boolean yearSet;
    boolean titleSet;
    boolean journalSet;


    @Override
    public void startDocument() {
        try {
            articleWriter = new CSVWriter(new FileWriter(fieldsFile));
            authorWriter = new CSVWriter(new FileWriter(authorsFile));
            titleWriter = new CSVWriter(new FileWriter(titleFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        unsetBools();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        if // (attributes.getLength() > 0) {
                (isPublished(qName)) {
            newEntry();
            // element = qName;
        } else if (qName.equals("author"))
            chars.setLength(0);
        else if (qName.equals("title")) {
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
        } else if (qName.equals("journal")) {
            if (!journalSet) {
                journalSet = true;
            } else
                nonsense = true;
            chars.setLength(0);
        }

    }

    private boolean isPublished(String s) {
        if (s.equals("article") || s.equals("inproceedings")
                || s.equals("proceedings") || s.equals("book")
                || s.equals("incollection") || s.equals("phdthesis")
                || s.equals("mastersthesis") || s.equals("www")
                || s.equals("data"))
            return true;
        return false;
    }

    private void newEntry() {
        chars.setLength(0);
        nonsense = false;
        nextFieldsLine[0] = primaryIndex + "";
        nextAuthorLine = new String[300];
        nextFieldsLine = new String[4];
        nextTitleLine = new String[1];
        authCount = 0;


    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equals("author")) {
            // enters here more times than it should!
            nextAuthorLine[authCount] = chars.toString();
            authCount++;
            if (authCount >= 50) {
                authCount = 0;
                System.out.println(primaryIndex + " " + localName);
                // nonsense = true;
            }
        } else if (qName.equals("year")) {
            nextFieldsLine[2] = chars.toString();
        } else if (qName.equals("title")) {
            nextTitleLine[0] = chars.toString();
        } else if (qName.equals("journal") || qName.equals("booktitle")) {
            nextFieldsLine[3] = chars.toString();
        } else if ((!nonsense)&& isPublished(qName)) {
            unsetBools();
            primaryIndex++;
            nextFieldsLine[1] = qName;
            articleWriter.writeNext(nextFieldsLine);
            authorWriter.writeNext(nextAuthorLine);
            titleWriter.writeNext(nextTitleLine);
        }
        else if (nonsense){
            unsetBools();
        }


    }

    private void unsetBools() {
        yearSet=false;
        titleSet=false;
        journalSet=false;

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
            titleWriter.close();
            System.out.println("End of document reached!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
