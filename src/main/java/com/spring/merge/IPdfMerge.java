package com.spring.merge;

import java.io.FileNotFoundException;

public interface IPdfMerge {
    void addFile(String folderPath) throws FileNotFoundException;
    void mergeDocuments(String filePath);
    void reset();
}