package space.amareth.mood;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Volodymyr on 22/12/2015.
 */
public class History
{
    //Creation date -> entry
    private HashMap<String, HistoryEntry> entries = new HashMap<>();

    public void fromXml(String xml) throws Exception
    {
        entries.clear();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(xml);
        Element root = doc.getDocumentElement();

        NodeList entries = root.getElementsByTagName("entry");

        for(int iEntry = 0; iEntry < entries.getLength(); ++iEntry)
        {
            Node entry = entries.item(iEntry);

            if(entry.getNodeType()==Node.ELEMENT_NODE)
            {
                Element entryElement = (Element)entry;

                NodeList whatWentWellNodes = entryElement.getElementsByTagName("what_went_well");
                NodeList whatWentNotWellNodes = entryElement.getElementsByTagName("what_went_not_well");
                NodeList whatIWillDoNodes = entryElement.getElementsByTagName("what_i_will_do");

                ArrayList<String> whatWentWell = new ArrayList<>(whatWentWellNodes.getLength());
                ArrayList<String> whatWentNotWell = new ArrayList<>(whatWentNotWellNodes.getLength());
                ArrayList<String> whatIWillDo = new ArrayList<>(whatIWillDoNodes.getLength());
                int rating = Integer.parseInt(entryElement.getAttribute("rating"));

                for(int iInnerNode = 0; iInnerNode < whatWentWellNodes.getLength(); ++iInnerNode)
                    whatWentWell.set(iInnerNode, whatWentWellNodes.item(iInnerNode).getTextContent());
                for(int iInnerNode = 0; iInnerNode < whatWentNotWellNodes.getLength(); ++iInnerNode)
                    whatWentNotWell.set(iInnerNode, whatWentNotWellNodes.item(iInnerNode).getTextContent());
                for(int iInnerNode = 0; iInnerNode < whatIWillDoNodes.getLength(); ++iInnerNode)
                    whatIWillDo.set(iInnerNode, whatIWillDoNodes.item(iInnerNode).getTextContent());

                this.entries.put(entryElement.getAttribute("creation_time"),
                        new HistoryEntry(whatWentWell, whatWentNotWell, whatIWillDo, rating));
            }
        }

        /*
            Example XML file:

            <?xml version="1.0"?>
            <entries version="1.0">
            <entry rating="4">
            <what_went_well>Candyyy</what_went_well>
            <what_went_well>More candy</what_went_well>
            <what_went_well>MORE CANDY</what_went_well>
            <what_went_not_well>Not enough candy :(</what_went_not_well>
            <what_i_will_do>I will get more candy</what_i_will_do>
            <what_i_will_do>I will also get some chocolate</what_i_will_do>
            </entry>
            <entry rating="5">
            <what_went_well>chocolate</what_went_well>
            <what_went_well>More chocolate</what_went_well>
            <what_went_well>MORE CHOCOLATE</what_went_well>
            <what_went_not_well>Not enough chocolate :(</what_went_not_well>
            <what_i_will_do>I will get more chocolate</what_i_will_do>
            <what_i_will_do>I will also get some ice cream</what_i_will_do>
            </entry>
            </entries>
        */
    }

    public String toXml() throws Exception
    {
        DocumentBuilder  db = DocumentBuilderFactory.newInstance().newDocumentBuilder();;
        Document doc = db.newDocument();

        Element root = doc.createElement("entries");
        root.setAttribute("version", "1.0");
        doc.appendChild(root);

        for(Map.Entry<String, HistoryEntry> entryPair : entries.entrySet())
        {
            HistoryEntry entry = entryPair.getValue();

            Element e = doc.createElement("entry");
            e.setAttribute("rating", ""+entry.rating);
            e.setAttribute("creation_time", entryPair.getKey());
            root.appendChild(e);

            for(String s : entry.whatWentWell)
            {
                Element el = doc.createElement("what_went_well");
                el.appendChild(doc.createTextNode(s));
                e.appendChild(el);
            }

            for(String s : entry.whatWentNotWell)
            {
                Element el = doc.createElement("what_went_not_well");
                el.appendChild(doc.createTextNode(s));
                e.appendChild(el);
            }

            for(String s : entry.whatIWillDo)
            {
                Element el = doc.createElement("what_i_will_do");
                el.appendChild(doc.createTextNode(s));
                e.appendChild(el);
            }
        }

        DOMSource source = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory.newInstance().newTransformer().transform(source, result);

        return writer.toString();
    }

    public void addEntry(HistoryEntry entry)
    {
        entries.put(new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()), entry);
    }
}
