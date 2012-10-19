package Video;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import uk.co.caprica.vlcj.logger.Logger;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.runtime.x.LibXUtil;

import com.sun.jna.NativeLibrary;

public class VideoBase 
{
private static final String VLCJ_LOG_LEVEL = "INFO";
private static final String NATIVE_LIBRARY_SEARCH_PATH = null;
private static final String DUMP_NATIVE_MEMORY = "false";

static 
{
  if(null == System.getProperty("vlcj.log"))
  {
    System.setProperty("vlcj.log", VLCJ_LOG_LEVEL);
   }
   LibXUtil.initialise();

   if(null != NATIVE_LIBRARY_SEARCH_PATH) 
   {
      Logger.info("Explicitly adding JNA native library search path: '{}'", NATIVE_LIBRARY_SEARCH_PATH);
      NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), NATIVE_LIBRARY_SEARCH_PATH);
   }

   System.setProperty("jna.dump_memory", DUMP_NATIVE_MEMORY);
}

protected static final void setLookAndFeel() 
{
   String lookAndFeelClassName = null;
   LookAndFeelInfo[] lookAndFeelInfos = UIManager.getInstalledLookAndFeels();
   for(LookAndFeelInfo lookAndFeel : lookAndFeelInfos) 
   {
      if("Nimbus".equals(lookAndFeel.getName())) 
      {
         lookAndFeelClassName = lookAndFeel.getClassName();
      }
   }
   if(lookAndFeelClassName == null) 
   {
      lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
   }
   try
   {
      UIManager.setLookAndFeel(lookAndFeelClassName);
   }
   catch(Exception e) 
   {
   }
}
}
