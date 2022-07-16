/*
 * Copyright (c) 2018, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.ui.worker;

import com.feuersoft.imanager.persistence.HibernateDataManagerDyn;
import com.feuersoft.imanager.persistence.Item;
import com.feuersoft.imanager.persistence.ProductLine;
import com.feuersoft.imanager.persistence.StoreQuantity;
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
 public class NewOrderChartWorker 
                extends SwingWorker<Integer, Integer>
 {

   private final CloseableTabbedPane tabbedPane;   
   
   public NewOrderChartWorker(final CloseableTabbedPane tabbedPane)
   {
     this.tabbedPane = tabbedPane;
   }    
    
   @Override
   protected void process(List<Integer> chunks)
   {
      InvManagerMainFrame.setProgressBar(true, Math.min(chunks.get(chunks.size()-1), 100));
   }

   @Override
   protected Integer doInBackground() throws Exception
   {
     InvManagerMainFrame.setWorkingStatus(true, "Creating new order chart...");
     InvManagerMainFrame.setProgressBar(true, 0); 
     
     HibernateDataManagerDyn jpa = new HibernateDataManagerDyn();

     DefaultPieDataset dataset = new DefaultPieDataset();
     JFreeChart jfc;

     int progress = 10;
     publish(progress);
     for (ProductLine pl : jpa.getAllProductLines())
     {
       int newQty = 0;
       List<Item> items = jpa.getItemsByProductLine(pl);
       progress += 20;
       publish(progress);
       for (Item i : items)
       {
         for (StoreQuantity sqty : i.getStores().values())
         {
           newQty += sqty.getNewOrderCalc();
         }
       }
       
       String title = String.format("%s - %,d", pl.getProductLineName(), newQty);
       dataset.setValue(title, newQty);
     }
     jfc = ChartFactory.createPieChart("New Order Qty Per Product Line",
              dataset, true, true, false);

     PiePlot pp = (PiePlot)jfc.getPlot();
     pp.setSectionOutlinesVisible(false);
     pp.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
     pp.setNoDataMessage("No New Order Data");
     pp.setCircular(false);
     pp.setLabelGap(0.02);

     javax.swing.JPanel pc = new ChartPanel(jfc);
     tabbedPane.add("New Order Chart", pc);
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
