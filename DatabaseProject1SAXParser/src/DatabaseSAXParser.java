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

public class DatabaseSAXParser  {
	public static void main(String[] args) {

		try {
			File inputFile = new File("input2.txt");
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

	boolean bAuthor = false;
	boolean bTitle = false;
	boolean bYear = false;
	boolean bJournal = false;
	int primaryIndex = 0;
	int authCount = 0;

	File articlesFile = new File("articleFile.csv");
	File authorsFile = new File ("authorsFile.csv");
	FileWriter fileWriter = null;
	CSVWriter articleWriter = null;
	CSVWriter authorWriter = null;
	String[] nextArticleLine = new String[5];
	String[] nextAuthorLine = new String[10];
	
	@Override
	public void startDocument() {
		try {
			articleWriter = new CSVWriter(new FileWriter(articlesFile));
			authorWriter = new CSVWriter (new FileWriter(authorsFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		if (qName.equals("article")) {
			newEntry();
		} else if (qName.equals("author")) {
			bAuthor = true;
		} else if (qName.equals("year")) {
			bYear = true;
		} else if (qName.equals("title")) {
			bTitle = true;
		} else if (qName.equals("journal")) {
			bJournal = true;
		}
	}

	private void newEntry() {
		nextArticleLine[0] = primaryIndex + "";
		primaryIndex++;
		nextAuthorLine = new String[10];
		authCount = 0;
		
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equals("article")) {
			articleWriter.writeNext(nextArticleLine);
			authorWriter.writeNext(nextAuthorLine);
		}
	}

	@Override
	public void characters(char ch[], int start, int length)
			throws SAXException {
		
		if (bAuthor){
			String auth = new String(ch, start, length);
			nextAuthorLine[authCount] = auth;
			authCount++;
			bAuthor = false;
		}
		else if (bTitle) {
			String tit = new String(ch, start, length);
			nextArticleLine[1] = tit;
			bTitle = false;
		} else if (bYear) {
			String year = new String(ch, start, length);
			nextArticleLine[2] = year;
			bYear = false;
		} else if (bJournal) {
			String journ = new String(ch, start, length);
			nextArticleLine[3] = journ;
			bJournal = false;
		} 
	}

	@Override
	public void endDocument() {
		try {
			articleWriter.close();
			authorWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
