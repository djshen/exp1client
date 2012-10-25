package Gui;

import Client.*;
import Ftp.*;

import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.web.*;
import java.util.regex.*;
import java.util.*;
import java.io.*;
import javafx.geometry.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.*;
import javafx.scene.image.*;
import javafx.event.*;

public class MainPage 
{
final private MainGui mainGui;
final private Client client;

private HBox hRoot;

private static String picPath = "http://140.112.18.211/exp1/userimg/";
private Button pictureButton;
private Label idLabel;

private TextField createRoomname;
private Button createRoomButton;

private VBox roomLayout;
private HashMap<String, RoomInfo> rooms;
private ScrollPane roomPane;

private TextField secretTo;
private Button secretToButton;
private TextField secretToText;

private VBox userLayout;
private HashMap<String, UserInfo> users;
private ScrollPane userPane;

private MessageArea messageArea;

public MainPage(String username, MainGui mg, Client c)
{
   mainGui = mg;
   client = c;

   //Image i = new Image(picPath + username + ".jpg");
   Image i = new Image("http://140.112.18.211/exp1/commonimg/img1.jpg", 200, 150, true, true);
   pictureButton = new Button("", new ImageView(i));
   pictureButton.setPrefSize(200, 150);
   pictureButton.setOnAction(new EventHandler<ActionEvent>()
   {
      public void handle(ActionEvent t)
      {
         FileChooser fc = new FileChooser();
         final File file = fc.showOpenDialog(mainGui.stage);
         final String localPath = file.getAbsolutePath();
         Platform.runLater(new Runnable()
         {
            @Override
            public void run()
            {
               try
               {
                  String[] s = { "-s", "-b", "localhost:2221", "IMG", "IMG", client.getName() + ".jpg", localPath };
                  FtpClient ftp = new FtpClient(s);
                  //client.picNeedUpdate();
               }
               catch(Exception e){}
            }
         });
      }
   });

   idLabel = new Label("Name: " + username);
   idLabel.setPrefSize(200, 40);
   idLabel.setStyle("-fx-alignment: center;");
   VBox vb1 = new VBox();
   vb1.setStyle("-fx-spacing: 5; -fx-border-color: #115599; -fx-border-width: 2px");
   vb1.getChildren().addAll(pictureButton, idLabel);

   createRoomname = new TextField();
   createRoomname.setPromptText("Room Name");
   createRoomname.setPrefSize(120, 25);
   createRoomButton = new Button("Create");
   createRoomButton.setPrefSize(80, 25);
   createRoomButton.setOnAction(new EventHandler<ActionEvent>()
   {
      public void handle(ActionEvent t)
      {
         if(!client.createRoomRequest(createRoomname.getText()))
         {
            createRoomname.setText("Error");
         }
         else
         {
            try
            {
               RoomInfo ri = new RoomInfo(createRoomname.getText(), client);
               rooms.put(createRoomname.getText(), ri);
               //roomLayout.getChildren().add(ri.getLayout());
            }
            catch(Exception e){}
         }
      }
   });
   HBox hb1 = new HBox();
   hb1.getChildren().addAll(createRoomname, createRoomButton);
   hb1.setStyle("-fx-spacing: 5; -fx-border-color: #115599; -fx-border-width: 2px");

   rooms = new HashMap<String, RoomInfo>();
   roomLayout = new VBox();
   roomLayout.setStyle("-fx-spacing: 5;");
   roomPane = new ScrollPane();
   roomPane.setStyle("-fx-border-color: #115599; -fx-border-width: 2px;");
   roomPane.setContent(roomLayout);
   roomPane.setPrefWidth(200);
   roomPane.setMinHeight(180);

   secretTo = new TextField();
   secretTo.setPromptText("User Name");
   secretTo.setPrefSize(120, 25);
   secretToText = new TextField();
   secretToText.setPrefSize(200, 25);
   secretToText.setPromptText("Message");
   secretToButton = new Button("Secret");
   secretToButton.setPrefSize(80, 25);
   secretToButton.setOnAction(new EventHandler<ActionEvent>()
   {
      public void handle(ActionEvent t)
      {
         try
         {
            client.sendToUser(secretTo.getText(), secretToText.getText());
            displayMsg("You say to " + secretTo.getText() + " : " + secretToText.getText());
         }
         catch(Exception e){}
      }
   });
   HBox hb2 = new HBox();
   hb2.getChildren().addAll(secretTo, secretToButton);
   hb2.setStyle("-fx-spacing: 5;");
   VBox vb3 = new VBox();
   vb3.setStyle("-fx-spacing: 5; -fx-border-color: #115599; -fx-border-width: 2px");
   vb3.getChildren().addAll(hb2, secretToText);

   users = new HashMap<String, UserInfo>();
   userLayout = new VBox();
   userLayout.setStyle("-fx-spacing: 5;");
   userPane = new ScrollPane();
   userPane.setStyle("-fx-border-color: #115599; -fx-border-width: 2px;");
   userPane.setContent(userLayout);
   userPane.setPrefWidth(200);
   userPane.setMinHeight(180);

   messageArea = new MessageArea(client, "Main Page");

   VBox vb2 = new VBox();
   vb2.setStyle("-fx-spacing: 5;");
   vb2.getChildren().addAll(vb1, hb1, roomPane, vb3, userPane);

   hRoot = new HBox();
   hRoot.setStyle("-fx-spacing: 5;");
   hRoot.getChildren().addAll(vb2, messageArea.getLayout());
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
      userLayout.getChildren().remove(users.get(un));
      users.remove(un);
   }
   catch(Exception e){}
}

public void addRoom(String rn)
{
   try
   {
      RoomInfo ri = new RoomInfo(rn, client);
      rooms.put(rn, ri);
      roomLayout.getChildren().add(ri.getLayout());
   }
   catch(Exception e){}
}
}

class RoomInfo
{
private HBox hRoot;
private Label roomname;
private Button joinButton;

public RoomInfo(final String rn, final Client c)
{
   roomname = new Label(rn);
   roomname.setPrefSize(150, 48);
   roomname.setStyle("-fx-alignment: center;");
   joinButton = new Button(new String("Join"));
   joinButton.setPrefSize(50, 48);
   joinButton.setOnAction(new EventHandler<ActionEvent>()
   {
      public void handle(ActionEvent t)
      {
         c.joinRoomRequest(rn);
      }
   });

   hRoot = new HBox();
   hRoot.getChildren().addAll(roomname, joinButton);
   hRoot.setPrefSize(200, 50);
   hRoot.setStyle("-fx-border-color: #446688; -fx-border-width: 1px");
}

public HBox getLayout()
{
   return hRoot;
}
}
