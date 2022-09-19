package com.github.av1ctor;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfSplitter;
import com.itextpdf.kernel.utils.PageRange;

import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class App {
    public static void main(
        String[] args
    ) throws Exception {
        if(args.length != 2) {
            throw new Exception("Usage: pdf-split-by-size max-size folder-name");
        }

        int size = Integer.parseInt(args[0]);

        File[] files = findAllFilesOnFolder(args[1], ".pdf");

        for(File file : files) {
            System.out.printf("Splitting file %s\n", file.getName());
            splitDocumentBySize(file.getAbsolutePath(), file.getPath(), size);
        }
    }

    static void splitDocumentBySize(
        String srcPath,
        String dstPath,
        int size
    ) throws Exception {

        if(Files.size(Paths.get(srcPath)) <= size) {
            return;
        }

        PdfDocument inputPdfDoc = new PdfDocument(new PdfReader(srcPath));

        PdfSplitter splitter = new PdfSplitter(inputPdfDoc) {
            int partNumber = 1;

            @Override
            protected PdfWriter getNextPdfWriter(PageRange documentPageRange) {
                try {
                    return new PdfWriter(dstPath + " - parte " + String.format("%02d", partNumber++) + ".pdf");
                } catch (FileNotFoundException e) {
                    throw new RuntimeException();
                }
            }
        };

        List<PdfDocument> documents = splitter.splitBySize(size);

        for (PdfDocument doc : documents) {
            doc.close();
        }

        inputPdfDoc.close();
    }

    static File[] findAllFilesOnFolder(
        String folderPath,
        String suffix
    ) throws Exception {
        File f = new File(folderPath);

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return name.endsWith(suffix);
            }
        };

        return f.listFiles(filter);
    }
}
