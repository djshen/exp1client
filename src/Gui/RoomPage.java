package Gui;

import Client.*;
import Video.*;
import Ftp.*;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.web.*;
import java.util.regex.*;
import java.util.*;
import java.io.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.*;
import javafx.scene.image.*;

public class RoomPage 
{
public MainGui mainGui;
private Client client;

final public String roomname;

private HBox hRoot;

private Accordion leftLayout;

private VBox remoteLiveLayout;
private HashMap<String, RemoteLive> remoteLives;
private ScrollPane remoteLivePane;

private VBox userLayout;
private HashMap<String, UserInfo> users;
private ScrollPane userPane;
private TextField kickText;
private Button kickButton;

private Label filenameLabel;
private Button sendFileButton;
private VBox fileLayout;
private ScrollPane filePane;

private MessageArea messageArea;

public RoomPage(String rn, MainGui mg, Client c, String[] ul)
{
   mainGui = mg;
   client = c;
   roomname = rn;

   leftLayout = new Accordion();
   leftLayout.setStyle("-fx-border-color: #115599; -fx-border-width: 2px;");

   users = new HashMap<String, UserInfo>();
   userLayout = new VBox();
   userLayout.setStyle("-fx-spacing: 5;");
   kickText = new TextField();
   kickText.setPromptText("Kick user");
   kickText.setPrefSize(120, 25);
   kickButton = new Button("Kick");
   kickButton.setPrefSize(80, 25);
   kickButton.setOnAction(new EventHandler<ActionEvent>()
   {
      public void handle(ActionEvent t)
      {
         try
         {
            String un = kickText.getText();
            if(client.kickUser(roomname, un))
            {
               mainGui.showMessageDialog(un + " is kicked");
            }
         }
         catch(Exception e){}
      }
   });
   userPane = new ScrollPane();
   userPane.setContent(userLayout);
   userPane.setPrefWidth(210);
   userPane.setMinHeight(250);
   HBox hb1 = new HBox();
   hb1.getChildren().addAll(kickText, kickButton);
   userLayout.getChildren().add(hb1);
   if(ul != null)
   {
      for(String un : ul)
      {
         addUser(un);
      }
   }

   remoteLives = new HashMap<String, RemoteLive>();
   remoteLiveLayout = new VBox();
   remoteLiveLayout.setStyle("-fx-spacing: 5;");
   remoteLivePane = new ScrollPane();
   remoteLivePane.setContent(remoteLiveLayout);
   remoteLivePane.setPrefWidth(210);
   remoteLivePane.setMinHeight(250);

   fileLayout = new VBox();
   fileLayout.setStyle("-fx-spacing: 5;");
   filePane = new ScrollPane();
   filePane.setContent(fileLayout);
   filePane.setPrefWidth(210);
   filePane.setMinHeight(250);
   filenameLabel = new Label("");
   filenameLabel.setPrefSize(200, 25);
   filenameLabel.setStyle("-fx-background-color: #dddddd; -fx-border-color: #446688; -fx-border-width: 1px;");
   sendFileButton = new Button("Send file");
   sendFileButton.setPrefSize(100, 25);
   sendFileButton.setOnAction(new EventHandler<ActionEvent>()
   {
      public void handle(ActionEvent t)
      {
         FileChooser fileChooser = new FileChooser();
         final File file = fileChooser.showOpenDialog(mainGui.stage);
         final String localPath = file.getAbsolutePath();
         Platform.runLater(new Runnable()
         {
            @Override
            public void run()
            {
               try
               {
                  String[] s = { "-s", "-b", "localhost:2221", roomname, roomname, file.getName(), localPath };
                  FtpClient ftp = new FtpClient(s);
                  client.sendFileInfo(roomname, file.getName());
               }
               catch(Exception e){}
            }
         });
      }
   });
   HBox hb2 = new HBox();
   //hb2.getChildren().addAll(chooseFileButton, sendFileButton);
   hb2.getChildren().addAll(sendFileButton);
   VBox vb1 = new VBox();
   vb1.getChildren().addAll(filenameLabel, hb2, filePane);

   leftLayout.getPanes().add(new TitledPane("Local Live", new LocalLive(roomname).getLayout()));
   leftLayout.getPanes().add(new TitledPane("Remote Live List", remoteLivePane));
   leftLayout.getPanes().add(new TitledPane("File List", vb1));
   leftLayout.getPanes().add(new TitledPane("BGM", new Pane()));
   leftLayout.getPanes().add(new TitledPane("User", userPane));

   messageArea = new MessageArea(client, roomname);

   hRoot = new HBox();
   hRoot.setStyle("-fx-spacing: 5;");
   hRoot.getChildren().addAll(leftLayout, messageArea.getLayout());
}

public HBox getLayout()
{
   return hRoot;
}

public void displayMsg(String msg)
{
   messageArea.displayMsg(msg);
}

public void addUser(String un)
{
   try
   {
      UserInfo ui = new UserInfo(un);
      users.put(un, ui);
      userLayout.getChildren().add(ui.getLayout());
   }
   catch(Exception e){}
}

public void removeUser(String un)
{
   try
   {
      userLayout.getChildren().remove(users.get(un).getLayout());
      users.remove(un);
   }
   catch(Exception e){}
}

public void setFileInfo(String un, String fn)
{
   try
   {
      fileLayout.getChildren().add(new FileInfo(this, un, fn).getLayout());
   }
   catch(Exception e){}
}
}

