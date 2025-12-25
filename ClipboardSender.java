
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.Base64;

public class ClipboardSender {

    public static void main(String[] args) {
        writeClipboard("");
        System.out.println("Reading file in binary and writing to clipboard every 500ms...");

        if (args.length < 1) {
            System.err.println("Usage: java ClipboardSender <input_file>");
            return;
        }

        String filePath = args[0];
        int chunkSize = 204800; // Size of each chunk to read
        int position = 0; // Start position

        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            long fileLength = file.length();
            writeClipboard("start copy file\n" + filePath);

            while (true) {
                String[] newClipboardText = readClipboardText().split(" copied up to ");
                String filename = newClipboardText[0];
                int receivedPosition = newClipboardText.length > 1 ? Integer.parseInt(newClipboardText[1]) : 0;

                if (filename.equals("prepare to receive " + filePath) || (filePath.equals(filename) && receivedPosition == position - 1)) {
                    byte[] buffer = readFileChunk(file, position, chunkSize);
                    String base64Data = Base64.getEncoder().encodeToString(buffer); // Encode to Base64
                    String clipboardContent = "copy file from " + position + " to " + (position + buffer.length - 1) + "\n"
                            + base64Data + "\nfile block end";
                    writeClipboard(clipboardContent); // Write to clipboard
                    System.out.println("Copied chunk to clipboard: " + position + " to " + (position + buffer.length - 1));
                    position += buffer.length;
                    if (receivedPosition + 1 >= fileLength) {
                        break; // Finished reading the entire file
                    }
                }

                try {
                    Thread.sleep(500); // Wait for 500 milliseconds
                } catch (InterruptedException e) {
                    System.err.println("Sleep interrupted: " + e.getMessage());
                }
            }

            // Signal that copying is finished
            writeClipboard("copy file finish");
            System.out.println("Finished copying file to clipboard.");
        } catch (IOException e) {
            System.err.println("Failed to read file: " + e.getMessage());
        }
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

    private static byte[] readFileChunk(RandomAccessFile file, int position, int chunkSize) throws IOException {
        file.seek(position); // Move to the specific position
        int bytesToRead = (int) Math.min(chunkSize, file.length() - position); // Adjust chunk size if near the end
        byte[] buffer = new byte[bytesToRead];
        file.readFully(buffer); // Read the chunk
        return buffer;
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
