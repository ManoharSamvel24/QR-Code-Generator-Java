package qrcodegenerator;

import com.formdev.flatlaf.FlatLightLaf;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;

public class QRCodeGenerator {

    private static BufferedImage currentQRImage = null;
    private static JLabel qrLabel = new JLabel(); // QR display area

    public static void generateQRCode(String data, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }

        currentQRImage = image;
        qrLabel.setIcon(new ImageIcon(image));
    }

    public static void saveQRCodeImage(Component parent) {
        if (currentQRImage == null) {
            JOptionPane.showMessageDialog(parent, "No QR code to save!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save QR Code Image");
        fileChooser.setSelectedFile(new File("qrcode.png"));
        int userSelection = fileChooser.showSaveDialog(parent);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                File fileToSave = fileChooser.getSelectedFile();
                ImageIO.write(currentQRImage, "png", fileToSave);
                JOptionPane.showMessageDialog(parent, "Image saved successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, "Error saving image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void createAndShowUI() {
        try{
            UIManager.setLookAndFeel(new FlatLightLaf());
            Font uiFont = new Font("Segoe UI", Font.PLAIN, 14);
            java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get(key);
                if (value instanceof Font) {
                    UIManager.put(key, uiFont);
                }
            }
        }
        catch(UnsupportedLookAndFeelException e){
            System.out.println(e.getMessage());
        }
        JFrame frame = new JFrame("QR Code Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 500);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        JTextField inputField = new JTextField();
        JButton generateButton = new JButton("Generate");
        JButton copyButton = new JButton("Copy to Clipboard");
        copyButton.setEnabled(false);

        inputField.addActionListener(e -> generateButton.doClick());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(generateButton, BorderLayout.EAST);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JPanel qrPanel = new JPanel(new GridBagLayout());
        qrPanel.setPreferredSize(new Dimension(280, 280));
        qrPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "QR Code",
                TitledBorder.LEFT, TitledBorder.TOP));
        qrLabel.setPreferredSize(new Dimension(280, 280));
        qrLabel.setHorizontalAlignment(JLabel.CENTER);
        qrLabel.setVerticalAlignment(JLabel.CENTER);
        qrPanel.add(qrLabel);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(qrPanel, BorderLayout.CENTER);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JButton saveButton = new JButton("Save / Export Image");
        saveButton.setEnabled(false); // Enable only after QR is generated
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.add(saveButton);
        buttonPanel.add(copyButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));


        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        frame.add(progressBar, BorderLayout.NORTH);

        frame.add(inputPanel, BorderLayout.PAGE_START);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        
        copyButton.addActionListener(e -> {
            if (currentQRImage != null) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                    new TransferableImage(currentQRImage), null
                );
                JOptionPane.showMessageDialog(frame, "QR image copied to clipboard!");
            } else {
                JOptionPane.showMessageDialog(frame, "No QR code to copy!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        generateButton.addActionListener(e -> {
            String text = inputField.getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a URL or text!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            progressBar.setVisible(true);
            generateButton.setEnabled(false);
            saveButton.setEnabled(false);

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    try {
                        generateQRCode(text, 280, 280);
                    } catch (WriterException ex) {
                        JOptionPane.showMessageDialog(frame, "Failed to generate QR code: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    progressBar.setVisible(false);
                    generateButton.setEnabled(true);
                    if (currentQRImage != null) {
                        saveButton.setEnabled(true);
                        copyButton.setEnabled(true);
                    }
                }
            };
            worker.execute();
        });

        saveButton.addActionListener(e -> saveQRCodeImage(frame));

        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(QRCodeGenerator::createAndShowUI);
    }
    
    static class TransferableImage implements Transferable {
        private final Image image;
        public TransferableImage(Image image) { 
            this.image = image; 
        }

        @Override public Object getTransferData(DataFlavor flavor) {
            return flavor.equals(DataFlavor.imageFlavor) ? image : null;
        }

        @Override public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{
                DataFlavor.imageFlavor
            };
        }

        @Override public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(DataFlavor.imageFlavor);
        }
    }

}
