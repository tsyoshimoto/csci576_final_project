/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csci576_finalproject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 *
 * @author bb500e
 */
public class MyProgram extends javax.swing.JFrame {

    static VideoProcessor videoProcessor;
    static AudioProcessor audioProcessor;
    
    static String videoFileName;
    static String audioFileName;
    static String outputVideoName;
    static String outputAudioName;
    
    /**
     * Creates new form MyProgram
     */
    public MyProgram() {
        initComponents();
        shotStartedLabel.setVisible(false);
        sequenceStartedLabel.setVisible(false);
        
        try {
            //AUDIO
            // opens the inputStream
            FileInputStream audioInputStream;
            try {
                audioInputStream = new FileInputStream(audioFileName);
                //inputStream = this.getClass().getResourceAsStream(filename);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }

            // initializes the playSound Object
            audioProcessor = new AudioProcessor(audioInputStream);


            //VIDEO
            FileInputStream videoInputStream;
            try {
                videoInputStream = new FileInputStream(videoFileName);
                //inputStream = this.getClass().getResourceAsStream(filename);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }
            
            FileOutputStream videoOutputStream;
            try {
                videoOutputStream = new FileOutputStream(outputVideoName);
                //inputStream = this.getClass().getResourceAsStream(filename);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }
            
            videoProcessor = new VideoProcessor(videoInputStream, videoOutputStream, imageLabel, shotStartedLabel, sequenceStartedLabel);
            //videoProcessor = new VideoProcessor(videoInputStream, imageLabel, shotStartedLabel, sequenceStartedLabel);
            
//playVideo = new PlayVideo(videoInputStream, jLabel1);

            

        } catch (Exception e) {

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        startProcessingButton = new javax.swing.JButton();
        imageLabel = new javax.swing.JLabel();
        sequenceStartedLabel = new javax.swing.JLabel();
        shotStartedLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        startProcessingButton.setText("Start Processing");
        startProcessingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startProcessingButtonActionPerformed(evt);
            }
        });

        sequenceStartedLabel.setText("Sequence Started");

        shotStartedLabel.setText("Shot Started");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(imageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(135, 135, 135)
                                .addComponent(startProcessingButton))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(68, 68, 68)
                                .addComponent(sequenceStartedLabel)
                                .addGap(32, 32, 32)
                                .addComponent(shotStartedLabel)))
                        .addGap(0, 114, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(sequenceStartedLabel)
                    .addComponent(shotStartedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(startProcessingButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startProcessingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startProcessingButtonActionPerformed
        // TODO add your handling code here:
        videoProcessor.play();
        audioProcessor.play();
        
    }//GEN-LAST:event_startProcessingButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MyProgram.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MyProgram.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MyProgram.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MyProgram.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        videoFileName = args[0];
        audioFileName = args[1];
        outputVideoName = args[2];
        outputAudioName = args[3];
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MyProgram().setVisible(true);
                    
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel imageLabel;
    private javax.swing.JLabel sequenceStartedLabel;
    private javax.swing.JLabel shotStartedLabel;
    private javax.swing.JButton startProcessingButton;
    // End of variables declaration//GEN-END:variables
}