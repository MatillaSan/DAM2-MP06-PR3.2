package cat.iesesteveterradas;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import org.apache.commons.text.StringEscapeUtils;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class PR32CreateMain {
    public static void main(String[] args) throws IOException {
        // Connectar-se a MongoDB (substitueix amb la teva URI de connexió)
        try (var mongoClient = MongoClients.create("mongodb://root:example@localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("PR32");
            MongoCollection<Document> collection = database.getCollection("EX1");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document = builder.parse(new File("data\\query1.xml"));
            NodeList nodeList = document.getElementsByTagName("post");

            // Itera sobre los nodos <post>
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    // Obtener el elemento <post>
                    Element postElement = (Element) node;

                    // Extraer datos del XML
                    String id = postElement.getElementsByTagName("Id").item(0).getTextContent();
                    String postTypeId = postElement.getElementsByTagName("PostTypeId").item(0).getTextContent();
                    String acceptedAnswerId = postElement.getElementsByTagName("AcceptedAnswerId").item(0)
                            .getTextContent();
                    String creationDate = postElement.getElementsByTagName("CreationDate").item(0).getTextContent();
                    String score = postElement.getElementsByTagName("Score").item(0).getTextContent();
                    String viewCount = postElement.getElementsByTagName("ViewCount").item(0).getTextContent();
                    String body = postElement.getElementsByTagName("Body").item(0).getTextContent();
                    String ownerUserId = postElement.getElementsByTagName("OwnerUserId").item(0).getTextContent();
                    String lastActivityDate = postElement.getElementsByTagName("LastActivityDate").item(0)
                            .getTextContent();
                    String title = postElement.getElementsByTagName("Title").item(0).getTextContent();
                    String tags = postElement.getElementsByTagName("Tags").item(0).getTextContent();
                    String answerCount = postElement.getElementsByTagName("AnswerCount").item(0).getTextContent();
                    String commentCount = postElement.getElementsByTagName("CommentCount").item(0).getTextContent();
                    String contentLicense = postElement.getElementsByTagName("ContentLicense").item(0)
                            .getTextContent();

                    body = StringEscapeUtils.unescapeHtml4(body);

                    // Crear un nou document
                    Document question = new Document("Title", title)
                            .append("Id", id)
                            .append("PostTypeId", postTypeId)
                            .append("AcceptedAnswerId", acceptedAnswerId)
                            .append("CreationDate", creationDate)
                            .append("Score", score)
                            .append("ViewCount", Integer.parseInt(viewCount))
                            .append("Body", body)
                            .append("OwnerUserId", ownerUserId)
                            .append("LastActivityDate", lastActivityDate)
                            .append("Tags", tags)
                            .append("AnswerCount", answerCount)
                            .append("CommentCount", commentCount)
                            .append("ContentLicense", contentLicense);

                    // Inserir el document a la col·lecció
                    collection.insertOne(question);

                    System.out.println("Document inserted successfully");
                }
            }

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }

    }
}
