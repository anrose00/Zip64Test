import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.io.FilenameUtils;

public class ZipTest {
        public static void main(String[] args) throws Exception {
            String pathToZip = "d:\\tmp\\zip_output.zip";
            String[] listToFileNames = {"d:\\tmp\\[Content_Types].xml","d:\\tmp\\moon.png"};
            createExampleZip64(pathToZip,listToFileNames);
            showZipContents(pathToZip);  // uses commons compress
            showZipContents2(pathToZip); // uses java.util.zip - cannot deal with 64bit size fields in data descriptor
        }

        public static void createExampleZip64(String zipFileName, String[] listOfFiles) throws FileNotFoundException, ArchiveException, IOException
            {
            /* Create Output Stream that will have final zip files */
            OutputStream zip_output = new FileOutputStream(new File(zipFileName));
            /* Create Archive Output Stream that attaches File Output Stream / and specifies type of compression */
            ZipArchiveOutputStream logical_zip = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, zip_output);
            logical_zip.setUseZip64(Zip64Mode.Always);
            for (int i=0;i<listOfFiles.length;i++) {
                String fileName = FilenameUtils.getName(listOfFiles[i]);
                Path path = Paths.get(listOfFiles[i]);
                ZipArchiveEntry entry = logical_zip.createArchiveEntry(path, fileName);
                /* Create Archive entry - write header information*/
                logical_zip.putArchiveEntry( entry);
                InputStream inputStream = null;
                /* Copy input file */
                try {
                    inputStream = new FileInputStream(listOfFiles[i]);
                    IOUtils.copy(inputStream, logical_zip);
                } finally {
                    inputStream.close();
                    /* Close Archive entry, write trailer information */
                    logical_zip.closeArchiveEntry();
                }
            }
            /* Close output stream, our files are zipped */
            logical_zip.finish();
            zip_output.close();
        }

        public static void showZipContents(String zipFileName) throws FileNotFoundException, ArchiveException, IOException
        {
            /* Create Input Stream that for zip files */
            FileInputStream inputStream = new FileInputStream(new File(zipFileName));
            /* Create Archive Output Stream that attaches File Output Stream / and specifies type of compression */
            ZipArchiveInputStream zip = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.ZIP, inputStream);
            ArchiveEntry entry = null;
            while ((entry = zip.getNextEntry()) != null) {
                System.out.println(entry.getName());
            }
            inputStream.close();
        }

        public static void showZipContents2(String zipFileName) throws FileNotFoundException, IOException {
            FileInputStream inputStream = new FileInputStream(new File(zipFileName));
            ZipInputStream zip = new ZipInputStream(inputStream);
            ZipEntry entry = null;
            while ((entry = zip.getNextEntry()) != null) {
                System.out.println(entry.getName());
            }
            inputStream.close();
        }
    }

