package hu.bsstudio.raktr.pdf;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RentPdfServiceTest {

    private final RentPdfService rentPdfService = new RentPdfService();

    @Test
    @SneakyThrows
    void testGenerateRentPdf() {
        var items = new LinkedHashMap<String, Integer>();
        items.put("Sony A7III kamera", 2);
        items.put("Canon EF 50mm f/1.4 objektív", 3);
        items.put("Manfrotto állvány", 1);
        items.put("Rode VideoMic Pro mikrofon", 2);
        items.put("LED panel", 4);
        items.put("HDMI kábel 5m", 6);

        var request = RentPdfRequest.builder()
                .teamName("Budavári Schönherz Stúdió")
                .teamLeaderName("Kovács János")
                .renterName("Nagy Péter")
                .renterId("123456AB")
                .firstSignerName("Kiss István")
                .firstSignerTitle("Üzemeltetési Felelős")
                .secondSignerName("Szabó Anna")
                .secondSignerTitle("BME - Hallgatói és Kollégiumi Erőforrások Osztálya")
                .deliveryDate(LocalDate.now())
                .returnDate(LocalDate.now().plusDays(3))
                .items(items)
                .build();

        byte[] pdfBytes = rentPdfService.generateRentPdf(request);

        assertTrue(pdfBytes.length > 0, "PDF should not be empty");
        assertTrue(pdfBytes[0] == '%' && pdfBytes[1] == 'P' && pdfBytes[2] == 'D' && pdfBytes[3] == 'F',
                "Output should be a valid PDF (starts with %PDF)");

        var outputPath = Path.of("build", "test-output", "rent-pdf-test.pdf");
        Files.createDirectories(outputPath.getParent());
        Files.write(outputPath, pdfBytes);

        System.out.println("PDF generated at: " + outputPath.toAbsolutePath());
    }

}
