4 arguments needed to run the program

java -jar target/pdfMerge-0.0.1-SNAPSHOT.jar --merge.library=itext --source.folder=c:\\Projects\\Workspace --target.filepath=c:\\Projects\\Workspace\\itext.pdf --chunk.size=5


--source.folder=<source path containing  pdf files>
--target.filepath=<mergedfile.pdf>
--merge.library=<itext | pdfbox>
--chunk.size=<merge in groups of>


May use ./compare.bat to run a comparison between pdfbox and itext. Make sure the two paths are updated as needed


    $ ./compare.bat

    or

    C:\> compare.bat


Use the following command to stage test data (pdf file) 

    $ for i in {1..10000}; do cp "myPDF.pdf" "myPDF_$i.pdf"; done