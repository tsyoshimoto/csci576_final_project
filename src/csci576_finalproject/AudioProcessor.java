package csci576_finalproject;

import static csci576_finalproject.MyPlayer.waitingForAudioSync;
import static csci576_finalproject.MyPlayer.waitingForVideoSync;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.DataLine.Info;

/**
 *
 * <Replace this with a short description of the class.>
 *
 * @author Giulio
 */
public class AudioProcessor {
    private static boolean paused;
    private static boolean stopped;
    
    private InputStream waveStream;

    private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb
    private final int SAMPLES_PER_SECOND = 48000;
    private final int NAN0SECONDS_PER_SAMPLE = 1000000000/SAMPLES_PER_SECOND;
    private final int BITS_PER_SAMPLE = 16;
    
    Thread audioThread;

    // plays the sound
    private static AudioInputStream audioInputStream = null;
    private static Info info;
    private static AudioFormat audioFormat;

    static byte[] audioBuffer;
    static int numSamplesRead;
    
    static SourceDataLine dataLine = null;
    static int readBytes;
    
    //CONSTRUCTOR USED FOR PART 1: PLAYING VIDEO
    public AudioProcessor(InputStream waveStream) {
        this.waveStream = waveStream;
        initialize();

        audioThread = new Thread() {
            public void run() {
                part1Thread();
            }
        };
    }

    public void play() {
         try
        {
            audioThread.start();        
        }
        catch(IllegalThreadStateException ex) {
            //It is currently paused
            if(!stopped)
            {
                paused = false;
                audioThread.interrupt();
            }
            else //Stopped and starting again
            {
                stopped = false;
                paused = false;
                audioThread = new Thread() {
                    public void run() {
                        part1Thread();
                    }
                };
                audioThread.start();
            }
           
        }
    }

    public void pause() {
        paused = true;
        audioThread.interrupt();
    }
    
    public void stop() {
        stopped = true;
        audioThread.interrupt();
    }
    
    public void setAudioStream(InputStream waveStream) {
        this.waveStream = waveStream;
        try {
	    //audioInputStream = AudioSystem.getAudioInputStream(this.waveStream);

            //add buffer for mark/reset support, modified by Jian
            InputStream bufferedIn = new BufferedInputStream(this.waveStream);
            audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);

        } catch (UnsupportedAudioFileException e1) {
            //throw new PlayWaveException(e1);
        } catch (IOException e1) {
            //throw new PlayWaveException(e1);
        }
    }
    
    private void initialize()
    {
        paused = false;
        stopped = false;

        try {
	    //audioInputStream = AudioSystem.getAudioInputStream(this.waveStream);

            //add buffer for mark/reset support, modified by Jian
            InputStream bufferedIn = new BufferedInputStream(this.waveStream);
            audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);

        } catch (UnsupportedAudioFileException e1) {
            //throw new PlayWaveException(e1);
        } catch (IOException e1) {
            //throw new PlayWaveException(e1);
        }

        // Obtain the information about the AudioInputStream
        audioFormat = audioInputStream.getFormat();
        info = new Info(SourceDataLine.class, audioFormat);
        
        audioBuffer = new byte[EXTERNAL_BUFFER_SIZE];
        numSamplesRead = 0;
        
        // opens the audio channel
                
                try {
                    dataLine = (SourceDataLine) AudioSystem.getLine(info);
                    dataLine.open(audioFormat, EXTERNAL_BUFFER_SIZE);
                } catch (LineUnavailableException e1) {
                    //throw new PlayWaveException(e1);
                }

                // Starts the music :P
                dataLine.start();

                readBytes = 0;
    }
    
    private void part1Thread() {
                try {

                    while (readBytes != -1) {
                        long startTime = System.nanoTime();
                           if(stopped)
                        {
                            break;
                        }
                        while(!paused && !waitingForVideoSync) {
                            if(stopped)
                            {
                                break;
                            }
                        
                            

                            /*if(numSamplesRead/SAMPLES_PER_SECOND == 5) {
                                if(waitingForAudioSync)
                                {
                                   waitingForAudioSync = false;
                                   waitingForVideoSync = false;
                                }
                                else {
                                    waitingForVideoSync = true;
                                }

                                numSamplesRead = 0;
                                System.out.println("found in audio");
                            }*/
                            
                             readBytes = audioInputStream.read(audioBuffer, 0,
                               BITS_PER_SAMPLE);//audioBuffer.length);
                            if (readBytes >= 0) {
                                dataLine.write(audioBuffer, 0, readBytes);
                            }
                            
                            numSamplesRead++;
                            
                            long timeDiff = System.nanoTime() - startTime;
                            long waitTime = NAN0SECONDS_PER_SAMPLE - timeDiff;
                            if(waitTime < 0) {
                                continue;
                            }
                            try {
                            sleep(waitTime/1000000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(VideoProcessor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        }
                        
                    }
                } catch (IOException e1) {
                    //throw new PlayWaveException(e1);
                } finally {
                    // plays what's left and and closes the audioChannel
                    dataLine.drain();
                    dataLine.close();
                }
            
    }
}