class LocalLive
{
private VBox vRoot;
final private ToggleButton no;
private ToggleButton webcam;
private ToggleButton video;

private Button chooseFileButton;
private Button launchButton;

final private VideoServer currentVideo;
final private Stage currentStage = new Stage();

public LocalLive(String rn)
{
   //Group root = new Group();
   //Scene scene = new Scene(root);
   //currentStage.setScene(scene);
   //Platform.runLater(new Runnable()
   //{
     // @Override
      //public void run()
      //{
         currentVideo = new VideoServer(rn);
      //}
   //});
   //group.getChildren().add(currentVideo.getLayout());
   no = new ToggleButton("No");
   no.setPrefSize(60, 40);
   no.setOnAction(new EventHandler<ActionEvent>()
   {
      public void handle(ActionEvent t)
      {
         currentVideo.stop();
      }
   });
   webcam = new ToggleButton("Webcam");
   webcam.setPrefSize(80, 40);
   webcam.setOnAction(new EventHandler<ActionEvent>()
   {
      public void handle(ActionEvent t)
      {
         if(currentVideo != null)
         {
            currentVideo.start();
            //currentStage.hide();
         }
      }
   });
   //video = new ToggleButton("Video");
   //video.setPrefSize(60, 40);

   ToggleGroup group = new ToggleGroup();
   no.setToggleGroup(group);
   webcam.setToggleGroup(group);
   //video.setToggleGroup(group);
   group.selectToggle(no);

   chooseFileButton = new Button("Choose File");
   chooseFileButton.setPrefSize(100, 40);

   launchButton = new Button("Launch");
   launchButton.setPrefSize(100, 40);

   HBox hb = new HBox();
   hb.getChildren().addAll(no, webcam/*, video*/);

   vRoot = new VBox();
   vRoot.setStyle("-fx-spacing: 5; -fx-alignment: center; -fx-border-color: #446688; -fx-border-width: 1px;");
   vRoot.setPrefSize(200, 300);
   vRoot.getChildren().addAll(hb, /*chooseFileButton, */launchButton);
}

public VBox getLayout()
{
   return vRoot;
}
}

class RemoteLive
{
private HBox hRoot;
private Label username;
private ToggleButton closeButton;
private ToggleButton openButton;

private String streamPath;

public RemoteLive(String rn, String un)
{
   username = new Label(un);
   username.setPrefSize(100, 40);

   Image closeIcon = new Image("file:///src/img/X.png", 30, 30, true, true);
   closeButton = new ToggleButton("", new ImageView(closeIcon));
   closeButton.setPrefSize(30, 38);

   Image openIcon = new Image("file:///src/img/O.png", 30, 30, true, true);
   openButton = new ToggleButton("", new ImageView(openIcon));
   openButton.setPrefSize(30, 38);

   ToggleGroup group = new ToggleGroup();
   closeButton.setToggleGroup(group);
   openButton.setToggleGroup(group);
   group.selectToggle(closeButton);

   hRoot = new HBox();
   hRoot.setStyle("-fx-spacing: 5; -fx-border-color: #446688; -fx-border-width: 1px;");
   hRoot.setPrefSize(200, 40);
   hRoot.getChildren().addAll(username, closeButton, openButton);
}

public HBox getLayout()
{
   return hRoot;
}
}

class FileInfo
{
private HBox hRoot;
private Label username;
private Label filename;
private Button dlButton;

final private RoomPage roomPage;

public FileInfo(RoomPage rp, String un, final String fn)
{
   roomPage = rp;
   username = new Label("By: " + un);
   username.setPrefSize(115, 25);

   filename = new Label("File: " + fn);
   filename.setPrefSize(115, 25);

   dlButton = new Button("Download");
   dlButton.setPrefSize(85, 48);
   dlButton.setOnAction(new EventHandler<ActionEvent>()
   {
      public void handle(ActionEvent t)
      {
         FileChooser fileChooser = new FileChooser();
         final File file = fileChooser.showSaveDialog(roomPage.mainGui.stage);
         final String localPath = file.getAbsolutePath();
         Platform.runLater(new Runnable()
         {
            @Override
            public void run()
            {
               try
               {
						System.err.println(fn);
                  String[] s = { "-b", "localhost:2221", roomPage.roomname, roomPage.roomname, fn, localPath };
                  FtpClient ftp = new FtpClient(s);
                  roomPage.mainGui.showMessageDialog("File: " + fn + " is downloaded");
               }
               catch(Exception e){}
            }
         });
      }
   });

   VBox vb = new VBox();
   vb.getChildren().addAll(username, filename);

   hRoot = new HBox();
   hRoot.setStyle("-fx-spacing: 5; -fx-border-color: #446688; -fx-border-width: 1px;");
   hRoot.setPrefSize(200, 50);
   hRoot.getChildren().addAll(vb, dlButton);
}

public HBox getLayout()
{
   return hRoot;
}
}
