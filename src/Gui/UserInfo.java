package Gui;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.web.*;
import java.util.regex.*;
import java.util.*;
import javafx.geometry.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.*;
import javafx.scene.image.*;

public class UserInfo
{
private static String picPath = "http://140.112.18.211/exp1/userimg/";
private HBox hRoot;
private ImageView pic;
private Label idLabel;

public UserInfo(String username)
{
   Image i = new Image(picPath + username + ".jpg", 100, 50, true, true);
   //Image i = new Image("http://140.112.18.211/exp1/commonimg/default.jpg", 100, 50, true, true);
   pic = new ImageView(i);

   idLabel = new Label(username);
   idLabel.setPrefSize(100, 50);
   idLabel.setStyle("-fx-alignment: center;");

   hRoot = new HBox();
   hRoot.getChildren().addAll(pic, idLabel);
   hRoot.setStyle("-fx-border-color: #446688; -fx-border-width: 1px");
}

public HBox getLayout()
{
   return hRoot;
}
}
