package me.sensingself.sensingsugar.Common.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.util.ArrayList;

import me.sensingself.sensingsugar.Common.Interfaces.PDFListener;
import me.sensingself.sensingsugar.Model.Patient;
import me.sensingself.sensingsugar.Model.TestingResult;
import me.sensingself.sensingsugar.Model.UserVo;
import me.sensingself.sensingsugar.R;

/**
 * Created by liujie on 3/2/18.
 */

public class PDFUtils {
    @SuppressLint("StaticFieldLeak")
    public static void createPDFFromTestingResults(final Context context, final ArrayList<TestingResult> testingResults, final String pdfFilename, final UserVo userVo, final Patient patient, final PDFListener listener){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    return getPdfFileFromTestingResults(context, testingResults, pdfFilename, userVo, patient);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null && s.trim().length() > 0) {
                    listener.createPDF(s);
                } else {
                    listener.createPDF(null);
                }
            }
        }.execute();


    }
    private static String getPdfFileFromTestingResults(Context context, ArrayList<TestingResult> testingResults, String pdfFilename, UserVo userVo, Patient patient){
        Document doc = new Document();
        PdfWriter docWriter = null;

        try {
            //special font sizes
            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
            Font bf12 = new Font(Font.FontFamily.TIMES_ROMAN, 12);
            //file path
            docWriter = PdfWriter.getInstance(doc , new FileOutputStream(pdfFilename));

            //document header attributes
            doc.addAuthor(context.getResources().getString(R.string.app_name));
            doc.addCreationDate();
            doc.addProducer();
            doc.addCreator(userVo.getFirstName() + " " + userVo.getLastName());
            doc.addTitle("Report Patients");
            doc.setPageSize(PageSize.LETTER);

            //open document
            doc.open();

            //create a paragraph
            Paragraph paragraph = new Paragraph();


            //specify column widths
            float[] columnWidths = {3f, 4f, 1.5f, 1.5f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(90f);

            //insert column headings
            insertCell(table, "TestingDate", Element.ALIGN_CENTER,1);
            insertCell(table, "BloodSugarType", Element.ALIGN_CENTER,1);
            insertCell(table, "mg/dl", Element.ALIGN_CENTER,1);
            insertCell(table, "mmol", Element.ALIGN_CENTER,1);
            table.setHeaderRows(1);

            //insert an empty row
            insertCell(table, "", Element.ALIGN_CENTER, 4);
            //create section heading by cell merging

            insertCell(table, patient.getFirstName() + " " + patient.getLastName() + "(" + patient.getBirthday() + ")",Element.ALIGN_LEFT,  4);
            double orderTotal, total = 0;

            //just some random data to fill
            for(TestingResult testingResult : testingResults){
                long timestamp = testingResult.getCurrentDate();
                String timer = GmtUtil.gmt0ToLocal(DateUtils.long2MinuteString(timestamp));

                insertCell(table, timer, Element.ALIGN_CENTER,1);
                insertCell(table, getBloodType(testingResult.getBloodSugarType(), context),Element.ALIGN_CENTER, 1);
                insertCell(table, Long.toString(testingResult.getMgdl()),Element.ALIGN_CENTER, 1);
                insertCell(table, Float.toString(testingResult.getMmol()),Element.ALIGN_CENTER, 1);
            }

            //add the PDF table to the paragraph
            paragraph.add(table);
            // add the paragraph to the document
            doc.add(paragraph);

        }
        catch (DocumentException dex)
        {
            dex.printStackTrace();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (doc != null){
                //close the document
                doc.close();
            }
            if (docWriter != null){
                //close the writer
                docWriter.close();
            }
            return pdfFilename;
        }
    }

    private static String getPdfFileFromPatients(Context context, ArrayList<Patient> patients, String pdfFilename, UserVo userVo){

        Document doc = new Document();
        PdfWriter docWriter = null;

        try {
            //file path
            docWriter = PdfWriter.getInstance(doc , new FileOutputStream(pdfFilename));

            //document header attributes
            doc.addAuthor(context.getResources().getString(R.string.app_name));
            doc.addCreationDate();
            doc.addProducer();
            doc.addCreator(userVo.getFirstName() + " " + userVo.getLastName());
            doc.addTitle("Report Patients");
            doc.setPageSize(PageSize.LETTER);

            //open document
            doc.open();

            //create a paragraph
            Paragraph paragraph = new Paragraph();


            //specify column widths
            float[] columnWidths = {3f, 1.5f, 1.5f, 1f, 2f, 2f, 2f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(90f);

            //insert column headings
            insertCell(table, "PatientName", Element.ALIGN_CENTER,1);
            insertCell(table, "Height",Element.ALIGN_CENTER, 1);
            insertCell(table, "Weight",Element.ALIGN_CENTER, 1);
            insertCell(table, "Gender", Element.ALIGN_CENTER,1);
            insertCell(table, "Date of Birth", Element.ALIGN_CENTER,1);
            insertCell(table, "Aadhaar",Element.ALIGN_CENTER, 1);
            insertCell(table, "Mobile",Element.ALIGN_CENTER, 1);
            table.setHeaderRows(1);

            for (Patient patient : patients){
                //create section heading by cell merging
                insertCell(table, patient.getFirstName() + " " + patient.getLastName(), Element.ALIGN_CENTER,  1);
                insertCell(table, patient.getHeight() + "", Element.ALIGN_CENTER, 1);
                insertCell(table, patient.getWeight() + "", Element.ALIGN_CENTER, 1);
                insertCell(table, patient.getGender(), Element.ALIGN_CENTER, 1);
                insertCell(table, patient.getBirthday(), Element.ALIGN_CENTER, 1);
                insertCell(table, patient.getAadhaar(), Element.ALIGN_CENTER, 1);
                insertCell(table, patient.getPhoneNumber(), Element.ALIGN_CENTER, 1);
            }
            //add the PDF table to the paragraph
            paragraph.add(table);
            // add the paragraph to the document
            doc.add(paragraph);

        }
        catch (DocumentException dex)
        {
            dex.printStackTrace();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (doc != null){
                //close the document
                doc.close();
            }
            if (docWriter != null){
                //close the writer
                docWriter.close();
            }
            return pdfFilename;
        }
    }
    private static String getPdfFileFromPatientsWithTestingResults(Context context, ArrayList<Patient> patients, String pdfFilename, UserVo userVo){

        Document doc = new Document();
        PdfWriter docWriter = null;

        try {
            //file path
            docWriter = PdfWriter.getInstance(doc , new FileOutputStream(pdfFilename));

            //document header attributes
            doc.addAuthor(context.getResources().getString(R.string.app_name));
            doc.addCreationDate();
            doc.addProducer();
            doc.addCreator(userVo.getFirstName() + " " + userVo.getLastName());
            doc.addTitle("Report Patients");
            doc.setPageSize(PageSize.LETTER);

            //open document
            doc.open();

            //create a paragraph
            Paragraph paragraph = new Paragraph();


            //specify column widths
            float[] columnWidths = {3f, 4f, 1.5f, 1.5f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(90f);

            //insert column headings
            insertCell(table, "TestingDate",Element.ALIGN_CENTER, 1);
            insertCell(table, "BloodSugarType",Element.ALIGN_CENTER, 1);
            insertCell(table, "mg/dl",Element.ALIGN_CENTER, 1);
            insertCell(table, "mmol", Element.ALIGN_CENTER,1);
            table.setHeaderRows(1);

            for (Patient patient : patients){
                //insert an empty row
                insertCell(table, "",  Element.ALIGN_CENTER,4);
                //create section heading by cell merging

                insertCell(table, patient.getFirstName() + " " + patient.getLastName() + "(" + patient.getBirthday() + ")", Element.ALIGN_LEFT, 4);
                double orderTotal, total = 0;

                //just some random data to fill
                for(TestingResult testingResult : patient.getTestingResults()){
                    long timestamp = testingResult.getCurrentDate();
                    String timer = GmtUtil.gmt0ToLocal(DateUtils.long2MinuteString(timestamp));

                    insertCell(table, timer, Element.ALIGN_CENTER,1);
                    insertCell(table, getBloodType(testingResult.getBloodSugarType(), context),Element.ALIGN_CENTER, 1);
                    insertCell(table, Long.toString(testingResult.getMgdl()),Element.ALIGN_CENTER, 1);
                    insertCell(table, Float.toString(testingResult.getMmol()),Element.ALIGN_CENTER, 1);
                }
            }
            //add the PDF table to the paragraph
            paragraph.add(table);
            // add the paragraph to the document
            doc.add(paragraph);

        }
        catch (DocumentException dex)
        {
            dex.printStackTrace();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (doc != null){
                //close the document
                doc.close();
            }
            if (docWriter != null){
                //close the writer
                docWriter.close();
            }
            return pdfFilename;
        }
    }
    @SuppressLint("StaticFieldLeak")
    public static void createPDFFromPatients(final Context context, final ArrayList<Patient> patients, final String pdfFilename, final UserVo userVo, final boolean hasTestingResults, final PDFListener listener){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    if (hasTestingResults){
                        return getPdfFileFromPatientsWithTestingResults(context, patients, pdfFilename, userVo);
                    }else{
                        return getPdfFileFromPatients(context, patients, pdfFilename, userVo);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null && s.trim().length() > 0) {
                    listener.createPDF(s);
                } else {
                    listener.createPDF(null);
                }
            }
        }.execute();
    }
    private static void insertCell(PdfPTable table, String text,int align,  int colspan){

        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim()));
        //set the cell alignment
        cell.setHorizontalAlignment(align);
        //set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
        //in case there is no text and you wan to create an empty row
        if(text.trim().equalsIgnoreCase("")){
            cell.setMinimumHeight(10f);
        }
        //add the call to the table
        table.addCell(cell);

    }
    private static String getBloodType(String blood, Context context){
        if (blood.equals("BB")){
            return context.getString(R.string.fasting_blood_sugar);
        }else if (blood.equals("PM")){
            return context.getString(R.string.postprandial_blood_sugar);
        }else {
            return context.getString(R.string.random_blood_sugar);
        }
    }
}
