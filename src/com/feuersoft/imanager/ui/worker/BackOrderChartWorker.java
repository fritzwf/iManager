/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui.worker;

import com.feuersoft.imanager.persistence.HibernateDataManagerDyn;
import com.feuersoft.imanager.persistence.Item;
import com.feuersoft.imanager.persistence.ProductLine;
import com.feuersoft.imanager.persistence.ProductVendor;
import com.feuersoft.imanager.ui.CloseableTabbedPane;
import com.feuersoft.imanager.ui.InvManagerMainFrame;
import java.awt.Font;
import java.util.List;
import javax.swing.SwingWorker;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

/**
 * @author Fritz Feuerbacher
 */
 public class BackOrderChartWorker 
                extends SwingWorker<Integer, Integer>
 {

   private final CloseableTabbedPane tabbedPane;
   private final ProductVendor vendor;
   
   public BackOrderChartWorker(final CloseableTabbedPane tabbedPane,
                               final ProductVendor vendor)
   {
     this.tabbedPane = tabbedPane;
     this.vendor = vendor;
   }    
    
   @Override
   protected void process(List<Integer> chunks)
   {
      InvManagerMainFrame.setProgressBar(true, Math.min(chunks.get(chunks.size()-1), 100));
   }

   @Override
   protected Integer doInBackground() throws Exception
   {
     InvManagerMainFrame.setWorkingStatus(true, "Creating backorder chart...");
     InvManagerMainFrame.setProgressBar(true, 0);
     
     HibernateDataManagerDyn jpa = new HibernateDataManagerDyn();

     DefaultPieDataset dataset = new DefaultPieDataset();
     JFreeChart jfc;

     int progress = 10;
     publish(progress);
     
     for (ProductLine prodLine : jpa.getProductLines(vendor))
     {
       int newQty = 0;
       List<Item> items = jpa.getItemsByProductLine(prodLine);
       progress += 20;
       publish(progress);
       for (Item i : items)
       {
         if (i.isBackOrdered())
         {
           newQty++;
         }
       }
       String title = String.format("%s - %,d", prodLine, newQty);
       dataset.setValue(title, newQty);
     }

     jfc = ChartFactory.createPieChart("Number of Backordered for " 
              + vendor.getProductVendorName(),
              dataset, true, true, false);

     PiePlot pp = (PiePlot)jfc.getPlot();
     pp.setSectionOutlinesVisible(false);
     pp.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
     pp.setNoDataMessage("No Backordered Data Found");
     pp.setCircular(false);
     pp.setLabelGap(0.02);

     javax.swing.JPanel pc = new ChartPanel(jfc);
     tabbedPane.add("Backordered Chart", pc);
     tabbedPane.setSelectedComponent(pc);
     
     InvManagerMainFrame.setWorkingStatus(false, null);

     return 1;     
   }
   
   @Override
   protected void done()
   {
     InvManagerMainFrame.setProgressBar(false, 0);
   }
 }
