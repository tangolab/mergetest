package com.spring.merge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

public class ITextMerge implements IPdfMerge {

    List<URL> files = new ArrayList<URL>();

    @Override
    public void addFile(String filePath) throws FileNotFoundException {
        try {
            files.add(new File(filePath).toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mergeDocuments(String filePath){
        try {
            Document document = new Document();
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(filePath));
    
            document.open();
            for (URL file : files){
                PdfReader reader = new PdfReader(file);
                copy.addDocument(reader);
                copy.freeReader(reader);
                reader.close();
            }
            document.close();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void reset() {
        files.clear();
    }

}