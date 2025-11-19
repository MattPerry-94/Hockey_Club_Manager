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

/**
 * Générateur PDF de feuilles de match utilisant Apache PDFBox.
 *
 * <p>Le PDF inclut automatiquement :</p>
 * <ul>
 *     <li>le logo du club (si disponible),</li>
 *     <li>la date du match,</li>
 *     <li>le nom de l’adversaire,</li>
 *     <li>la liste des joueurs triés par poste (gardien, défenseur, attaquant).</li>
 * </ul>
 *
 * <p>L'affichage est paginé automatiquement pour éviter les dépassements.</p>
 */
public class MatchSheetPdfGenerator {

    /**
     * Génère la feuille de match au format PDF.
     *
     * @param category  catégorie (U9, U11, etc.)
     * @param players   liste des joueurs de la catégorie
     * @param outFile   fichier PDF de sortie
     * @param matchDate date du match
     * @param opponent  nom de l’adversaire
     * @param logoFile  fichier image du logo du club (optionnel)
     *
     * @throws IOException en cas d’erreur de création du PDF
     */
    public static void generate(String category,
                                List<Player> players,
                                File outFile,
                                LocalDate matchDate,
                                String opponent,
                                File logoFile) throws IOException {

        try (PDDocument doc = new PDDocument()) {

            Writer w = new Writer(doc, 50f, 18f);

            // Logo
            w.drawLogo(logoFile);

            // Titre
            w.setFontBold(18);
            w.writeLine("Feuille de match — Catégorie " + safe(category));

            // Ligne : date + adversaire
            LocalDate dateToUse = matchDate != null ? matchDate : LocalDate.now();
            String dateStr = dateToUse.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String opp = safe(opponent);
            String info = "Date: " + dateStr + (opp.isEmpty() ? "" : "    Adversaire: " + opp);

            w.setFontRegular(12);
            w.writeLine(info);
            w.newParagraph();

            // Sections de joueurs
            w.writeSection("Gardiens", filterBy(players, "GARDIEN"));
            w.writeSection("Défenseurs", filterBy(players, "DEFENSEUR"));
            w.writeSection("Attaquants", filterBy(players, "ATTAQUANT"));

            w.close();
            doc.save(outFile);
        }
    }

    /**
     * Classe interne responsable de l’écriture PDF (texte, pagination, logo, sections).
     */
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

        /**
         * Constructeur du writer.
         *
         * @param doc     document PDF en cours
         * @param margin  marge du document
         * @param leading interlignage vertical
         */
        Writer(PDDocument doc, float margin, float leading) throws IOException {
            this.doc = doc;
            this.margin = margin;
            this.leading = leading;
            this.x = margin;
            newPage();
        }

        /**
         * Crée une nouvelle page dans le PDF et réinitialise les coordonnées d’écriture.
         */
        void newPage() throws IOException {
            if (cs != null) cs.close();
            page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            cs = new PDPageContentStream(doc, page);
            y = page.getMediaBox().getHeight() - margin;
        }

        /**
         * Vérifie s'il reste assez de place sur la page, sinon crée une nouvelle page.
         */
        void ensureSpace() throws IOException {
            if (y < margin + 40f) {
                newPage();
            }
        }

        /** Définit la police en gras. */
        void setFontBold(float size) { this.bold = true; this.currentFontSize = size; }

        /** Définit la police normale. */
        void setFontRegular(float size) { this.bold = false; this.currentFontSize = size; }

        /**
         * Écrit une ligne de texte, avec pagination automatique.
         *
         * @param text texte à écrire
         */
        void writeLine(String text) throws IOException {
            ensureSpace();
            cs.beginText();
            cs.setFont(bold ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, currentFontSize);
            cs.newLineAtOffset(x, y);
            cs.showText(text);
            cs.endText();
            y -= leading;
        }

        /** Ajoute un léger espacement vertical. */
        void newParagraph() { y -= leading * 0.5f; }

        /**
         * Dessine le logo en haut à droite si disponible.
         *
         * @param logoFile fichier image du logo
         */
        void drawLogo(File logoFile) throws IOException {
            if (logoFile == null || !logoFile.exists()) return;
            try {
                PDImageXObject image = PDImageXObject.createFromFileByExtension(logoFile, doc);
                float pageWidth = page.getMediaBox().getWidth();
                float pageHeight = page.getMediaBox().getHeight();

                float targetHeight = 60f;
                float aspect = (float) image.getWidth() / (float) image.getHeight();
                float targetWidth = targetHeight * aspect;

                float xRight = pageWidth - margin - targetWidth;
                float yTop = pageHeight - margin - targetHeight;

                cs.drawImage(image, xRight, yTop, targetWidth, targetHeight);
            } catch (Exception ignore) {
                // On ignore silencieusement si le logo est illisible
            }
        }

        /**
         * Écrit une section (ex: Gardiens, Défenseurs, Attaquants).
         *
         * @param title titre de la section
         * @param list  joueurs correspondants
         */
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

        /** Ferme le flux d’écriture PDF. */
        void close() throws IOException { if (cs != null) cs.close(); }
    }

    /**
     * Sécurise une chaîne en évitant les nulls.
     *
     * @param s chaîne potentiellement null
     * @return chaîne non-null trimée
     */
    private static String safe(String s) { return s == null ? "" : s.trim(); }

    /**
     * Filtre une liste de joueurs selon leur poste.
     *
     * @param players  tous les joueurs
     * @param position poste recherché
     * @return joueurs correspondant au poste
     */
    private static List<Player> filterBy(List<Player> players, String position) {
        return players.stream()
                .filter(p -> position.equalsIgnoreCase(safe(p.getPosition())))
                .collect(Collectors.toList());
    }
}
