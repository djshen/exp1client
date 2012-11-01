package Gui;

import Client.*;

import javafx.application.Application;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.geometry.*;
import javafx.scene.layout.*;
import javafx.event.*;
import java.util.*;
 
public class MainGui extends Application 
{
final private Client client = new Client(this, "140.112.18.211", 22222);
public Stage stage;
private Group root;
private LoginPane loginPane;
private TabPane tabPane;
private MainPage mainPage;
public HashMap<String, RoomPage> rooms;
public HashMap<String, Tab> roomTabs;
private String username;
private void init(Stage primaryStage) 
{
   stage = primaryStage;
   root = new Group();
   primaryStage.setScene(new Scene(root));

   loginPane = new LoginPane(this);

   rooms = new HashMap<String, RoomPage>();
   roomTabs = new HashMap<String, Tab>();

   root.getChildren().addAll(loginPane.getLayout());

   //root.getChildren().remove(loginPane.getLayout());
   //root.getChildren().add(borderPane);
   //initMainPage();
}

public void initMainPage()
{
   root.getChildren().remove(loginPane.getLayout());

   BorderPane borderPane = new BorderPane();
   tabPane = new TabPane();
   tabPane.setMinSize(900, 640);
   tabPane.setSide(Side.TOP);
   tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
   Tab tab = new Tab();
   tab.setText("Main Page");
   mainPage = new MainPage(username, this, client);
   tab.setContent(mainPage.getLayout());
   tabPane.getTabs().addAll(tab);
   borderPane.setCenter(tabPane);
   root.getChildren().add(borderPane);
   stage.sizeToScene();
}
 
@Override 
public void start(Stage primaryStage) throws Exception 
{
   primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
   {
      public void handle(WindowEvent t)
      {
         System.err.println("close");
         Iterator<String> iter = rooms.keySet().iterator();
         while(iter.hasNext())
         {
            client.leaveRoom(iter.next());
         }
         client.leaveRoom("Main Page");
      }
   });
   init(primaryStage);
   primaryStage.show();
}

public static void main(String[] args) 
{
   launch(args);
}

public Client getClient()
{
   return client;
}

public Group getRoot()
{
   return root;
}

public void setUsername(String un)
{
   username = un;
}

public void addRoomTab(final String roomname, String[] ul)
{
   System.err.println("add tab " + roomname);
   RoomPage rp = new RoomPage(roomname, this, client, ul);
   try
   {
      rooms.put(roomname, rp);
      final Tab tab = new Tab();
      tab.setText(roomname);
      tab.setId(roomname);
      tab.setContent(rp.getLayout());
      roomTabs.put(roomname, tab);

      ContextMenu cm = new ContextMenu();
      MenuItem i = new MenuItem("Close");
      i.setOnAction(new EventHandler<ActionEvent>()
      {
         public void handle(ActionEvent t)
         {
            tabPane.getTabs().remove(tab);
            client.leaveRoom(roomname);
         }  
      });
      cm.getItems().add(i);
      tab.setContextMenu(cm);
      tabPane.getTabs().addAll(tab);
   }
   catch(Exception e)
   {
      System.err.println(e.toString());
      e.printStackTrace();
   }
}

public void removeRoomTab(String rn)
{
   try
   {
      rooms.remove(rn);
      tabPane.getTabs().remove(roomTabs.get(rn));
      roomTabs.remove(rn);
   }
   catch(Exception e){}
}

public RoomPage getRoomPage(String roomname)
{
   RoomPage rp = null;
   try
   {
      rp = rooms.get(roomname);
   }
   catch(Exception e)
   {
      System.err.println(e.toString());
      e.printStackTrace();
   }
   return rp;
}

public MainPage getMainPage()
{
   return mainPage;
}

public void showMessageDialog(final String msg)
{
   Stage dialogStage = new Stage();
   dialogStage.initModality(Modality.WINDOW_MODAL);
   dialogStage.setScene(new Scene(VBoxBuilder.create().children(new Text(msg)).alignment(Pos.CENTER).padding(new Insets(5)).build()));
   dialogStage.show();
}

public void getJoinReply(final String rn, final String un)
{
         Stage dialogStage = new Stage();
         dialogStage.initModality(Modality.WINDOW_MODAL);
         VBox vb = new VBox();
         Text text = new Text("User: " + un + " wants to join " + rn);
         Button ob = new Button("yes");
         ob.setOnAction(new EventHandler<ActionEvent>()
         {
            public void handle(ActionEvent t)
            {
               joinReply(rn, un, true);
            }
         });
         Button nb = new Button("no");
         nb.setOnAction(new EventHandler<ActionEvent>()
         {
            public void handle(ActionEvent t)
            {
               joinReply(rn, un, false);
            }
         });
         vb.getChildren().addAll(text, ob, nb);
         dialogStage.setScene(new Scene(vb));
         dialogStage.show();
}

public void joinReply(String rn, String un, boolean r)
{
   client.joinReply(rn, un, r);
}
}

class LoginPane
{
private MainGui mainGui;
private VBox vRoot;

private Label msgLabel;

private TextField usernameText;

private PasswordField passwordText;

private Button loginButton;

public LoginPane(MainGui mg)
{
   mainGui = mg;

   msgLabel = new Label();
   msgLabel.setPrefSize(200, 50);

   Label label1 = new Label("User Name: ");
   label1.setPrefSize(100, 50);
   
   usernameText = new TextField();
   usernameText.setPrefSize(100, 50);

   HBox hb1 = new HBox();
   hb1.setStyle("-fx-spacing: 5;");
   hb1.getChildren().addAll(label1, usernameText);

   Label label2 = new Label("Password: ");
   label2.setPrefSize(100, 50);

   passwordText = new PasswordField();
   passwordText.setPrefSize(100, 50);

   HBox hb2 = new HBox();
   hb2.setStyle("-fx-spacing: 5;");
   hb2.getChildren().addAll(label2, passwordText);

   loginButton = new Button("Login");
   loginButton.setPrefSize(100, 50);
   loginButton.setOnAction(new EventHandler<ActionEvent>()
   {
      public void handle(ActionEvent t)
      {
         Client client = mainGui.getClient();
         if(client.connect(usernameText.getText()))
         {
            mainGui.setUsername(usernameText.getText());
            mainGui.initMainPage();
         }
         else
         {
            msgLabel.setText("Login Error");
         }
      }
   });

   vRoot = new VBox();
   vRoot.setStyle("-fx-spacing: 5;");
   vRoot.setPrefSize(250, 180);
   vRoot.setAlignment(Pos.CENTER);
   vRoot.getChildren().addAll(hb1, hb2, loginButton);
}

public VBox getLayout()
{
   return vRoot;
}
}
