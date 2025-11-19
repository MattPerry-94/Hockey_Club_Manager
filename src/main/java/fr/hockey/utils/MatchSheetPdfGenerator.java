package fr.hockey.utils;

import fr.hockey.models.Player;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MatchSheetPdfGenerator {

    public static void generate(String category,
                                List<Player> players,
                                File outFile,
                                LocalDate matchDate,
                                String opponent,
                                File logoFile) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            Writer w = new Writer(doc, 50f, 18f);

            // Logo en haut à droite (optionnel)
            w.drawLogo(logoFile);

            // Titre
            w.setFontBold(18);
            w.writeLine("Feuille de match — Catégorie " + safe(category));

            // Ligne d’infos: Date + Adversaire
            LocalDate dateToUse = matchDate != null ? matchDate : LocalDate.now();
            String dateStr = dateToUse.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String opp = safe(opponent);
            String info = "Date: " + dateStr + (opp.isEmpty() ? "" : "    Adversaire: " + opp);
            w.setFontRegular(12);
            w.writeLine(info);
            w.newParagraph();

            // Sections
            w.writeSection("Gardiens", filterBy(players, "GARDIEN"));
            w.writeSection("Défenseurs", filterBy(players, "DEFENSEUR"));
            w.writeSection("Attaquants", filterBy(players, "ATTAQUANT"));

            w.close();
            doc.save(outFile);
        }
    }

    private static class Writer {
        private final PDDocument doc;
        private PDPage page;
        private PDPageContentStream cs;
        private final float margin;
        private final float leading;
        private final float x;
        private float y;
        private float currentFontSize = 12f;
        private boolean bold = false;

        Writer(PDDocument doc, float margin, float leading) throws IOException {
            this.doc = doc;
            this.margin = margin;
            this.leading = leading;
            this.x = margin;
            newPage();
        }

        void newPage() throws IOException {
            if (cs != null) cs.close();
            page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            cs = new PDPageContentStream(doc, page);
            y = page.getMediaBox().getHeight() - margin;
        }

        void ensureSpace() throws IOException {
            if (y < margin + 40f) {
                newPage();
            }
        }

        void setFontBold(float size) { this.bold = true; this.currentFontSize = size; }
        void setFontRegular(float size) { this.bold = false; this.currentFontSize = size; }

        void writeLine(String text) throws IOException {
            ensureSpace();
            cs.beginText();
            cs.setFont(bold ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, currentFontSize);
            cs.newLineAtOffset(x, y);
            cs.showText(text);
            cs.endText();
            y -= leading;
        }

        void newParagraph() { y -= leading * 0.5f; }

        void drawLogo(File logoFile) throws IOException {
            if (logoFile == null || !logoFile.exists()) return;
            try {
                PDImageXObject image = PDImageXObject.createFromFileByExtension(logoFile, doc);
                float pageWidth = page.getMediaBox().getWidth();
                float pageHeight = page.getMediaBox().getHeight();
                float targetHeight = 60f; // hauteur souhaitée
                float aspect = (float) image.getWidth() / (float) image.getHeight();
                float targetWidth = targetHeight * aspect;
                float xRight = pageWidth - margin - targetWidth;
                float yTop = pageHeight - margin - targetHeight;
                cs.drawImage(image, xRight, yTop, targetWidth, targetHeight);
            } catch (Exception ignore) {
                // Si l'image ne peut pas être lue, on ignore silencieusement
            }
        }

        void writeSection(String title, List<Player> list) throws IOException {
            setFontBold(14);
            writeLine(title);
            setFontRegular(12);
            if (list == null || list.isEmpty()) {
                writeLine("Aucun joueur");
                return;
            }
            for (Player p : list) {
                String roleSuffix = "";
                String role = safe(p.getRole());
                if ("CAPITAINE".equalsIgnoreCase(role)) roleSuffix = " (Capitaine)";
                else if ("ASSISTANT".equalsIgnoreCase(role)) roleSuffix = " (Assistant)";
                String num = p.getNumber() > 0 ? "N°" + p.getNumber() + " " : "";
                String line = num + safe(p.getLastName()) + " " + safe(p.getFirstName()) + roleSuffix;
                writeLine(line);
            }
            newParagraph();
        }

        void close() throws IOException { if (cs != null) cs.close(); }
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }

    private static List<Player> filterBy(List<Player> players, String position) {
        return players.stream()
                .filter(p -> position.equalsIgnoreCase(safe(p.getPosition())))
                .collect(Collectors.toList());
    }
}