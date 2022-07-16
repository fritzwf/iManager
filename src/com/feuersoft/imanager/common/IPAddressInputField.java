/*
 * Copyright (c) 2010, Fritz Feuerbacher.
 */
package com.feuersoft.imanager.common;

import javax.swing.*;

/**
 * Common IP Address control. This needs to be added to the Netbeans
 * pallette if you are going to use it with that IDE.
 * @author Fritz Feuerbacher
 */
public class IPAddressInputField
  extends JPanel
{
  private JSpinner firstSpinner;
  private JSpinner secondSpinner;
  private JSpinner thirdSpinner;
  private JSpinner fourthSpinner;

  public IPAddressInputField()
  {
    initGui();
  }

  @Override
  public void setEnabled(boolean enabled)
  {
    super.setEnabled(enabled);
    firstSpinner.setEnabled(enabled);
    secondSpinner.setEnabled(enabled);
    thirdSpinner.setEnabled(enabled);
    fourthSpinner.setEnabled(enabled);
  }

  private void initGui()
  {
    firstSpinner = new JSpinner();
    firstSpinner.setModel(new SpinnerNumberModel(127, 0, 255, 1));
    firstSpinner.setToolTipText("Set the IP Address first octet");
    firstSpinner.setEditor(new JSpinner.NumberEditor(firstSpinner));

    secondSpinner = new JSpinner();
    secondSpinner.setModel(new SpinnerNumberModel(0, 0, 255, 1));
    secondSpinner.setToolTipText("Set the IP Address first octet");
    secondSpinner.setEditor(new JSpinner.NumberEditor(secondSpinner));

    thirdSpinner = new JSpinner();
    thirdSpinner.setModel(new SpinnerNumberModel(0, 0, 255, 1));
    thirdSpinner.setToolTipText("Set the IP Address first octet");
    thirdSpinner.setEditor(new JSpinner.NumberEditor(thirdSpinner));

    fourthSpinner = new JSpinner();
    fourthSpinner.setModel(new SpinnerNumberModel(1, 0, 255, 1));
    fourthSpinner.setToolTipText("Set the IP Address first octet");
    fourthSpinner.setEditor(new JSpinner.NumberEditor(fourthSpinner));

    JLabel firstDot = new JLabel(".");
    JLabel secondDot = new JLabel(".");
    JLabel thirdDot = new JLabel(".");

    GroupLayout layout = new GroupLayout(this);
    setLayout(layout);

    layout.setHorizontalGroup(layout.createSequentialGroup()
      .addComponent(firstSpinner,45,45,45)
      .addGap(2)
      .addComponent(firstDot)
      .addGap(2)
      .addComponent(secondSpinner,45,45,45)
      .addGap(2)
      .addComponent(secondDot)
      .addGap(2)
      .addComponent(thirdSpinner,45,45,45)
      .addGap(2)
      .addComponent(thirdDot)
      .addGap(2)
      .addComponent(fourthSpinner,45,45,45));

    layout.setVerticalGroup(
      layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
        .addComponent(firstSpinner)
        .addComponent(firstDot)
        .addComponent(secondSpinner)
        .addComponent(secondDot)
        .addComponent(thirdSpinner)
        .addComponent(thirdDot)
        .addComponent(fourthSpinner));
  }

  /**
   * @return address The IPv4 address in the format of X.X.X.X where X is an
   *         <p>
   *         integer between 0 and 255.
   */
  public String getIPAddress()
  {
    int first = ((SpinnerNumberModel)firstSpinner.getModel())
                                                 .getNumber().intValue();
    int second =((SpinnerNumberModel)secondSpinner.getModel())
                                                 .getNumber().intValue();

    int third = ((SpinnerNumberModel)thirdSpinner.getModel())
                                                 .getNumber().intValue();

    int fourth = ((SpinnerNumberModel)fourthSpinner.getModel())
                                                 .getNumber().intValue();

    StringBuilder builder = new StringBuilder();
    builder.append(first).append('.')
      .append(second).append('.')
      .append(third).append('.')
      .append(fourth);

    return builder.toString();
  }

  /**
   * @param address the array to put the four octets in.
   * @return address The address from the parameter list is return with the
   *         <p>
   *         values of the four octets.  If address is null or length is less
   *         <p>
   *         than four, than a new array is created and return containing the
   *         <p>
   *         values of the four octets in the first four positions.
   */
  public byte[] getIPAddress(byte[] address)
  {
    if (address == null || address.length < 4)
    {
      address = new byte[4];
    }

    address[0] = ((SpinnerNumberModel)firstSpinner.getModel())
                                                  .getNumber().byteValue();
    address[1] = ((SpinnerNumberModel)secondSpinner.getModel())
                                                  .getNumber().byteValue();
    address[2] = ((SpinnerNumberModel)thirdSpinner.getModel())
                                                  .getNumber().byteValue();
    address[3] = ((SpinnerNumberModel)fourthSpinner.getModel())
                                                  .getNumber().byteValue();

    return address;
  }

  /**
   * Sets the four octet values of this component.
   *
   * @param address The IPv4 address in the format of X.X.X.X where X is an
   *                <p>
   *                integer between 0 and 255.
   * @throws IllegalArgumentException Thrown when the address does match the
   *                                  <p>
   *                                  standard IPv4 format of X.X.X.X where X
   *                                  is
   *                                  <p>
   *                                  an integer between 0 and 255.
   */
  public void setIPAddress(String address)
  {
    if (null != address && !address.isEmpty())
	{
		String[] octetStrings = address.split("\\.", 4);

	    if (octetStrings.length == 4)
	    {
	      int i = 0;
          Integer[] octets = new Integer[4];

          for (String lastValue : octetStrings)
	      {
	        try
	        {
	          Integer testInt = Integer.valueOf(lastValue);
              if (testInt >= 0 && testInt <= 255)
              {
                octets[i++] = testInt;
              }
              else
              {
	            //throw illegal argument exception message
	            String message = String.format(
	              "%s contains a value of %s that is not between 0 and 255",
	              address, octets[i]);
	            throw new IllegalArgumentException(message);
              }
	        }
	        catch (NumberFormatException nfe)
	        {
	          //throw illegal argument exception message
	          String message = String.format(
	            "%s contains a value of %s that is not an integer", address,
	            octets[i]);
	          throw new IllegalArgumentException(message);
	        }
	      }

	      ((SpinnerNumberModel)firstSpinner.getModel())
	                                       .setValue(octets[0]);
	      ((SpinnerNumberModel)secondSpinner.getModel())
	                                       .setValue(octets[1]);
	      ((SpinnerNumberModel)thirdSpinner.getModel())
	                                       .setValue(octets[2]);
	      ((SpinnerNumberModel)fourthSpinner.getModel())
	                                       .setValue(octets[3]);
	    }
	    else
	    {
	      //throw illegal argument exception message
	      String message = String.format(
	        "%s is not in the format x.x.x.x, where x is an integer between 0 and 255",
	        address);
	      throw new IllegalArgumentException(message);
	    }
    }
  }
}

