package com.spring.merge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

public class PdfboxMerge implements IPdfMerge {
    PDFMergerUtility PDFmerger = new PDFMergerUtility();

    @Override
    public void addFile(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        PDFmerger.addSource(file);
    }

    @Override
    public void reset() {
        PDFmerger = new PDFMergerUtility();
    }

    @Override
    public void mergeDocuments(String filePath){
        PDFmerger.setDestinationFileName(filePath);
        long memoryLimit  = Runtime.getRuntime().freeMemory() * 80 / 100;
        try {
            PDFmerger.mergeDocuments(MemoryUsageSetting.setupMixed(memoryLimit));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}