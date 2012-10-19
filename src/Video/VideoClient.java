package Video;

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

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.mrl.RtspMrl;

public class VideoClient extends VideoBase
{
private final MediaPlayerFactory mediaPlayerFactory;
private final EmbeddedMediaPlayer remoteMediaPlayer;

private final JFrame frame;
private final JPanel contentPane;
private final JPanel videoPanel;
private final JPanel remotePanel;
private final Canvas remoteCanvas;
private final JPanel remoteStreamControls;

private final JLabel streamFromLabel;
private final JTextField streamFromTextField;
private final JButton receiveButton;
private final JButton receiveSnapshotButton;

private final CanvasVideoSurface remoteVideoSurface;

public static void main(String[] args) throws Exception 
{
   setLookAndFeel();

   SwingUtilities.invokeLater(new Runnable() 
   {
      @Override
      public void run() 
      {
         new VideoClient().start();
      }
   });
}

public VideoClient() 
{
   mediaPlayerFactory = new MediaPlayerFactory("--no-video-title-show");
   remoteMediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();

   contentPane = new JPanel();
   contentPane.setBorder(new EmptyBorder(16, 16, 16, 16));
   contentPane.setLayout(new BorderLayout(16, 16));

   videoPanel = new JPanel();
   videoPanel.setLayout(new GridLayout(1, 2, 16, 0));

   remoteCanvas = new Canvas();
   remoteCanvas.setBackground(Color.black);
   remoteCanvas.setSize(320, 180);

   remoteStreamControls = new JPanel();
   remoteStreamControls.setLayout(new BoxLayout(remoteStreamControls, BoxLayout.X_AXIS));

   streamFromLabel = new JLabel("Stream From:");
   streamFromLabel.setDisplayedMnemonicIndex(7);
   remoteStreamControls.add(streamFromLabel);

   remoteStreamControls.add(Box.createHorizontalStrut(4));

   streamFromTextField = new JTextField();
   streamFromTextField.setFocusAccelerator('f');
   streamFromTextField.setColumns(12);
   remoteStreamControls.add(streamFromTextField);

   receiveButton = new JButton("Receive");
   receiveButton.setMnemonic('r');
   remoteStreamControls.add(receiveButton);

   receiveSnapshotButton = new JButton("Snap");
   receiveSnapshotButton.setMnemonic('a');
   remoteStreamControls.add(receiveSnapshotButton);

   remoteVideoSurface = mediaPlayerFactory.newVideoSurface(remoteCanvas);
   remoteMediaPlayer.setVideoSurface(remoteVideoSurface);

   remotePanel = new JPanel();
   remotePanel.setBorder(new TitledBorder("Remote"));
   remotePanel.setLayout(new BorderLayout(0, 8));
   remotePanel.add(remoteCanvas, BorderLayout.CENTER);
   remotePanel.add(remoteStreamControls, BorderLayout.SOUTH);

   videoPanel.add(remotePanel);

   contentPane.add(videoPanel, BorderLayout.CENTER);

   frame = new JFrame("video chat");
   frame.setContentPane(contentPane);
   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   frame.pack();

   receiveButton.addActionListener(new ActionListener() 
   {
      @Override
      public void actionPerformed(ActionEvent e) 
      {
         receive();
      }
   });

   receiveSnapshotButton.addActionListener(new ActionListener() 
   {
      @Override
      public void actionPerformed(ActionEvent e) 
      {
         remoteMediaPlayer.saveSnapshot();
      }
   });
}

private void start() 
{
   streamFromTextField.setText("230.0.0.1:5555");

   frame.setVisible(true);
}

private void receive() 
{
   String mrl = streamFromTextField.getText();
   remoteMediaPlayer.playMedia("rtsp://" + mrl, ":ffmpeg-threads=1", ":network-caching=1000");
}

}
