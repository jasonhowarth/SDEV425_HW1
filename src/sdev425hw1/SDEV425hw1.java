/*
* JASON HOWARTH
* SDEV425 6980
* HOMEWORK 1 - Fix security issues
* Use CMU SEI CERT Rules and Recommendations:
* https://wiki.sei.cmu.edu/confluence/display/java/1+Front+Matter
 */
package sdev425hw1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class SDEV425hw1 {

    public static void main(String[] args) {

        /*1. Rule 49: MSC05-J. Do not exhaust heap space 
         * ISSUE: OutOfMemoryError due to reading in entire file
         * FIX: Replaced BufferedReader with Scanner to more efficiently read file
         */
        FileInputStream inputStream = null;
        Scanner sc = null;
        //Count email addresses in invalid formats
        int emailBadLength = 0;
        int emailBadCharacters = 0;
        //Max file size in MB
        long maxFileSizeMB = 10;

        try {

            /*
            * 2. Rec. 01: DCL53-J. Minimize the scope of variables
            * ISSUE: Scope of variable not minimized.
            * FIX: filename variable set to final so it can't be modified later
             */
            // Read the filename from the command line argument
            final String filename = args[0];

            /*
            * 3. Rule 49: MSC05-J. Do not exhaust heap space 
            * ISSUE: OutOfMemoryError due to reading in large file
            * FIX: Get filesize using .length() then exit program if file is larger than 10MB
             */
            //GET DIRECTORY WITH EMAIL.TXT AND CHECK FILE SIZE
            String workingDirectory = System.getProperty("user.dir");
            String fullPath = workingDirectory.concat("/").concat(filename);
            File emailFile = new File(fullPath);
            double fileSizeBytes = emailFile.length();
            //Convert Bytes to MB
            double fileSizeMb = fileSizeBytes / (1024 * 1024);
            long fileSizeRoundedMb = Math.round(fileSizeMb);
            if (fileSizeRoundedMb > maxFileSizeMB) {
                //FILE EXCEEDED MAX FILE SIZE
                System.out.println("File Size Check Failed.\n" + filename + " > " + maxFileSizeMB + " MB. Program Exiting.");
                System.exit(0);
            } else { 
                //FILE IS LESS THAN OR EQUAL TO MAX FILE SIZE
                System.out.println("File Size Check Passed.\n" + filename + " is " + fileSizeRoundedMb + " MB / " + fileSizeBytes + " Bytes <= " + maxFileSizeMB + "MB.");
            }
            
            //READ IN DATA FROM TEXT FILE USING SCANNER
            inputStream = new FileInputStream(filename);
            sc = new Scanner(inputStream);

            /*
             * 4. Rec. 00: IDS56-J. Prevent arbitrary file upload
             * ISSUE: Error from reading in non-text file.
             * FIX: Perform MIME type check using .probeContentType() then exit 
             * program if incorrect MIME type. 
             * Note: This is weak MIME type check and dependent on OS.
             */
            File file = new File(filename);
            Path path = file.toPath();
            String fileMimeType = Files.probeContentType(path);
            //MIME TYPE
            String correctMimeType = "text/plain";
            if (!fileMimeType.equals(correctMimeType)) {
                //INCORRECT MIME TYPE
                System.out.println("\nFile MIME Type Check Failed.\n" + filename + " MIME type " + fileMimeType
                        + " is invalid. Program exiting.");
                System.exit(0);
            } else {
                //CORRECT MIME TYPE
                System.out.println("\nFile MIME Type Check Passed.\n" + filename + " MIME type is " + fileMimeType);
            }

            //Print valid email addresses
            System.out.println("\nValid Email Addresses:");
            while (sc.hasNextLine()) {
                String fileLine = sc.nextLine();
                /*
                 * 5. Rule 00: IDS06-J. Exclude unsanitized user input from format strings
                 * ISSUE: Unsanitized user input read in from file is printed using System.out
                 * FIX: Validate email address format and length using RegEx and .matches() 
                 * Then count and exclude email addresses that are improperly formatted.
                 * Reference: Acceptable email address format and length from
                 * https://www.rfc-editor.org/errata_search.php?rfc=3696&eid=1690
                 */
                //First Regex to limit email address length to 254 characters and accepted characters
                //Reference: https://www.regular-expressions.info/email.html
                if (!fileLine.matches("^[a-zA-Z0-9@._%+-]{6,254}$")) {
                    emailBadLength++;
                    //Second Regex to match Email Address format 
                    //Reference: https://www.regular-expressions.info/email.html
                } else if (!fileLine.matches("\\b[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\\b")) {
                    emailBadCharacters++;
                } else {
                    System.out.println(fileLine);
                }
            }
            /*
         * 6. Rule 13: FIO02-J. Detect and handle file-related errors
         * ISSUE: File not present in arguments will throw an exception.
         * FIX: Check if file exists by catching ArrayIndexOutOfBoundsException, then exit program if file from arguments doesn't exist.
             */
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("""
                    File check Failed: No file specified. Program exiting.
                    How to fix in NetBeans: Right-click Source Packages > Properties > Run > Arguments: EmailAddresses.txt""");
            System.exit(0);
        } catch (IOException io) {
            System.out.println("File IO exception" + io.getMessage());

        } finally {
            // Print number of invalid email addresses
            System.out.println("\nInvalid email addresses: " + (emailBadCharacters + emailBadLength));
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException io) {
                System.out.println("Issue closing the Files" + io.getMessage());
            }
        }
    }
}
