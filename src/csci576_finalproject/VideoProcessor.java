
package csci576_finalproject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import static csci576_finalproject.MyPlayer.waitingForAudioSync;
import static csci576_finalproject.MyPlayer.waitingForVideoSync;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.Math.abs;
import static java.lang.Thread.sleep;
import java.security.KeyPair;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author bb500e
 */
public class VideoProcessor {

    private static boolean paused;
    private static boolean stopped;

    private static InputStream videoStream;
    private static JLabel videoOutLabel;
    private static JLabel shotStartedLabel;
    private static JLabel sequenceStartedLabel;

    private static OutputStream videoOutStream;

    final static int WIDTH = 480;
    final static int HEIGHT = 270;

    final static float SEQUENCE_THRESHOLD = 1.2f;
    final static float SHOT_THRESHOLD = 0.6f; //0.8f

    final static long NANOSECONDS_PER_FRAME = (1 * 1000000000) / 30;
    static int splitCount = 0;

    static BufferedImage img;
    static Thread videoThread;

    private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb
    static byte[] bytes;
    static byte[] outputBytes;
    static int byteOffset;
    static int videoOutByteOffset;

    static float entropy;
    static float lastEntropy;
    static Map<Float, Integer> entropyCountDictionary;

    static int numRead;
    static long lastShotStartTime;
    static long lastSequenceStartTime;
    static boolean inCommercial = false;

    static long startTime;
    static int numFramesRead;
    
    private enum RUN_STATE{
        part1,
        part2
    }
    private RUN_STATE runType;

    //CONSTRUCTOR USED FOR PART 1: PLAYING VIDEO
    public VideoProcessor(final InputStream videoStream, final JLabel videoOutLabel) {
        this.videoStream = videoStream;
        this.videoOutLabel = videoOutLabel;
        runType = RUN_STATE.part1;
        initialize();

        videoThread = new Thread() {
            public void run() {
                part1Thread();
            }
        };
    }

    //CONSTRUTOR USED FOR PART 2: REMOVING COMMERCIAL
    public VideoProcessor(final InputStream videoStream, final OutputStream videoOutStream, final JLabel videoOutLabel, final JLabel shotStartedLabel, final JLabel sequenceStartedLabel) {
        this.videoStream = videoStream;
        this.videoOutLabel = videoOutLabel;
        this.shotStartedLabel = shotStartedLabel;
        this.sequenceStartedLabel = sequenceStartedLabel;
        runType = RUN_STATE.part2;
        initialize();

        videoThread = new Thread() {
            public void run() {
                part2Thread();

            }
        };
    }

    public void play() {
        try {
            videoThread.start();
        } catch (IllegalThreadStateException ex) {
            //System.out.println(paused + " " + stopped);
            if(!stopped) {
                paused = false;
                videoThread.interrupt();
                System.out.println("starting again");
            }
            else {
                stopped = false;
                paused = false;
                img = null;
        outputBytes = null;
        bytes = null;
                initialize();
                if(runType == RUN_STATE.part1) {
                    videoThread = new Thread() {
                        public void run() {
                            part1Thread();
                        }
                    };
                }
                else {
                    videoThread = new Thread() {
                        public void run() {
                            part2Thread();
                        }
                    };
                }
                videoThread.start();
            }
        }

    }

    public void pause() {
        paused = true;
        videoThread.interrupt();
        //paused = true;
        //this.notifyAll();
    }

    public void stop() {
        stopped = true;
        videoThread.interrupt();
    }
    
    public void setVideoStream(InputStream videoStream) {
        this.videoStream = videoStream;
    }

    private void initialize() {
        
        entropyCountDictionary = new HashMap<>();
        img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        outputBytes = new byte[Integer.MAX_VALUE];
        bytes = new byte[WIDTH * HEIGHT * 3];
        
        setScreenToBlack();
        byteOffset = 0;
        videoOutByteOffset = 0;

        paused = false;
        stopped = false;

        entropy = 0.0f;
        lastEntropy = 0.0f;
        numRead = 0;
        lastShotStartTime = 0;
        lastSequenceStartTime = 0;
        
        numFramesRead = 0;
    }
    
