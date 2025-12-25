
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.Base64;

public class ClipboardReceiver {

    public static void main(String[] args) {
        writeClipboard("");
        System.out.println("Reading clipboard every 500ms until 'copy file finish' is detected...");
        RandomAccessFile randomAccessFile = null;
        String filename = "";
        int lastEndPosition = -1;

        while (true) {
            String[] newClipboardText = readClipboardText().split("\\n");
            String command = newClipboardText[0];
            String data = newClipboardText.length > 1 ? newClipboardText[1] : "";
            String endFlag = newClipboardText.length > 2 ? newClipboardText[2] : "";

            if ("start copy file".equals(command)) {
                filename = data;
                try {
                    randomAccessFile = new RandomAccessFile("receive" + File.separator + filename, "rw");
                    writeClipboard("prepare to receive " + filename);
                    System.out.println("Prepared to receive file: " + filename);
                } catch (IOException e) {
                    System.err.println("Failed to open file for writing: " + e.getMessage());
                }
            }

            if ("copy file finish".equals(command)) {
                if (randomAccessFile != null) {
                    try {
                        randomAccessFile.close();
                        randomAccessFile = null;
                    } catch (IOException e) {
                        System.err.println("Failed to close file: " + e.getMessage());
                    }
                }
                writeClipboard(""); // Clear the clipboard before exiting
                break;
            }

            if (command.startsWith("copy file from") && "file block end".equals(endFlag)) {
                String[] range = command.substring(15).split(" to ");
                int fileStart = Integer.parseInt(range[0]);
                int fileEnd = Integer.parseInt(range[1]);
                if (randomAccessFile != null && fileStart == lastEndPosition + 1) {
                    try {
                        byte[] decodedData = Base64.getDecoder().decode(data); // Decode Base64 data
                        randomAccessFile.seek(fileStart); // Move to the specific position
                        randomAccessFile.write(decodedData); // Write binary data
                        lastEndPosition = fileEnd;
                        System.out.println("Wrote data to file from " + fileStart + " to " + fileEnd);
                    } catch (IOException e) {
                        System.err.println("Failed to write to file: " + e.getMessage());
                    }
                }
                writeClipboard(filename + " copied up to " + lastEndPosition); // Acknowledge the received block
            }

            try {
                Thread.sleep(500); // Wait for 500 milliseconds
            } catch (InterruptedException e) {
                System.err.println("Sleep interrupted: " + e.getMessage());
            }
        }
        System.out.println("Finished loop reading clipboard.");
    }

    private static String readClipboardText() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable contents = clipboard.getContents(null);
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) contents.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (Exception e) {
            System.err.println("Failed to read clipboard: " + e.getMessage());
        }
        return "";
    }

    private static void writeClipboard(String content) {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(content), null); // Set clipboard to specified content
        } catch (Exception e) {
            System.err.println("Failed to update clipboard: " + e.getMessage());
        }
    }
}
