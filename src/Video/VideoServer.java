package Video;

import java.util.List;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.mrl.RtspMrl;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;

import javafx.embed.swing.*;
import javafx.application.*;

public class VideoServer extends VideoBase
{
private final MediaPlayerFactory mediaPlayerFactory;
private final EmbeddedMediaPlayer localMediaPlayer;

private final JFrame frame;
//private final JFXPanel pane;
private final JPanel contentPane;
private final JPanel sourceControls;
private final JPanel videoPanel;
private final JPanel localPanel;
private final Canvas localCanvas;
private final JPanel localStreamControls;

private final JLabel mediaLabel;
private final JTextField mediaTextField;

private final JLabel streamToLabel;
private final JTextField streamToTextField;
private final JButton sendButton;
private final JButton sendSnapshotButton;

private final CanvasVideoSurface localVideoSurface;

private String roomname;

/*public static void main(String[] args) throws Exception 
{
   setLookAndFeel();

   SwingUtilities.invokeLater(new Runnable() 
   {
      @Override
      public void run() 
      {
         new VideoServer().start();
      }
   });
}*/

public VideoServer(String rn) 
{
   roomname = rn;
   setLookAndFeel();

   mediaPlayerFactory = new MediaPlayerFactory("--no-video-title-show");
   localMediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();

   contentPane = new JPanel();
   contentPane.setBorder(new EmptyBorder(16, 16, 16, 16));
   contentPane.setLayout(new BorderLayout(16, 16));

   sourceControls = new JPanel();
   sourceControls.setBorder(new TitledBorder("Source"));
   sourceControls.setLayout(new BoxLayout(sourceControls, BoxLayout.X_AXIS));

   mediaLabel = new JLabel("Media:");
   mediaLabel.setDisplayedMnemonic('m');
   sourceControls.add(mediaLabel);

   sourceControls.add(Box.createHorizontalStrut(4));

   mediaTextField = new JTextField();
   mediaTextField.setFocusAccelerator('m');
   sourceControls.add(mediaTextField);

   contentPane.add(sourceControls, BorderLayout.NORTH);

   videoPanel = new JPanel();
   videoPanel.setLayout(new GridLayout(1, 2, 16, 0));

   localCanvas = new Canvas();
   localCanvas.setBackground(Color.black);
   localCanvas.setSize(320, 180);

   localStreamControls = new JPanel();
   localStreamControls.setLayout(new BoxLayout(localStreamControls, BoxLayout.X_AXIS));

   localStreamControls.add(Box.createHorizontalStrut(4));

   streamToLabel = new JLabel("Stream To:");
   streamToLabel.setDisplayedMnemonicIndex(7);
   localStreamControls.add(streamToLabel);

   streamToTextField = new JTextField();
   streamToTextField.setFocusAccelerator('t');
   streamToTextField.setColumns(12);
   localStreamControls.add(streamToTextField);

   sendButton = new JButton("Send");
   sendButton.setMnemonic('s');
   localStreamControls.add(sendButton);

   sendSnapshotButton = new JButton("Snap");
   sendSnapshotButton.setMnemonic('n');
   localStreamControls.add(sendSnapshotButton);

   localVideoSurface = mediaPlayerFactory.newVideoSurface(localCanvas);
   localMediaPlayer.setVideoSurface(localVideoSurface);

   localPanel = new JPanel();
   localPanel.setBorder(new TitledBorder("Local"));
   localPanel.setLayout(new BorderLayout(0, 8));
   localPanel.add(localCanvas, BorderLayout.CENTER);
   localPanel.add(localStreamControls, BorderLayout.SOUTH);

   videoPanel.add(localPanel);

   contentPane.add(videoPanel, BorderLayout.CENTER);

   frame = new JFrame("video chat");
   frame.setContentPane(contentPane);
   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   frame.pack();
	//pane = new JFXPanel();


   sendButton.addActionListener(new ActionListener() 
   {
      @Override
      public void actionPerformed(ActionEvent e) 
      {
         send();
      }
   });

   sendSnapshotButton.addActionListener(new ActionListener() 
   {
      @Override
      public void actionPerformed(ActionEvent e) 
      {
         localMediaPlayer.saveSnapshot();
      }
   });

}

public void start() 
{
   mediaTextField.setText(!RuntimeUtil.isWindows() ? "v4l2:///dev/video0" : "dshow://");

   streamToTextField.setText(":8554/" + roomname);

   frame.setVisible(true);
}

public void send() 
{
   String mrl = mediaTextField.getText();
   String streamTo = streamToTextField.getText();

   if(mrl.startsWith("v4l2://") || mrl.startsWith("dshow://"))
   {
      String[] localOptions = {formatRtspStream(streamTo), ":sout-all", ":sout-keep", ":live-caching=300"/*, ":input-slave=alsa://hw:0,0"*/};
      localMediaPlayer.playMedia(mrl, localOptions);
   }
   else if(mrl.startsWith("file://"))
   {
      String[] localOptions = {":sout=#duplicate{dst=display,dst=rtp{sdp=rtsp://"+streamTo+"}}", ":sout-all", ":sout-keep", " :file-caching=2000"};
      localMediaPlayer.playMedia(mrl, localOptions);
   }
   else if(mrl.startsWith("http://www.youtube.com") || mrl.startsWith("https://www.youtube.com"))
   {
      localMediaPlayer.setPlaySubItems(true); // <--- This is very important for YouTube media

      localMediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() 
      {
         @Override
         public void buffering(MediaPlayer mediaPlayer, float newCache) 
         {
          System.out.println("Buffering " + newCache);
         }

         @Override
         public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t subItem) 
         {
            List<String> items = mediaPlayer.subItems();
            System.out.println(items);
         }
      });
      String[] localOptions = {":sout=#transcode{vcodec=mp2v,vb=100,scale=1,acodec=mpga,ab=128,channels=2,samplerate=44100}:duplicate{dst=display,dst=rtp{sdp=rtsp://"+streamTo+"}}", ":sout-all", ":sout-keep", ":network-caching=2000"};
      localMediaPlayer.playMedia(mrl, localOptions);
   }
}

public void stop()
{
	if(localMediaPlayer.isPlaying())
	{
		localMediaPlayer.release();
	}
	frame.setVisible(false);
}

private static String formatRtspStream(String mrl) 
{
   StringBuilder sb = new StringBuilder(60);
   sb.append(":sout=#transcode{vcodec=mp2v,vb=800,scale=1,acodec=mpga,ab=128,channels=2,samplerate=44100}:duplicate{dst=display,dst=rtp{sdp=rtsp://" + mrl + "}}");
   return sb.toString();
}

}
