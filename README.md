# ECE4310Antivirus

Runnable .jar file is included called AVScanner.jar

To run the scanner:
1. Run the AVScanner.jar file. 
2. Select a directory to scan (Such as Example)
3. Choose a definitions.txt file.  This is just a list of SHA-256 hash strings with one string on each line.
4. Click Scan
5. To delete viruses, click the delete button.

In the example folder are some sample data files and a sample definitions.txt file.

For this example, I have defined test1.txt as a virus.  The content of test1.txt is "Example text".  test2.txt is not defined as a virus and its content is "Example tex".  I have also defined cpp-logo as a virus.

I then copied test1.txt and cpp-logo to the folder titled "1".  These copied files have the same data as the files they were copied from.

Then I created a folder called "Example Folder".  In this folder, I pasted test1.txt with the same data and filename as the one in the main folder.  Inside "Example Folder", I created a folder called "Nested Folder".  In this folder, I created a file called test2.txt and added the same data as test1.txt which is "Example text".

These examples are to show a few things:

1. Nested files and folders work through recursion.
2. Scanner is looking at data inside file, not filename or path.
3. Works on all file types.
4. Works with directory names with spaces in the name.
