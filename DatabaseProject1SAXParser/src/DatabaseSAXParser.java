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
    public static void main(String[] args){

        try {
            File inputFile = new File("input.txt");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            UserHandler userhandler = new UserHandler();
            saxParser.parse(inputFile, userhandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
class UserHandler extends DefaultHandler {

    boolean bFirstName = false;
    boolean bLastName = false;
    boolean bNickName = false;
    boolean bMarks = false;

    File outputFile = new File("output.csv");
    FileWriter fileWriter = null;
    CSVWriter writer = null;
    String[] nextLine = new String[5];


    @Override
    public void startElement(String uri,
                             String localName, String qName, Attributes attributes)
            throws SAXException {
        try{
            fileWriter = new FileWriter(outputFile);
            writer = new CSVWriter(fileWriter);
        }catch(IOException e){
            e.printStackTrace();
        }
        if (qName.equalsIgnoreCase("student")) {
            String rollNo = attributes.getValue("rollno");
            System.out.println("Roll No : " + rollNo);
            nextLine[0] = rollNo;
            //nextLine[0] = "Hello";
            //writer.writeNext(nextLine);
        } else if (qName.equalsIgnoreCase("firstname")) {
            bFirstName = true;
        } else if (qName.equalsIgnoreCase("lastname")) {
            bLastName = true;
        } else if (qName.equalsIgnoreCase("nickname")) {
            bNickName = true;
        }
        else if (qName.equalsIgnoreCase("marks")) {
            bMarks = true;
        }
    }

    @Override
    public void endElement(String uri,
                           String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("student")) {
            System.out.println("End Element :" + qName);
            System.out.println("nextline: " + nextLine[0]);
            writer.writeNext(nextLine);
            try {
                writer.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void characters(char ch[],
                           int start, int length) throws SAXException {
        if (bFirstName) {
            String firstName = new String(ch, start, length);
            System.out.println("First Name: "
                    + firstName);
            nextLine[1] = firstName;
            bFirstName = false;
        } else if (bLastName) {
            String lastName = new String(ch, start, length);
            System.out.println("Last Name: "
                    + lastName);
            nextLine[2] = lastName;
            bLastName = false;
        } else if (bNickName) {
            String nickName = new String(ch, start, length);
            System.out.println("Nick Name: "
                    + nickName);
            nextLine[3] = nickName;
            bNickName = false;
        } else if (bMarks) {
            String marks = new String(ch, start, length);
            System.out.println("Marks: "
                    + marks);
            nextLine[4] = marks;
            bMarks = false;
        }
    }
}