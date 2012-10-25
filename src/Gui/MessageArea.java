package Gui;

import Client.*;
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
import javafx.scene.effect.*;

public class MessageArea
{
private Client client;
private HTMLEditor htmlEditor = null;
private final String INITIAL_TEXT = "<html><body></body></html>";
private WebView browser;  
private WebEngine webEngine;  
private Button paste;
private VBox vRoot;
private String roomname;

private final String imgRoot = "http://140.112.18.211/exp1/commonimg/";
private final String[] imgPath = 
{
   imgRoot + "1.png",
   imgRoot + "2.png",
   imgRoot + "3.png",
   imgRoot + "4.png",
   imgRoot + "5.png",
   imgRoot + "6.png",
   imgRoot + "7.png",
   imgRoot + "8.png",
   imgRoot + "9.png",
   imgRoot + "10.png",
   imgRoot + "11.png",
   imgRoot + "12.png",
   imgRoot + "13.png",
   imgRoot + "14.png",
   imgRoot + "15.png",
   imgRoot + "16.png",
   imgRoot + "17.png",
   imgRoot + "18.png"
};

public MessageArea(Client c, String rn)
{
   client = c;
   roomname = rn;
   vRoot = new VBox();

   vRoot.setPadding(new Insets(8, 8, 8, 8));
   vRoot.setSpacing(5);

   browser = new WebView();
   browser.setPrefSize(700, 400);
   webEngine = browser.getEngine();
   webEngine.load("file:///home/jack1/eclipse_workspace/test2/src/index.html");
   VBox vb1 = new VBox();
   vb1.getChildren().add(browser);
   vb1.setStyle("-fx-border-color: #115599; -fx-border-width: 2px;");

   vRoot.getChildren().add(vb1);

   htmlEditor = new HTMLEditor();
   htmlEditor.setPrefSize(600, 240);
   htmlEditor.setHtmlText(INITIAL_TEXT);
   VBox vb2 = new VBox();
   vb2.getChildren().add(htmlEditor);
   vb2.setStyle("-fx-border-color: #115599; -fx-border-width: 2px;");

   Button sendButton = new Button("Send");
   sendButton.setPrefSize(100, 240);
   sendButton.setOnAction(new EventHandler<ActionEvent>() 
   {
      @Override
      public void handle(ActionEvent arg0) 
      {
         String text = htmlEditor.getHtmlText();
         text = text.replaceFirst("<html><head></head><body contenteditable=\"true\">","");
         text = text.replaceFirst("</body></html>","");
         text = text.replaceAll("\"'", "\"");
         text = text.replaceAll("'\"", "\"");
         text = text.replaceAll("'", "\"");
         try
         {
            if(roomname.equals("Main Page"))
            {
               System.err.println("mp");
               client.sendToMainRoom(text);
            }
            else
            {
               client.sendToRoom(roomname, text);
            }
         }
         catch(Exception e)
         {
            System.err.println(e.toString());
            e.printStackTrace();
         }
         htmlEditor.setHtmlText(INITIAL_TEXT);
      }
   });
   VBox vb3 = new VBox();
   vb3.getChildren().add(sendButton);
   vb3.setStyle("-fx-border-color: #115599; -fx-border-width: 2px;");

   HBox hb = new HBox();
   hb.setStyle("-fx-spacing: 5");
   hb.getChildren().addAll(vb2, vb3);
   vRoot.getChildren().add(hb);

   Platform.runLater(new Runnable()
   {
      @Override
      public void run()
      {
         adjust();
      }
   });
}

public VBox getLayout()
{
   return vRoot;
}

public void linkPaste(Node n, Pattern p, int d)
{
   if(n instanceof ImageView)
   {
      ImageView v = (ImageView) n;
      String url = v.getImage().impl_getUrl();
      if(url != null && p.matcher(url).matches())
      {
         paste = (Button) v.getParent().getParent();
         paste.setVisible(false);
         paste.setManaged(false);
      }
   }
   if(n instanceof Parent)
   {
      for(Node c : ((Parent) n).getChildrenUnmodifiable())
      {
         linkPaste(c, p, d+1);
      }
   }
}

public void hideImageNodesMatching(Node node, Pattern imageNamePattern, int depth) 
{
   if (node instanceof ImageView) 
   {
      ImageView imageView = (ImageView) node;
      String url = imageView.getImage().impl_getUrl();
      if (url != null && imageNamePattern.matcher(url).matches()) 
      {
         Node button = imageView.getParent().getParent();
         button.setVisible(false); 
         button.setManaged(false);
      }
   }
   if (node instanceof Parent) 
      for (Node child : ((Parent) node).getChildrenUnmodifiable()) 
         hideImageNodesMatching(child, imageNamePattern, depth + 1);
}

public void adjust()
{ 
   Node seperator = htmlEditor.lookup(".separator");
   seperator.setVisible(false); seperator.setManaged(false);
   linkPaste(htmlEditor, Pattern.compile(".*Paste.*"), 0);
   hideImageNodesMatching(htmlEditor, Pattern.compile(".*(Cut|Copy).*"), 0);
   Node node = htmlEditor.lookup(".top-toolbar");
   if (node instanceof ToolBar) 
   {
      ToolBar bar = (ToolBar) node;
      for(final String img : imgPath)
      {
         ImageView graphic = new ImageView(new Image(img, 30, 30, true, true));
         graphic.setEffect(new DropShadow());
         Button imgButton = new Button("", graphic);
         bar.getItems().add(imgButton);
         imgButton.setOnAction(new EventHandler<ActionEvent>() 
         {
            public void handle(ActionEvent t) 
            {
               Clipboard board = Clipboard.getSystemClipboard();
               ClipboardContent c = new ClipboardContent();
               c.putHtml("<img src='" + img + "' width='30' height='30'/>");
               String o;
               if(board.hasHtml())
               {
                  o = board.getHtml();
               }
               else 
               {
                  o = new String();
               }
               board.setContent(c);
               paste.fire();
               c.putHtml(o);
               board.setContent(c);
               htmlEditor.requestFocus();
            }
         });
      }
   }
}

public void displayMsg(String msg)
{
   webEngine.executeScript("insertText('"+msg+"')");
}
}
