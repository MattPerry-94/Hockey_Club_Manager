package fr.hockey.utils;

import fr.hockey.models.Player;
import org.junit.jupiter.api.Test;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MatchSheetPdfGeneratorTest {

    @Test
    void testGenerateMatchSheetPdf() throws Exception {
        List<Player> players = new ArrayList<>();

        Player g = new Player();
        g.setFirstName("Jean");
        g.setLastName("Dupont");
        g.setPosition("GARDIEN");
        g.setRole("JOUEUR");
        g.setNumber(1);
        players.add(g);

        Player d1 = new Player();
        d1.setFirstName("Marc");
        d1.setLastName("Durand");
        d1.setPosition("DEFENSEUR");
        d1.setRole("JOUEUR");
        d1.setNumber(4);
        players.add(d1);

        Player d2 = new Player();
        d2.setFirstName("Paul");
        d2.setLastName("Martin");
        d2.setPosition("DEFENSEUR");
        d2.setRole("ASSISTANT");
        d2.setNumber(7);
        players.add(d2);

        Player a1 = new Player();
        a1.setFirstName("Luc");
        a1.setLastName("Bernard");
        a1.setPosition("ATTAQUANT");
        a1.setRole("CAPITAINE");
        a1.setNumber(9);
        players.add(a1);

        Player a2 = new Player();
        a2.setFirstName("Nicolas");
        a2.setLastName("Leroy");
        a2.setPosition("ATTAQUANT");
        a2.setRole("JOUEUR");
        a2.setNumber(12);
        players.add(a2);

        Player a3 = new Player();
        a3.setFirstName("Alex");
        a3.setLastName("Moreau");
        a3.setPosition("ATTAQUANT");
        a3.setRole("JOUEUR");
        a3.setNumber(15);
        players.add(a3);

        File out = Files.createTempFile("feuille_match_", ".pdf").toFile();

        MatchSheetPdfGenerator.generate("U13", players, out, LocalDate.now(), "Lions", null);

        boolean exists = out.exists();
        long size = Files.size(out.toPath());
        assertTrue(exists);
        assertTrue(size > 0);
        System.out.println("MATCH_PDF_PATH=" + out.getAbsolutePath());
        System.out.println("MATCH_PDF_EXISTS=" + exists);
        System.out.println("MATCH_PDF_SIZE=" + size);

        int pages;
        String text;
        try (PDDocument doc = PDDocument.load(out)) {
            pages = doc.getNumberOfPages();
            PDFTextStripper stripper = new PDFTextStripper();
            text = stripper.getText(doc);
        }

        System.out.println("MATCH_PDF_PAGES=" + pages);
        boolean hasTitle = text.contains("Feuille de match") && text.contains("Catégorie U13");
        System.out.println("MATCH_PDF_TEXT_CONTAINS_TITLE=" + hasTitle);
        assertTrue(pages >= 1);
        assertTrue(hasTitle);

        System.out.println("TEST_RESULT=PASS");
    }
}

