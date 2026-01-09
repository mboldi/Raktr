package hu.bsstudio.raktr.pdf;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TabAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import hu.bsstudio.raktr.exception.PdfGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RentPdfService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy. MM. dd.");

    public byte[] generateRentPdf(RentPdfRequest request) {
        try (var outputStream = new ByteArrayOutputStream()) {
            generatePdfDocument(outputStream, request);
            log.info("Generated rent PDF for team [{}]", request.getTeamName());
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new PdfGenerationException("Failed to generate rent PDF", e);
        }
    }

    private void generatePdfDocument(ByteArrayOutputStream outputStream, RentPdfRequest request) throws IOException {
        var pdfDoc = new PdfDocument(new PdfWriter(outputStream));
        var document = new Document(pdfDoc, PageSize.A4);

        var pageWidth = pdfDoc.getDefaultPageSize().getWidth() - document.getLeftMargin() - document.getRightMargin();

        var regularFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN, PdfEncodings.CP1250);
        var boldFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD, PdfEncodings.CP1250);

        addTitle(document, boldFont, regularFont, pageWidth);
        addPartiesTable(document, regularFont, boldFont, pageWidth, request);
        addDates(document, regularFont, boldFont, pageWidth, request);
        addPermissionRequest(document, regularFont, boldFont, pageWidth);
        addFirstSignatures(document, regularFont, boldFont, pageWidth);
        addPermissionGrant(document, regularFont, boldFont, pageWidth, request);
        addItemsTable(document, regularFont, pageWidth, request.getItems());
        addOperatorSignature(document, regularFont, boldFont, pageWidth);

        document.close();
    }

    private void addTitle(Document document, PdfFont boldFont, PdfFont regularFont, float pageWidth) {
        var centerTabStop = createCenterTabStop(pageWidth);

        var title = new Paragraph()
                .addTabStops(centerTabStop)
                .setFont(boldFont)
                .setFontSize(20)
                .add(new Tab())
                .add("Szállítólevél\n")
                .setFixedLeading(2);
        document.add(title);

        var subTitle = new Paragraph()
                .addTabStops(centerTabStop)
                .setFont(regularFont)
                .setFontSize(10)
                .add(new Tab())
                .add("- Készült négy példányban -")
                .setMarginBottom(0);
        document.add(subTitle);

        var dormitoryName = new Paragraph()
                .addTabStops(centerTabStop)
                .setFont(boldFont)
                .setFontSize(16)
                .add(new Tab())
                .add("Schönherz Kollégium")
                .setMarginTop(12)
                .setMarginBottom(15);
        document.add(dormitoryName);
    }

    private void addPartiesTable(Document document, PdfFont regularFont, PdfFont boldFont, float pageWidth, RentPdfRequest request) {
        var table = new Table(2).useAllAvailableWidth();

        var transporterCell = new Cell()
                .add(new Paragraph().setFont(boldFont).setFontSize(14).add("Szállító:\n").setFixedLeading(30))
                .add(new Paragraph().setFont(regularFont).setFontSize(12)
                        .add("Kör neve: " + request.getTeamName() + "\n")
                        .add("Körvezető: " + request.getTeamLeaderName())
                        .setFixedLeading(30))
                .setWidth(pageWidth / 2);

        var liablePartyCell = new Cell()
                .add(new Paragraph().setFont(boldFont).setFontSize(14).add("Felelősségvállaló:\n").setFixedLeading(30))
                .add(new Paragraph().setFont(regularFont).setFontSize(12)
                        .add("Személy: " + request.getRenterName() + " \n")
                        .add("Szig. szám: " + request.getRenterId())
                        .setFixedLeading(30));

        table.addCell(transporterCell).addCell(liablePartyCell);
        document.add(table);
    }

    private void addDates(Document document, PdfFont regularFont, PdfFont boldFont, float pageWidth, RentPdfRequest request) {
        var dateTabs = new ArrayList<TabStop>();
        dateTabs.add(new TabStop(pageWidth / 4 + 10, TabAlignment.LEFT));

        var deliveryDateLine = new Paragraph()
                .addTabStops(dateTabs)
                .setFont(boldFont)
                .setFontSize(12)
                .add("Kiszállítás időpontja:")
                .add(new Tab())
                .add(new Paragraph().setFont(regularFont).setFontSize(12)
                        .add(formatDate(request.getDeliveryDate()))
                        .add(" (Év, Hónap, Nap)"));

        var returnDateLine = new Paragraph()
                .addTabStops(dateTabs)
                .setFont(boldFont)
                .setFontSize(12)
                .add("Visszaszállítás időpontja:")
                .add(new Tab())
                .add(new Paragraph().setFont(regularFont).setFontSize(12)
                        .add(formatDate(request.getReturnDate()))
                        .add(" (Év, Hónap, Nap)"))
                .setMultipliedLeading(0.1f);

        document.add(deliveryDateLine).add(returnDateLine);
    }

    private void addPermissionRequest(Document document, PdfFont regularFont, PdfFont boldFont, float pageWidth) {
        var dormitoryNameBold = new Paragraph().setFont(boldFont).setFontSize(12).add("Schönherz Kollégiumból ");

        var permissionText = new Paragraph()
                .setFont(regularFont)
                .setFontSize(12)
                .add("A szállítón szereplő eszközöknek a fent jelzett időpontban a ")
                .add(dormitoryNameBold)
                .add("történő szállítására engedélyt kérek.\n")
                .setMultipliedLeading(1.0f)
                .setMarginTop(10);
        document.add(permissionText);

        var today = new Paragraph()
                .setFont(regularFont)
                .setFontSize(12)
                .add("Kelt: " + formatDate(LocalDate.now()))
                .setMultipliedLeading(2);
        document.add(today);
    }

    private void addFirstSignatures(Document document, PdfFont regularFont, PdfFont boldFont, float pageWidth) {
        var signUnderlineTabs = createSignUnderlineTabs(pageWidth);
        var signNamesTabs = createSignNamesTabs(pageWidth);

        var signsLine = new Paragraph()
                .setFont(regularFont)
                .setFontSize(12)
                .addTabStops(signUnderlineTabs)
                .add(new Tab()).add(new Tab()).add(new Tab()).add(new Tab())
                .setFixedLeading(30);
        document.add(signsLine);

        var firstSignsNames = new Paragraph()
                .addTabStops(signNamesTabs)
                .setFontSize(12)
                .setFont(boldFont)
                .add(new Tab())
                .add("Körvezető")
                .add(new Tab())
                .add("Felelősségvállaló")
                .setFixedLeading(35);
        document.add(firstSignsNames);
    }

    private void addPermissionGrant(Document document, PdfFont regularFont, PdfFont boldFont, float pageWidth, RentPdfRequest request) {
        var dormitoryNameBold = new Paragraph().setFont(boldFont).setFontSize(12).add("Schönherz Kollégiumból ");

        var permissionGiveText = new Paragraph()
                .setFont(regularFont)
                .setFontSize(12)
                .add("A szállítón szereplő eszközöknek a fent jelzett időpontban a ")
                .add(dormitoryNameBold)
                .add("történő szállítását engedélyezem.\n")
                .setMultipliedLeading(1.0f)
                .setMarginTop(10);
        document.add(permissionGiveText);

        var signUnderlineTabs = createSignUnderlineTabs(pageWidth);
        var signNamesTabs = createSignNamesTabs(pageWidth);

        var signsLine = new Paragraph()
                .setFont(regularFont)
                .setFontSize(12)
                .addTabStops(signUnderlineTabs)
                .add(new Tab()).add(new Tab()).add(new Tab()).add(new Tab())
                .setFixedLeading(30);
        document.add(signsLine);

        var secondSignsNames = new Paragraph()
                .addTabStops(signNamesTabs)
                .setFontSize(12)
                .setFont(boldFont)
                .add(new Tab())
                .add(request.getFirstSignerName())
                .add(new Tab())
                .add(request.getSecondSignerName())
                .setFixedLeading(35);

        var secondSignsTitles = new Paragraph()
                .addTabStops(signNamesTabs)
                .setFontSize(12)
                .setFont(boldFont)
                .add(new Tab())
                .add(request.getFirstSignerTitle())
                .add(new Tab())
                .add(request.getSecondSignerTitle())
                .setFixedLeading(-20);

        document.add(secondSignsNames);
        document.add(secondSignsTitles);
    }

    private void addItemsTable(Document document, PdfFont regularFont, float pageWidth, Map<String, Integer> items) {
        var itemsTable = new Table(new float[]{pageWidth / 12, pageWidth * 5 / 12, pageWidth / 12, pageWidth * 5 / 12})
                .useAllAvailableWidth()
                .setMarginTop(30);

        for (int i = 0; i < 2; i++) {
            itemsTable.addCell(new Cell().setTextAlignment(TextAlignment.CENTER)
                    .add(new Paragraph().setFont(regularFont).setFontSize(12).add("db")));
            itemsTable.addCell(new Cell().setTextAlignment(TextAlignment.CENTER)
                    .add(new Paragraph().setFont(regularFont).setFontSize(12).add("megnevezés")));
        }

        if (items != null) {
            for (var item : items.entrySet()) {
                itemsTable.addCell(new Cell().add(new Paragraph().setFont(regularFont).setFontSize(12).add(item.getValue().toString())));
                itemsTable.addCell(new Cell().add(new Paragraph().setFont(regularFont).setFontSize(12).add(item.getKey())));
            }
        }

        document.add(itemsTable);
    }

    private void addOperatorSignature(Document document, PdfFont regularFont, PdfFont boldFont, float pageWidth) {
        var signUnderlineTabs = createSignUnderlineTabs(pageWidth);
        var signNamesTabs = createSignNamesTabs(pageWidth);

        var seenBy = new Paragraph()
                .addTabStops(signUnderlineTabs)
                .setFont(regularFont)
                .setFontSize(12)
                .setMarginLeft(pageWidth / 2)
                .setFixedLeading(40)
                .add("Látta:")
                .add(new Tab())
                .add(new Tab());
        document.add(seenBy);

        var seenByTitle = new Paragraph()
                .addTabStops(signNamesTabs)
                .setFont(boldFont)
                .setFontSize(12)
                .setMarginLeft(pageWidth / 2)
                .add(new Tab())
                .add("Üzemeltető")
                .setFixedLeading(-20);
        document.add(seenByTitle);
    }

    private List<TabStop> createCenterTabStop(float pageWidth) {
        var tabStops = new ArrayList<TabStop>();
        tabStops.add(new TabStop(pageWidth / 2, TabAlignment.CENTER));
        return tabStops;
    }

    private List<TabStop> createSignUnderlineTabs(float pageWidth) {
        var line = new DottedLine();
        line.setGap(2.0f);

        var tabStops = new ArrayList<TabStop>();
        tabStops.add(new TabStop(pageWidth / 12));
        tabStops.add(new TabStop(pageWidth / 12 + pageWidth / 3, TabAlignment.LEFT, line));
        tabStops.add(new TabStop(pageWidth / 12 + pageWidth / 3 + pageWidth / 6));
        tabStops.add(new TabStop(pageWidth - pageWidth / 12, TabAlignment.LEFT, line));
        return tabStops;
    }

    private List<TabStop> createSignNamesTabs(float pageWidth) {
        var tabStops = new ArrayList<TabStop>();
        tabStops.add(new TabStop(pageWidth / 12 + pageWidth / 6, TabAlignment.CENTER));
        tabStops.add(new TabStop(pageWidth - (pageWidth / 12 + pageWidth / 6), TabAlignment.CENTER));
        return tabStops;
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

}
