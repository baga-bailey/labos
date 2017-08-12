package uk.co.mafew.hephaestus.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;

public class SerialListener implements Runnable, SerialPortEventListener
{
	static CommPortIdentifier portId1;
	static CommPortIdentifier portId2;

	InputStream inputStream;
	OutputStream outputStream;
	SerialPort serialPort1, serialPort2;
	Thread readThread;
	protected String divertCode = "10";
	static String TimeStamp;

	public static void main(String[] args)
	{
		Enumeration ports = CommPortIdentifier.getPortIdentifiers();
		while (ports.hasMoreElements())
		{
			CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
			String type;
			switch (port.getPortType())
			{
			case CommPortIdentifier.PORT_PARALLEL:
				type = "Parallel";
				break;
			case CommPortIdentifier.PORT_SERIAL:
				type = "Serial";
				break;
			default: // / Shouldn't happen
				type = "Unknown";
				break;
			}
			System.out.println(port.getName() + ": " + type);
		}

		try
		{
			portId1 = CommPortIdentifier.getPortIdentifier("COM4");
			portId2 = CommPortIdentifier.getPortIdentifier("COM5");
			SerialListener reader = new SerialListener();
		}

		catch (Exception e)
		{
			TimeStamp = new java.util.Date().toString();
			System.out.println(TimeStamp + ": COM4 " + portId1);
			System.out.println(TimeStamp + ": COM5 " + portId2);
			System.out.println(TimeStamp + ": msg1 - " + e);
		}
	};

	public SerialListener()
	{
		try
		{
			TimeStamp = new java.util.Date().toString();
			serialPort1 = (SerialPort) portId1.open("ComControl", 2000);
			System.out.println(TimeStamp + ": " + portId1.getName() + " opened for scanner input");
			serialPort2 = (SerialPort) portId2.open("ComControl", 2000);
			System.out.println(TimeStamp + ": " + portId2.getName() + " opened for diverter output");

		}
		catch (PortInUseException e)
		{
		}
		try
		{
			inputStream = serialPort1.getInputStream();
		}
		catch (IOException e)
		{
		}
		try
		{
			serialPort1.addEventListener(this);
		}
		catch (TooManyListenersException e)
		{
		}
		serialPort1.notifyOnDataAvailable(true);
		try
		{

			serialPort1.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			serialPort1.setDTR(false);
			serialPort1.setRTS(false);

			serialPort2.setSerialPortParams(9600, SerialPort.DATABITS_7, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);

		}
		catch (UnsupportedCommOperationException e)
		{
		}

		readThread = new Thread(this);
		readThread.start();
	}

	public void run()
	{
		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException e)
		{
		}
	}

	public void serialEvent(SerialPortEvent event)
	{
		switch (event.getEventType())
		{
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			StringBuffer readBuffer = new StringBuffer();
			int c;
			try
			{
				while ((c = inputStream.read()) != 10)
				{
					if (c != 13)
						readBuffer.append((char) c);
				}
				String scannedInput = readBuffer.toString();
				TimeStamp = new java.util.Date().toString();
				System.out.println(TimeStamp + ": scanned input received:" + scannedInput);
				inputStream.close();
				if (scannedInput.substring(0, 1).equals("F"))
				{
					outputStream = serialPort1.getOutputStream();
					outputStream.write(divertCode.getBytes());
					System.out.println(TimeStamp + ": diverter fired");
					outputStream.close();
				}
				else
				{
					System.out.println(TimeStamp + ": diverter not diverted");
				}
			}
			catch (IOException e)
			{
			}

			break;
		}
	}
}
