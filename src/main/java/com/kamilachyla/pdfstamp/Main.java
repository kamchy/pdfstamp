package com.kamilachyla.pdfstamp;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Main {
    public static final String OUTPUT = "output.pdf";
    public static final String INPUT = "input.pdf";
    record Position(String img, int page, int left, int bottom){}

    static void generate(String optDateStr) throws Exception {
        final var inputStream = Objects.requireNonNull(Main.class.getResourceAsStream("/" + INPUT));
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputStream), new PdfWriter(OUTPUT));
        Document doc = new Document(pdfDoc);
        var dateLeft = 200;
        var dateBottom = 580;
        var letterWidth = 15;
        var dateStr = Optional.ofNullable(optDateStr).orElse(
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        var positions = new ArrayList<Position>();
        positions.addAll(List.of(
                new Position("/signature.png", 2, 350, 580),
                new Position("/kid_name.png", 1, 50, 245)));
        for (int i = 0; i < dateStr.length(); i++) {
            final var charAt = dateStr.charAt(i);
            var f = "/%c.png".formatted(charAt == '.' ? 'd' : charAt);
            positions.add(new Position(f, 2, dateLeft + i * letterWidth, dateBottom));
        }

        for (Position p : positions) {
            Image image = new Image(ImageDataFactory.create(Objects.requireNonNull(Main.class.getResource(p.img))));
            image.setFixedPosition(p.page, p.left, p.bottom);
            doc.add(image);
        }

        doc.close();
    }

    public static void main(String[] args) {
        try {
            Main.generate(args[0]);
            System.out.printf(new File(OUTPUT).getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}