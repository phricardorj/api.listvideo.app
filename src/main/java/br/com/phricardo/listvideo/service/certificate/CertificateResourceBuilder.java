package br.com.phricardo.listvideo.service.certificate;

import br.com.phricardo.listvideo.dto.request.CertificateDataRequestDTO;
import br.com.phricardo.listvideo.service.UserAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class CertificateResourceBuilder {

    private final UserAuthenticationService userAuthenticationService;
    private static final PDType1Font TITLE_FONT = PDType1Font.HELVETICA_BOLD;
    private static final PDType1Font STUDENT_NAME_FONT = PDType1Font.HELVETICA_BOLD;
    private static final PDType1Font TEXT_FONT = PDType1Font.HELVETICA;
    private static final PDType1Font BOLD_FONT = PDType1Font.HELVETICA_BOLD;
    private static final Color RED_COLOR = Color.RED;
    private static final Color BLACK_COLOR = Color.BLACK;
    private static final String FILE_NAME = "certificate";
    private static final String BACKGROUND_IMAGE_PATH = "/templates/certificate_background.jpg";

    public ResponseEntity<Resource> buildCertificateResource(CertificateDataRequestDTO certificateDataRequestDTO) {
        final var user = userAuthenticationService.getCurrentUser();
        final var dateTime =  LocalDate.now(java.time.ZoneId.of("America/Sao_Paulo"))
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
            document.addPage(page);

            BufferedImage backgroundImage = ImageIO.read(requireNonNull(getClass().getResourceAsStream(BACKGROUND_IMAGE_PATH)));
            PDImageXObject background = JPEGFactory.createFromImage(document, backgroundImage);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(background, 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());

                float centerY = page.getMediaBox().getHeight() / 2;

                writeText(contentStream, "Certificado de Conclusão", TITLE_FONT, RED_COLOR, 32, 50, centerY + 150);
                writeText(contentStream, "Certificamos que", TEXT_FONT, BLACK_COLOR, 30, 50, centerY + 60);
                writeText(contentStream, user.getName(), STUDENT_NAME_FONT, RED_COLOR, 26, 50, centerY + 20);
                writeText(contentStream, "concluiu o conteúdo da playlist do YouTube", TEXT_FONT, BLACK_COLOR, 18, 50, centerY - 30);
                writeText(contentStream, "com ID: " + certificateDataRequestDTO.getCourseId() + " com carga horária", TEXT_FONT, BLACK_COLOR, 18, 50, centerY - 55);
                writeText(contentStream, "de "+ certificateDataRequestDTO.getDuration() +" em " + dateTime + ".", TEXT_FONT, BLACK_COLOR, 18, 50, centerY - 80);
            }

            final var byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            document.close();

            final var inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            final var resource = new ByteArrayResource(IOUtils.toByteArray(inputStream));

            return ResponseEntity.ok()
                    .headers(createHeader())
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (IOException e) {
            throw new RuntimeException("There was an error generating PDF of the certificate", e);
        }
    }

    private void writeText(PDPageContentStream contentStream, String text, PDType1Font font, Color color, int fontSize, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.setNonStrokingColor(color);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    private HttpHeaders createHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", FILE_NAME + ".pdf");
        return headers;
    }
}
