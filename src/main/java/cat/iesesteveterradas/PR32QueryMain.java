package cat.iesesteveterradas;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.HELVETICA_BOLD;

public class PR32QueryMain {
    public static void main(String[] args) {
        // Conectarse a MongoDB
        MongoClient mongoClient = MongoClients.create("mongodb://root:example@localhost:27017/");
        MongoDatabase database = mongoClient.getDatabase("PR32");
        MongoCollection<Document> collection = database.getCollection("EX1");

        // Consulta 1: Obtener las preguntas con ViewCount mayor que la media
        FindIterable<Document> query1Result = collection
                .find(new Document("ViewCount", new Document("$gt", obtenerMediaViewCount(collection))));
        generarInformePDF("Informe1.pdf", query1Result);

        // Consulta 2: Obtener las preguntas con títulos que contienen ciertas letras
        FindIterable<Document> query2Result = collection
                .find(new Document("Title", new Document("$regex", "ep|ki|an|ch|po|co|zap|gag|oaf|elf")));
        generarInformePDF("Informe2.pdf", query2Result);

        // Cerrar la conexión a MongoDB
        mongoClient.close();
    }

    private static int obtenerMediaViewCount(MongoCollection<Document> collection) {
        int totalViewCount = 0;
        int totalCount = 0;
        FindIterable<Document> allDocuments = collection.find();
        for (Document doc : allDocuments) {
            totalViewCount += doc.getInteger("ViewCount");
            totalCount++;
        }
        return totalViewCount / totalCount;
    }

    private static void generarInformePDF(String nombreArchivo, FindIterable<Document> resultadosConsulta) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                PDType1Font font = new PDType1Font(HELVETICA_BOLD);
                contentStream.setFont(font, 12);
                int y = 700; // Posición inicial en la página
                for (Document doc : resultadosConsulta) {
                    String titulo = doc.getString("Title");
                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, y);
                    contentStream.showText(titulo);
                    contentStream.endText();
                    y -= 20; // Espacio entre cada título
                }
            }

            document.save("data\\out" + nombreArchivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
