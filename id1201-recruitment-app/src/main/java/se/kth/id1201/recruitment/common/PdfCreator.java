package se.kth.id1201.recruitment.common;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletResponse;
import se.kth.id1201.recruitment.model.components.Availability;

/**
 * Class for generating PDF files for recruiters wishing to
 * view them in PDF format
 */
public class PdfCreator {

    /**
     * Method called to shorten a date to a more readable format
     * @param d the date to shorten
     * @return  a shortened String in the form YYYY-MM-DD
     */
    public static String shortDate(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1; // months start at 0
        String m = month >= 10 ? "" + month : "0" + month + "";
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String day1 = day >= 10 ? "" + day : "0" + day;
        String date = year + "-" + m + "-" + day1;
        return date;
    }
    /**
     * Method to output a PDF file to the given HttpServletResponse,
     * printing out a users application information and translating
     * fields using the provided ResourceBundle
     * @param response  The response to write to
     * @param app       The application to print out
     * @param trans     The resource bundle containing translations
     * @throws DocumentException    on error
     * @throws IOException          if retrieving the output stream from the response fails
     */
    public static void print(HttpServletResponse response, PdfObject app, ResourceBundle trans) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        Font chapterFont = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLDITALIC);
        Font paragraphFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);
        
        DottedLineSeparator separator = new DottedLineSeparator();
        separator.setPercentage(59500f / 523f);
        Chunk linebreak = new Chunk(separator);
        
        UserDTO p = app.getPerson();
        Chunk chunk = new Chunk(trans.getString("application_status"), chapterFont);
        Chapter chap = new Chapter(new Paragraph(chunk), 1);
        chap.add(new Paragraph("" + app.getStatus(), paragraphFont));
        float width = chunk.getWidthPoint();
        chap.setIndentation(width/2);
        document.add(chap);
        
        document.add(linebreak);
        
        document.add(new Paragraph(trans.getString("personal_information")));
        PdfPTable personal = personalInfoTable(trans, p);
        document.add(personal);
        
        document.add(linebreak);

        document.add(new Paragraph(trans.getString("competence_profile")));
        PdfPTable competences = competenceTable(trans, app.getCompetences());
        document.add(competences);
        
        document.add(linebreak);

        document.add(new Paragraph(trans.getString("availability")));
        PdfPTable avail = availabilityTable(trans, app.getAvailabilities());
        document.add(avail);

        document.close();
    }
    /**
     * Generate a PdfPTable using a ResourceBundle to 
     * translate the given fields
     * @param trans The resource bundle containing all translations
     * @param avails   The PDFObject to retrieve information from
     * @return 
     */
    private static PdfPTable availabilityTable(ResourceBundle trans, List<Availability> avails) {
        PdfPTable avail = new PdfPTable(2);
        avail.setHorizontalAlignment(Element.ALIGN_LEFT);
        avail.addCell(trans.getString("availability_from"));
        avail.addCell(trans.getString("availability_to"));
        for (Availability a : avails) {
            avail.addCell(shortDate(a.getFromDate()));
            avail.addCell(shortDate(a.getToDate()));
        }
        avail.setSpacingBefore(10);
        avail.setSpacingAfter(32);
        return avail;
    }
    /**
     * Method to load a competence table with the given ResourceBundle
     * translations and the list of provided competences
     * @param trans the bundle where all translations are located
     * @param comps the list of competences
     * @return 
     */
    private static PdfPTable competenceTable(ResourceBundle trans, List<TranslatedCompetence> comps) {
        PdfPTable competences = new PdfPTable(2);
        competences.setHorizontalAlignment(Element.ALIGN_LEFT);
        competences.addCell(trans.getString("competence_desc"));
        competences.addCell(trans.getString("competence_exp"));
        for (TranslatedCompetence tc : comps) {
            competences.addCell(tc.getCompetence());
            competences.addCell("" + tc.getExperience());
        }
        competences.setSpacingBefore(10);
        competences.setSpacingAfter(32);
        return competences;
    }
    /**
     * Method to generate a table for a users personal information
     * @param trans the ResourceBundle containing translations for the given fields
     * @param p     the UserDTO object to retrieve information from
     * @return 
     */
    private static PdfPTable personalInfoTable(ResourceBundle trans, UserDTO p) {
        PdfPTable personal = new PdfPTable(1);
        personal.setHorizontalAlignment(Element.ALIGN_LEFT);
        personal.setLockedWidth(false);
        Chunk glue = new Chunk(new VerticalPositionMark());
        Phrase pr = new Phrase();
        pr.add(trans.getString("person_firstname"));
        pr.add(glue);
        pr.add(p.getFname());
        personal.addCell(pr);
        
        Phrase pr1 = new Phrase();
        pr1.add(trans.getString("person_lastname"));
        pr1.add(glue);
        pr1.add(p.getLname());
        personal.addCell(pr1);
        
        Phrase pr2 = new Phrase();
        pr2.add(trans.getString("person_email"));
        pr2.add(glue);
        pr2.add(p.getEmail());
        personal.addCell(pr2);
        
        Phrase pr3 = new Phrase();
        pr3.add(trans.getString("person_dob"));
        pr3.add(glue);
        pr3.add(shortDate(p.getDob()));
        personal.addCell(pr3);
        
        
        personal.setSpacingBefore(10);
        personal.setSpacingAfter(32);
        return personal;
    }
}
