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


public class DatabaseSAXParser{
	public static void main(String[] args) {

		try {
			File inputFile = new File("dblp.xml");
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
	boolean nonsense = false;
	int primaryIndex = 0;
	int authCount = 0;

	File articlesFile = new File("articleFile.csv");
	File authorsFile = new File ("authorsFile.csv");
	File titleFile = new File ("titleFile.csv");
	FileWriter fileWriter = null;
	CSVWriter articleWriter = null;
	CSVWriter authorWriter = null;
	CSVWriter titleWriter = null;
	String[] nextArticleLine = new String[3];
	String[] nextTitleLine = new String[1];
	String[] nextAuthorLine = new String[20];
	
	@Override
	public void startDocument() {
		try {
			articleWriter = new CSVWriter(new FileWriter(articlesFile));
			authorWriter = new CSVWriter (new FileWriter(authorsFile));
			titleWriter = new CSVWriter (new FileWriter(titleFile));
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
		} else if (qName.equals("phdthesis")) {
			newEntry();
		} else if (qName.equals("inproceedings")) {
			newEntry();
		} else if (qName.equals("www")) {
			newEntry();
		} else if (qName.equals("mastersthesis")) {
			newEntry();
		}
		
	}

	private void newEntry() {
		nextArticleLine[0] = primaryIndex + "";
		primaryIndex++;
		nextAuthorLine = new String[20];
		authCount = 0;
		
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equals("article")&&!nonsense) {
			articleWriter.writeNext(nextArticleLine);
			authorWriter.writeNext(nextAuthorLine);
			titleWriter.writeNext(nextTitleLine);
			
		}
		else
			nonsense = false;
	}

	@Override
	public void characters(char ch[], int start, int length)
			throws SAXException {
		
		if (bAuthor){
			String auth = new String(ch, start, length);
			nextAuthorLine[authCount] = auth;
			authCount++;
			if (authCount >=20){
				authCount = 0;
				nonsense = true;
			}
			bAuthor = false;
		}
		else if (bTitle) {
			String tit = new String(ch, start, length);
			nextTitleLine[0] = tit;
			bTitle = false;
		} else if (bYear) {
			String year = new String(ch, start, length);
			nextArticleLine[1] = year;
			bYear = false;
		} else if (bJournal) {
			String journ = new String(ch, start, length);
			nextArticleLine[2] = journ;
			bJournal = false;
		} 
	}
	
	@Override
	public void endDocument() {
		try {
			articleWriter.close();
			authorWriter.close();
			titleWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
