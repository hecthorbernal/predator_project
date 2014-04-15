package preprocessing;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class StaXParser {
	static final String CONVERSATION = "conversation";
	static final String ID = "id";
	static final String MESSAGE = "message";
	static final String LINE = "line";
	static final String AUTHOR = "author";
	static final String TIME = "time";
	static final String TEXT = "text";

	@SuppressWarnings({ "unchecked", "null" })
	public List<Conversation> readConfig(String configFile) {
		List<Conversation> items = new ArrayList<Conversation>();

		try {
			// First create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();

			// Setup a new eventReader
			InputStream in = new FileInputStream(configFile);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

			// Read the XML document
			Conversation item = null;
			Message message = null;

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();

					// If we have a conversation element we create a new conversation
					if (startElement.getName().getLocalPart() == (CONVERSATION)) {
						item = new Conversation();

						// Read the attribute from this tag and add the id to the object
						Iterator<Attribute> attributes = startElement
								.getAttributes();
						while (attributes.hasNext()) {
							Attribute attribute = attributes.next();
							if (attribute.getName().toString().equals(ID)) {
								item.setId(attribute.getValue());
							}							
						}
					}


					// if we have a message we add it to the current conversation
					if (event.isStartElement()) {
						if (event.asStartElement().getName().getLocalPart()
								.equals(MESSAGE)) {	

							message = new Message();

							// Read the attribute from this tag and add the line id to the object
							Iterator<Attribute> attributes = startElement
									.getAttributes();
							while (attributes.hasNext()) {
								Attribute attribute = attributes.next();
								if (attribute.getName().toString().equals(LINE)) {
									message.setLine(attribute.getValue());
								}
							}

							// add author to message
							event = eventReader.nextTag(); //move to next tag <author>
							event = eventReader.nextEvent(); // move to next event
							message.setAuthor(event.asCharacters().getData());
							event = eventReader.nextEvent(); //move to end tag </author>

							// add time to message
							event = eventReader.nextTag();
							event = eventReader.nextEvent(); // move to next event
							message.setTime(event.asCharacters().getData());
							event = eventReader.nextEvent(); //move to end tag

							// add text to message
							event = eventReader.nextTag(); // move to next tag
							event = eventReader.nextEvent(); // move to next event 
							
							// check if taext has content
							if (event.isCharacters()) {
							message.setText(event.asCharacters().getData());
							event = eventReader.nextEvent(); //move to end tag </text>
							} else {
							message.setText(""); //no text in message
							}
							
							// add message to conversation
							item.messages.add(message);

							continue;
						}

					}

				}

				// If we reach the end of an Conversation element we add it to the list
				if (event.isEndElement()) {
					EndElement endElement = event.asEndElement();
					if (endElement.getName().getLocalPart() == (CONVERSATION)) {
						items.add(item);
					}
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		return items;
	}

}