    private void setScreenToBlack() {
        //Initialize to black screen
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int pix = 0;
                img.setRGB(x, y, pix);
            }
        }
        videoOutLabel.setIcon(new ImageIcon(img));
    }
    
    private void getPixelsInEachFrame() {
        startTime = System.nanoTime();
        int offset = 0;
        numRead = 0;
 
                //480x270 pixels in frame, 30 frames/sec, 5 sec
                //System.out.println(offset + " " + compareValue);
                /*if(numFramesRead/30 == 5) {
                    if(waitingForVideoSync)
                    {
                       waitingForAudioSync = false;
                       waitingForVideoSync = false;
                    }
                    else {
                        waitingForAudioSync = true;
                    }
                    
                    numFramesRead = 0;
                    System.out.println("found in video");
                }
        */
        try {
            while (offset < bytes.length && (numRead = videoStream.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
        } catch (Exception e) {

        }

        entropyCountDictionary.clear();
        int ind = 0;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                byte r = bytes[ind];
                byte g = bytes[ind + HEIGHT * WIDTH];
                byte b = bytes[ind + HEIGHT * WIDTH * 2];

                float y_luminance = 0.299f * r + 0.587f * g + 0.114f * b;
                if (!entropyCountDictionary.containsKey(y_luminance)) {
                    entropyCountDictionary.put(y_luminance, 1);
                } else {
                    int currentCount = entropyCountDictionary.get(y_luminance);
                    entropyCountDictionary.remove(y_luminance);
                    entropyCountDictionary.put(y_luminance, ++currentCount);
                }

                int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                img.setRGB(x, y, pix);
                ind++;

            }
        }
        numFramesRead++;
        
        videoOutLabel.setIcon(new ImageIcon(img));

    }

    private void calculateEntropy() {
        Iterator it = entropyCountDictionary.entrySet().iterator();
        entropy = 0.0f;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            int entropyCount = (int) pair.getValue();
            //System.out.println(entropyCount/(480f *270f));
            entropy += (entropyCount / (480f * 270f)) * Math.log(entropyCount / (480f * 270f)) / Math.log(2);
            //System.out.println(entropy);
        }
    }

    private void videoSplitProcessing(OutputStream videoOutStream, JLabel shotStartedLabel, JLabel sequenceStartedLabel) {
        try {
            if (abs(entropy - lastEntropy) > SEQUENCE_THRESHOLD) {
                //New Scene
                shotStartedLabel.setVisible(true);
                sequenceStartedLabel.setVisible(true);
                System.out.println("Sequence found " + entropy + " " + lastEntropy);

                lastShotStartTime = startTime;
                lastSequenceStartTime = startTime;

                if (!inCommercial) {
                    videoOutStream.write(outputBytes, 0/*videoOutByteOffset*/, byteOffset);
                    videoOutByteOffset += byteOffset;
                }

                byteOffset = 0;
                inCommercial = false;
                //outputBytes = new byte[Integer.MAX_VALUE - 1];
            } else if (abs(entropy - lastEntropy) > SHOT_THRESHOLD) {
                //New Shot
                shotStartedLabel.setVisible(true);
                System.out.println("Shot found");

                //Check how short of a time since last sequence detected
                if ((startTime - lastShotStartTime) / 1000000000 < 5) {
                    inCommercial = true;
                    System.out.println("Found commercial");
                }

                lastShotStartTime = startTime;
            } else {
                int currentByteIndex = 0;

                for (currentByteIndex = 0; currentByteIndex < bytes.length; currentByteIndex++) {
                    if (byteOffset == Integer.MAX_VALUE - 1) {
                        System.out.println(outputBytes.length + " " + byteOffset);
                        videoOutStream.write(outputBytes, 0/*videoOutByteOffset*/, byteOffset - 1);
                        videoOutByteOffset += byteOffset - 1;

                        byteOffset = 0;
                    }

                    outputBytes[byteOffset++] = bytes[currentByteIndex];

                }

                if ((startTime - lastShotStartTime) / 1000000000 >= 1) {
                    shotStartedLabel.setVisible(false);
                }
                if ((startTime - lastSequenceStartTime) / 1000000000 >= 1) {
                    sequenceStartedLabel.setVisible(false);
                }
            }

            lastEntropy = entropy;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
    
    private void part1Thread()
    {
        // plays the video            
                while (numRead != -1) {
                    if(stopped)
                        {
                            setScreenToBlack();
                            break;
                        }
                    while(!paused && !waitingForAudioSync) {
                        if(stopped)
                        {
                            setScreenToBlack();
                            break;
                        }
                        

                        getPixelsInEachFrame();

                        long timeDiff = System.nanoTime() - startTime;
                        long waitTime = NANOSECONDS_PER_FRAME - timeDiff;
                        if(waitTime < 0)
                        {
                           continue;
                        }
                        
                        try {
                            sleep(waitTime/1000000);
                        } catch (InterruptedException ex) {
                            //Logger.getLogger(VideoProcessor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }
                    //System.out.println("In numRead loop" + numRead);
                }

    }
    
    private void part2Thread()
    {
        while (numRead != -1) {

                    getPixelsInEachFrame();

                    calculateEntropy();
                    videoSplitProcessing(videoOutStream, shotStartedLabel, sequenceStartedLabel);
                    long timeDiff = System.nanoTime() - startTime;
                        long waitTime = NANOSECONDS_PER_FRAME - timeDiff;
                        if(waitTime < 0)
                        {
                           continue;
                        }
                        
                        try {
                            sleep(waitTime/1000000);
                        } catch (InterruptedException ex) {
                            //Logger.getLogger(VideoProcessor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
    }
}
