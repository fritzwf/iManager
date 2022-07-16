/*
 * Copyright (c) 2011, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui;

import java.awt.Image;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fritz Feuerbacher
 */
public class ItemImageJPanel 
   extends javax.swing.JPanel
{
  private static final Logger log =
            LoggerFactory.getLogger(ItemImageJPanel.class);
  public static final String TITLE_NAME = "Item Image";
  
  private final java.util.TimerTask urlTimerTask = new urlAccessTimerTask();
  private final java.util.Timer urlTimer = new java.util.Timer();
  private final AtomicBoolean urlFound = new AtomicBoolean(false);
  private String strUrl = "";
  private Proxy proxy = null;
  private static final String strWait = "<html><h2>Retrieving image.  Please wait...</h2></html>";
  private static final String strFailed = "<html><h2>Unable to retrieve image.</h2></html>";

  /** Creates new form ItemImageJPanel
   * @param strUrl - The url to the image.
   * @param proxy - The proxy to use if required.
   */
  public ItemImageJPanel(String strUrl, Proxy proxy)
  {
    initComponents();
    this.proxy = proxy;
    this.strUrl = strUrl;
    urlTimer.schedule(urlTimerTask, 0);
    jLabelItemImage.setText(strWait);
  }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jProgressBarWait = new javax.swing.JProgressBar();
      jScrollPane1 = new javax.swing.JScrollPane();
      jLabelItemImage = new javax.swing.JLabel();

      jProgressBarWait.setIndeterminate(true);

      jScrollPane1.setViewportView(jLabelItemImage);

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addGap(70, 70, 70)
            .addComponent(jProgressBarWait, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(563, Short.MAX_VALUE))
         .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 463, Short.MAX_VALUE)
            .addComponent(jProgressBarWait, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
      );
   }// </editor-fold>//GEN-END:initComponents


   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JLabel jLabelItemImage;
   private javax.swing.JProgressBar jProgressBarWait;
   private javax.swing.JScrollPane jScrollPane1;
   // End of variables declaration//GEN-END:variables

  /**
   * Create a timer to retrieve a URL from the network.
   */
  protected class urlAccessTimerTask
    extends java.util.TimerTask
  {
    @Override
    public void run()
    {
      Image image = null;
      try
      {
        // Read picture from a URL
        URL url = new URL(strUrl);
        String protocol = url.getProtocol();
        
        if (protocol.equalsIgnoreCase("https"))
        {
           image = ImageIO.read(((HttpsURLConnection)url.openConnection(proxy)).getInputStream());
        }
        else
        if (protocol.equalsIgnoreCase("http"))
        {
           image = ImageIO.read(url.openConnection(proxy).getInputStream());
        }
        jLabelItemImage.setText("");
        jLabelItemImage.setIcon(new ImageIcon(image));
        urlFound.set(true);
        if (log.isDebugEnabled())
        {
          log.debug("Item image: " + strUrl);
        }
      }
      catch (Exception e)
      {
        log.error(e.getMessage() + ", for URL: " + strUrl);
        jLabelItemImage.setText(strFailed);
      }
      finally
      {
        urlFound.set(true);
        jProgressBarWait.setVisible(false);
        cancel();
      }
    }
  }
}
