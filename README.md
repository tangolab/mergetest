4 arguments needed to run

java -jar target/pdfMerge-0.0.1-SNAPSHOT.jar --merge.library=itext --source.folder=c:\\Projects\\Workspace --target.filepath=c:\\Projects\\Workspace\\itext.pdf --chunk.size=5


--source.folder=<source path containing  pdf files>
--target.filepath=<mergedfile.pdf>
--merge.library=<itext | pdfbox>
--chunk.size=<merge in groups of>