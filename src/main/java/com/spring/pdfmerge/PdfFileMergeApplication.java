package com.spring.pdfmerge;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import com.spring.merge.IPdfMerge;
import com.spring.merge.ITextMerge;
import com.spring.merge.PdfboxMerge;


import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PdfFileMergeApplication implements ApplicationRunner {

    private static final String TEMP_FILE_PREFIX = "_";
    // private static IPdfMerge pdfMergeUtil = new ITextMerge();
    private static IPdfMerge pdfMergeUtil = new PdfboxMerge();

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PdfFileMergeApplication.class, args);
    }

    private static String getWorkFolder(String sourceFolder) throws Exception {
        String workFolder = sourceFolder + "/work";
        File file = new File(workFolder);
        if (file.isDirectory())
            // empty the work folder
            Arrays.stream(file.listFiles()).forEach(File::delete);
        else {
            // if \\work folder does not exist, create it now
            if (!file.mkdir()) {
                throw new Exception("Unable to create working folder " + workFolder);
            }
        }
        return workFolder;
    }

    private static String processFolder(String src, String dest, String pref, String filter, Long chunkSize,
            String workFolder) {
        int outputFiles = 0;
        String retFileName = "";
        try {
            File[] files = getFilteredFiles(src, filter);
            // we have atleast two files
            if (files.length > 1) {
                int n = 0;

                pdfMergeUtil.reset();

                // PDFMergerUtility PDFmerger = new PDFMergerUtility();
                for (int i = 0; i < files.length; i++, n++) {
                    // form the fully qualified source file path
                    // File file = new File(src + "\\" + files[i].getName());
                    pdfMergeUtil.addFile(src + "\\" + files[i].getName());
                    // PDFmerger.addSource(file);
                    if (n >= chunkSize || (i + 1 == files.length)) {
                        // identify destination pattern
                        retFileName = workFolder + "\\" + pref + i + ".pdf";

                        // PDFmerger.setDestinationFileName(retFileName);
                        // use up to 80% of JVM free memory
                        // long memoryLimit = Runtime.getRuntime().freeMemory() * 80 / 100;
                        // then fallback to temp file (unlimited size)
                        // Merging the documents
                        pdfMergeUtil.mergeDocuments(retFileName);
                        // PDFmerger.mergeDocuments(MemoryUsageSetting.setupMixed(memoryLimit ));
                        outputFiles++;
                        n = -1;
                        // Instantiating another PDFMergerUtility
                        // PDFmerger = new PDFMergerUtility();
                        pdfMergeUtil.reset();
                        performGC();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        if (outputFiles > 1) {
            retFileName = processFolder(workFolder, dest, pref + TEMP_FILE_PREFIX, pref + ".*\\.pdf",
                    chunkSize < 10 ? 10 : Math.round(chunkSize * .75), workFolder);
        } else {
            File f = new File(retFileName);
            File t = new File(dest);
            if (t.isFile()) {
                t.delete();
            }
            f.renameTo(t);
            retFileName = dest;
        }

        return retFileName;
    }

    private static void performGC() {
        // Mimimum acceptable free memory you think your app needs
        long minRunningMemory = (1024 * 1024);

        Runtime runtime = Runtime.getRuntime();

        if (runtime.freeMemory() < minRunningMemory)
            System.gc();
    }

    private static File[] getFilteredFiles(String src, String filter) {
        // source path
        File f = new File(src);
        // declare filtering routine
        FilenameFilter fnFilter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                // find files that match the specified pattern
                return name.matches(filter);
            }
        };

        // filter in only the files that match the specified name pattern
        File[] files = f.listFiles(fnFilter);
        return files;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<String> mergeLib = args.getOptionValues("merge.library");

        if (args.containsOption("merge.library") && args.containsOption("source.folder")
                && args.containsOption("target.filepath") && args.containsOption("chunk.size")) {
            String workFolder = PdfFileMergeApplication.getWorkFolder(args.getOptionValues("source.folder").get(0));
            Long start = System.currentTimeMillis();
            if (mergeLib.get(0).toLowerCase().equals("itext")) {
                pdfMergeUtil = new ITextMerge();
            } else {
                pdfMergeUtil = new PdfboxMerge();
            }
            // process pdf files in the specified source folder
            processFolder(args.getOptionValues("source.folder").get(0), args.getOptionValues("target.filepath").get(0),
                    TEMP_FILE_PREFIX, ".*\\.pdf", Long.parseLong(args.getOptionValues("chunk.size").get(0)),
                    workFolder);
            Long end = System.currentTimeMillis();
            System.out.format("Time taken - %.2f seconds using %s\n", (double) (end - start) / 1000,mergeLib.get(0));

        }
        else{
            System.out.format("all 4 arguments are required --merge.library=<itext|pdfbox> --source.folder=c:\\Projects\\Workspace --target.filepath=c:\\Projects\\Workspace\\outputfile.pdf --chunk.size=5");
        }
    }
}