package uk.co.mafew.messaging.email;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

import java.util.Enumeration;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Xmail
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		try
		{
		Xmail xm;
		Properties props = new Properties();
	    props.setProperty("mail.imap.partialfetch","false");
		Session session = Session.getDefaultInstance(props, null);
		File file=new File("C:\\Users\\jbailey1\\Desktop\\FW  FAO SARA (urgent) -99123 Escatec .msg");
        FileInputStream fis=new FileInputStream(file);
        MimeMessage mess=new MimeMessage(session,fis);
        
		
			xm = new Xmail(mess);
			System.out.print(xm.toString());
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private Message message=null;
	  private String boundary="";
	  private Document doc=null;
	  
	  public Xmail()
	  {
	  }
	  
	  public Xmail(MimeMessage mess) throws SAXException
	  {
	    this.message=mess;
	    doc=this.formatToXMTP(message);
	  }
	  
	  public Xmail(String str) throws SAXException
	  {
	    this.message=buildEmail(str);
	  }
	  
	  public Xmail(Document doc)
	  {
	    this.message=buildEmail(doc);
	  }
	  
	  public void setMessage(Message message)
	  {
	    this.message=message;
	    doc=this.formatToXMTP(message);
	  }
	  
	  public Message getMessage()
	  {
	    return this.message;
	  }
	  
	  public Document getAsDoc()
	  {
	    return this.doc;
	  }
	  
	  public String toString()
	  {
	    return this.convertToString(doc);
	  }
	  
	  public Document formatToXMTP(Message message)
	  {
	    
	    try
	    {
	      DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	      DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
	      doc=domBuilder.newDocument();
	      
	      Element MIMEelem=doc.createElement("MIME");
	      //MIMEelem.setAttribute("xmlns:mime","http://www.grovelogic.com/xmtp");
	      //MIMEelem.setAttribute("xmlns","http://www.grovelogic.com/xmtp");
	      
	      doc.appendChild(MIMEelem);
	    }
	    catch(Exception e)
	    {
	      e.printStackTrace();
	    }
	    
	    try
	    {
	      addHeaders(message.getAllHeaders(),doc.getDocumentElement());
	    }
	    catch (MessagingException e)
	    {
	      e.printStackTrace();
	    }
	    addParts();
	 
	    //System.out.println("MESSAGE FORMATTED TO XML");
	    //System.out.println(convertToString(doc)); 
	    
	    return doc;   
	  }
	  
	  private void addParts()
	  {
	      //Element BODYElem=doc.createElement("mime:BODY");
	      Element BODYElem=doc.createElement("BODY");
	      Element partsElem=doc.createElement("Parts");
	      Element MIMEElem;
	      Element childBODYElem;
	      Part messagePart=message; 
	      if (boundary.compareTo("")!=0)
	      {
	        try
	        {
	          Object content=messagePart.getContent();
	          for(int i=0;i < ((Multipart)content).getCount(); i++)
	          { 
	            MIMEElem=addMIMEElement();
	            partsElem.appendChild(MIMEElem);
	            messagePart=((Multipart)content).getBodyPart(i);
	            addHeaders(messagePart.getAllHeaders(),MIMEElem);
	            //childBODYElem=doc.createElement("mime:BODY");
	            childBODYElem=doc.createElement("BODY");
	            childBODYElem.appendChild(doc.createTextNode(getBody((messagePart)).replaceAll("&","&amp;").replaceAll("<","&lt;")));
	            MIMEElem.appendChild(childBODYElem);
	            BODYElem.appendChild(partsElem);
	            doc.getDocumentElement().appendChild(BODYElem);
	            
	          }
	        }
	        catch (IOException e)
	        {
	          e.printStackTrace();
	        }
	        catch (MessagingException e)
	        {
	          e.printStackTrace();
	        }
	      }
	      else
	      {
	        //childBODYElem=doc.createElement("mime:BODY");
	        MIMEElem=addMIMEElement();
	        partsElem.appendChild(MIMEElem);
	        childBODYElem=doc.createElement("BODY");
	        childBODYElem.appendChild(doc.createTextNode(getBody((messagePart)).replaceAll("&","&amp;").replaceAll("<","&lt;")));
	        MIMEElem.appendChild(childBODYElem);
	        BODYElem.appendChild(partsElem);
	        doc.getDocumentElement().appendChild(BODYElem);
	      }
	  }
	  
	  public String getBody(Message message)
	  {
	    ByteArrayOutputStream os = new ByteArrayOutputStream();
	    try
	    {      
	      message.writeTo(os);
	    }
	    catch (IOException e)
	    {
	      e.printStackTrace();
	    }
	    catch (MessagingException e)
	    {
	      e.printStackTrace();
	    }
	    return retrieveBody(os);
	    
	  }
	  
	  private String getBody(Part messagePart )
	  {
	    ByteArrayOutputStream os = new ByteArrayOutputStream();
	    try
	    {
	      messagePart.writeTo(os);
	    }
	    catch (IOException e)
	    {
	      e.printStackTrace();
	    }
	    catch (MessagingException e)
	    {
	      e.printStackTrace();
	    }
	    
	    return retrieveBody(os);
	    
	  }
	  
	  private String retrieveBody(ByteArrayOutputStream os)
	  {
	    String line="";
	    char[] chArray;
	    try
	    {
	      
	      ByteArrayInputStream is=new ByteArrayInputStream(os.toByteArray());
	      System.out.println(os.size());
	      BufferedReader inputStream = new BufferedReader(new InputStreamReader(is));
	      
	      String temp="";int counter=0;
	      do
	      {
	        temp=inputStream.readLine();
	        line+=temp;counter++;
	      } while(temp.length()!=0);
	      System.out.println("line="+line);
	      System.out.println("length="+new Integer(line.length()).toString());
	      System.out.println("lines="+new Integer(counter).toString());
	      chArray=new char[os.size()-line.length()-(counter*2)];
	      inputStream.read(chArray,0,os.size()-line.length()-(counter*2));
	      line =new String(chArray);
	      //System.out.println(line);
	      is.close();
	      inputStream.close();
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    try
	    {
	      os.close();
	    }
	    catch (IOException e)
	    {
	      
	    }
	    return line;
	  }
	  
	  private Element addMIMEElement()
	  {
	    Element MIMEelem=doc.createElement("MIME");
	    //MIMEelem.setAttribute("xmlns:mime","http://www.grovelogic.com/xmtp");
	    //MIMEelem.setAttribute("xmlns","urn:xpository:xmtp");
	    return MIMEelem;    
	  }
	  
	    
	  private void addHeaders(Enumeration<Header> headers,Element MIMEelem)
	  {
	    try
	    {
	      String head;

	      while (headers.hasMoreElements())
	      {
	        Header header=headers.nextElement();
	        
	        Element elem=doc.createElement(header.getName());
	        head=header.getValue().replaceAll("\"","").replaceAll("\\r","");
	       
	        if (header.getName().compareTo("Content-Type")==0 || header.getName().compareTo("Content-Disposition")==0)
	        {
	          elem=formatAttributes(elem,head);
	        }
	        else
	        {
	          Node node=doc.createTextNode(head);
	          elem.appendChild(node);
	        }
	        //System.out.println(convertToString(doc));
	        if (header.getName().compareTo("From")==0 || header.getName().compareTo("To")==0 || header.getName().compareTo("Subject")==0)
	        {
	          Element refElem=(Element)(MIMEelem.getFirstChild());
	          refElem.getParentNode().insertBefore(elem,refElem);
	        }
	        else
	        {
	          MIMEelem.appendChild(elem);
	        }
	       
	      }
	    }
	    catch(Exception e)
	    {
	      e.printStackTrace();
	      System.out.println("***ERROR IN ADDHEADERS****"+this.convertToString(doc)+"***************");
	    }
	 
	  }
	  
	  private Element formatAttributes(Element elem,String cType)
	  {
	    String[] strs;
	    strs=cType.split(";");
	    
	    Node node=doc.createTextNode(strs[0]);
	    for(int i=1;i<strs.length;i++)
	    {
	      strs[i]=strs[i].replaceAll("\\n","");
	      elem.setAttribute(strs[i].substring(0,strs[i].indexOf("=")).trim()
	                        ,"\""+strs[i].substring(strs[i].indexOf("=")+1,strs[i].length())+"\"");
	                        
	      if(strs[i].substring(0,strs[i].indexOf("=")).trim().compareTo("boundary")==0)
	      {
	        boundary=strs[i].substring(strs[i].indexOf("=")+1,strs[i].length());
	      }
	    }
	    elem.appendChild(node);
	    return elem;
	  }
	  
	  private MimeMessage buildEmail(String contents)
	  {System.out.println("*****************STARTED BUILDING EMAIL**************");
	    //System.out.println(contents);
	    MimeMessage message=null;
	    try
	    {
	      DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	      DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
	      Document doc=domBuilder.parse(new InputSource(new StringReader(contents)));
	      message=buildEmail(doc);
	    }
	    catch(Exception e)
	    {
	      e.printStackTrace();
	    }
	    
	    return message;
	  }
	  
	    private MimeMessage buildEmail(Document doc)
	    {
	    System.out.println("**************STARTED buildEmail");
	    this.doc=doc;
	    MimeMessage message=null;
	    try
	    {
	      Properties props = System.getProperties();
	      props.put("mail.smtp.host", "exchange"); 
	      Session session = Session.getInstance(props, null); 
	      
	      // create a message 
	      message = new MimeMessage(session); 
	      Multipart mp = new MimeMultipart();
	     
	      // create the headers
	      NodeList list=doc.getDocumentElement().getChildNodes();
	      addHeaders(list,message);
	      
	      Enumeration headers=message.getAllHeaders();
	      while (headers.hasMoreElements())
	      {
	        Header header=(Header)headers.nextElement();
	        System.out.println("BEFORE PARTS ADDED-"+header.getName()+"="+header.getValue());
	      }
	      //Add parts
	      NodeList partsList=doc.getElementsByTagName("Parts").item(0).getChildNodes();
	      int noParts=partsList.getLength();
	      
	      MimeBodyPart part=null;
	      for (int i=0;i<noParts;i++)
	      {
	       if(partsList.item(i).getNodeType()==Node.ELEMENT_NODE)
	       {
	         part=addBody(partsList.item(i).getChildNodes());
	         mp.addBodyPart(part);
	       }
	      }
	      
	      headers=message.getAllHeaders();
	      while (headers.hasMoreElements())
	      {
	        Header header=(Header)headers.nextElement();
	        System.out.println("AFTER PARTS ADDED-"+header.getName()+"="+header.getValue());
	      }
	      
	      message.setContent(mp);
	      
	      headers=message.getAllHeaders();
	      while (headers.hasMoreElements())
	      {
	        Header header=(Header)headers.nextElement();
	        System.out.println("AFTER SET CONTENT-"+header.getName()+"="+header.getValue());
	      }       
	             // send the message 
	  
	      //Set the address to me for testing
	      InternetAddress[] address = { new InternetAddress("baileyj@walsall.gov.uk") }; 
	      message.setRecipients(Message.RecipientType.TO, address); 

	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    return message;
	  }
	  
	 
	  private void addHeaders(NodeList list,MimeMessage message)
	  {
	    String header,value;
	      try
	      {
	        int length=list.getLength();
	        
	        for (int i=0;i<length;i++)
	        {
	          if(list.item(i).getNodeType()==Node.ELEMENT_NODE && list.item(i).getNodeName().compareTo("BODY")!=0)
	          {
	            header=list.item(i).getNodeName();
	            try
	            {
	              value=list.item(i).getFirstChild().getNodeValue().replaceAll(";","-");
	            }
	            catch (Exception e)
	            {
	              value="";
	            }
	            if (list.item(i).getAttributes().getLength()>0)
	            {
	              NamedNodeMap attList;
	              attList=list.item(i).getAttributes();
	              for(int j=0;j<attList.getLength();j++)
	              {
	                Attr attr=(Attr)attList.item(j);
	                value+=";\n"+attr.getName()+"="+attr.getNodeValue();
	              }
	            }
	            message.setHeader(header,value);
	            System.out.println(header+"= "+value);
	          }
	        }
	      }
	      catch (MessagingException e)
	      {
	        e.printStackTrace();
	      }
	  }
	  
	  private MimeBodyPart addBody(NodeList list)
	  {
	    MimeBodyPart part=new MimeBodyPart();
	    String body="";
	    DataSource ds=null;
	    String encoding="";
	    InputStream is = null;
	      try
	      {
	        
	        int length=list.getLength();
	        
	        //Loop through tags and add the body as Content-Transfer-Encoding
	        for (int i=0;i<length;i++)
	        {
	          if(list.item(i).getNodeType()==Node.ELEMENT_NODE && list.item(i).getNodeName().compareTo("Content-Transfer-Encoding")==0)
	          {
	            encoding=list.item(i).getFirstChild().getNodeValue();
	            part.setHeader(list.item(i).getNodeName(),/*body*/encoding);
	          }
	        }
	        
	        //Loop through tags and add the body as text/plain
	        for (int i=0;i<length;i++)
	        {
	          if(list.item(i).getNodeType()==Node.ELEMENT_NODE && list.item(i).getNodeName().compareTo("BODY")==0)
	          {
	            try
	            {
	              body=list.item(i).getFirstChild().getNodeValue();
	            }
	            catch(Exception e)
	            {
	              body="";
	            }
	             is =(InputStream)(new ByteArrayInputStream(body.getBytes()));
	            if(encoding.compareTo("")!=0)
	              is=MimeUtility.decode(is,encoding);
	            ds = new ByteArrayDataSource(is,"text/plain");
	            
	          }
	          part.setDataHandler(new DataHandler(ds));
	        }
	        //Loop again adding the headers
	        for (int i=0;i<length;i++)
	        {
	          if(list.item(i).getNodeType()==Node.ELEMENT_NODE && 
	             list.item(i).getNodeName().compareTo("BODY")!=0 &&
	             list.item(i).getNodeName().compareTo("Content-Transfer-Encoding")!=0)
	          {
	              body=list.item(i).getFirstChild().getNodeValue();
	              
	              if (list.item(i).getAttributes().getLength()>0)
	              {
	                NamedNodeMap attList;
	                attList=list.item(i).getAttributes();
	                for(int j=0;j<attList.getLength();j++)
	                {
	                  Attr attr=(Attr)attList.item(j);
	                  if(attr.getName().compareTo("filename")==0)
	                  {
	                    part.setFileName(attr.getNodeValue().replaceAll("\"",""));
	                  }
	                }
	              }
	                      
	              part.setHeader(list.item(i).getNodeName(),body);
	          }
	        }
	      }
	      catch (Exception e)
	      {
	        e.printStackTrace();
	      }
	      finally{
	    	  try {
	    		  is.close();
			} catch (Exception e2) {
				System.out.println(e2.getLocalizedMessage());
			}
	    	  
	      }
	      //System.out.println(body);
	      return part;
	  }
	  
	  protected String convertToString(Document newDoc)
	    {
	        try
	        {
	            TransformerFactory factory = TransformerFactory.newInstance();
	            Transformer aTransformer=factory.newTransformer();
	            aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
	            
	            DOMSource src=new DOMSource(newDoc);
	            StringWriter writer=new StringWriter();
	            StreamResult dest=new StreamResult(writer);
	            aTransformer.transform(src, dest);

	            return writer.toString();
	        }
	        catch(IllegalArgumentException e)
	        {
	            return e.toString();
	        }
	        catch(Exception exp)
	        {
	            return exp.toString();
	        }
	        
	    }

}

