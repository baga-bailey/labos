package uk.co.mafew.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import uk.co.mafew.logging.Logger;
import static java.nio.file.StandardCopyOption.*;

public class FileHelper
{

	Logger logger;

	public static void main(String[] args)
	{
		FileHelper fh = new FileHelper();
		/*System.out.println(Convert.elementToString((Element)
				fh.extractFilesFromZip("C:\\Users\\jbailey1\\Desktop\\New folder\\test 1.zip",
				"C:\\Users\\jbailey1\\Desktop\\New folder")));*/

		
		/* System.out.println(Convert.docToString((Document)
		 fh.getInnerZipProperties(
		 "\\\\ukfavwlmts03\\c$\\Decode, Analysis and Tech tools\\DecodeAutomation\\files\\decryptSaosFile\\processedFiles\\00179599_955063792_AUDITW6_plain.00179599-UKN-MR.zip"
		 )));*/
		 
		
		//System.out.println(fh.zipFile("C:\\Users\\jbailey1\\Desktop\\New, folder\\6077f995-66cd-4eeb-88d2-9fb4afd67dd4\\6077f995-66cd-4eeb-88d2-9fb4afd67dd4\\6077f995-66cd-4eeb-88d2-9fb4afd67dd4\\test%201.zip", "C:\\Users\\jbailey1\\Documents\\New folder\\Infor_usage_data.csv"));
		//fh.parseTextFile("C:\\Users\\jbailey1\\Documents\\uranus\\files\\system21\\workingDirectory\\51fe209b-58ed-4fe4-808f-51d3bce0d463\\AC1_SYS1_SL71_02.09.16_VERSION.txt",
		//		".*", "UTF-16le");
		
		fh.appendToLine("C:\\Users\\jbailey1\\Documents\\uranus\\files\\system21\\workingDirectory\\271f1e3c-0ba2-4d04-905e-e25907356712\\AC1_SYS2_SL71_week1_02.09.16_UserNames.csv", 
				",\"Status\"", "^\"UserId\",\"Username\".*", true, "UTF-16le");
	}
	public FileHelper()
	{
		logger = new Logger(this.getClass().getName());
	}

	public boolean createDirectory(String dir)
	{
		boolean retValue = false;
		retValue = (new File(dir)).mkdirs();
		return retValue;
	}

	public boolean recursiveDelete(String dir)
	{
		Path directory = Paths.get(dir);
		boolean retVal = false;
		try
		{
			Files.walkFileTree(directory, new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
				{
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
				{
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}

			});
			retVal = true;
		}
		catch (IOException e)
		{
			logger.log.error(e.getMessage());
		}
		return retVal;
	}

	public boolean moveDirectory(String sourceDir, String destinationDir)
	{
		boolean retVal = false;
		retVal = copyDirectory(sourceDir, destinationDir);
		if (retVal)
		{
			retVal = recursiveDelete(sourceDir);
		}

		return retVal;
	}

	public boolean copyDirectory(final String sourceDir, final String destinationDir)
	{
		Path sourceDirectory = Paths.get(sourceDir);
		Path destDirectory = Paths.get(destinationDir);
		boolean retVal = false;

		try
		{
			if (!destDirectory.toFile().exists())
			{
				Files.createDirectory(destDirectory);
			}
			Files.walkFileTree(sourceDirectory, new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
				{
					Path tempDest = Paths.get(file.toString().replace(sourceDir, destinationDir));
					Files.copy(file, tempDest);
					System.out.println(file.getFileName());
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
				{
					Path tempDest = Paths.get(dir.toString().replace(sourceDir, destinationDir));
					if (!tempDest.toFile().exists())
					{
						Files.createDirectory(tempDest);
					}
					System.out.println(dir.getFileName());
					return FileVisitResult.CONTINUE;
				}

			});
			retVal = true;
		}
		catch (IOException e)
		{
			logger.log.error(e.getMessage());
		}
		return retVal;
	}

	public boolean deleteDirectory(String dir)
	{
		File file = new File(dir);
		return deleteDirectory(file);
	}

	public boolean deleteDirectory(File file)
	{
		if (!file.exists())
		{
			return false;
		}
		if (file.isDirectory())
		{
			for (File f : file.listFiles())
			{
				deleteDirectory(f);
			}
		}
		return file.delete();
	}

	public boolean copyFile(String source, String target)
	{
		try
		{
			Path srcPath = Paths.get(source);
			Path targetPath = Paths.get(target);
			Files.copy(srcPath, targetPath, COPY_ATTRIBUTES);
		}
		catch (IOException e)
		{
			logger.log.debug(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean moveFile(String source, String target)
	{
		try
		{
			Path srcPath = Paths.get(source);
			Path targetPath = Paths.get(target);
			Files.move(srcPath, targetPath);
		}
		catch (IOException e)
		{
			logger.log.debug(e.getMessage());
			return false;
		}
		return true;
	}

	public Node moveAllFiles(String sourceDir, String targetDir, String regex)
	{
		return moveAllFiles(sourceDir, targetDir, regex, true);
	}

	public Node copyAllFiles(String sourceDir, String targetDir, String regex)
	{
		return moveAllFiles(sourceDir, targetDir, regex, false);
	}

	private Node moveAllFiles(String sourceDir, String targetDir, String regex, boolean move)
	{
		Node result = null;
		Document doc = null;
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			String rootNode;
			if (move)
			{
				rootNode = "filesMoved";
			}
			else
			{
				rootNode = "filesCopied";
			}
			// rootNode = "<" + rootNode + "></" + rootNode + ">";
			StringReader sr = new StringReader("<" + rootNode + "></" + rootNode + ">");
			InputSource is = new InputSource(sr);
			doc = db.parse(is);

			File folder = new File(sourceDir);

			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++)
			{
				String filename = listOfFiles[i].getName();
				if (isMatch(regex, filename))
				{
					if (listOfFiles[i].isFile())
					{
						if (move)
						{
							Files.move(listOfFiles[i].toPath(), Paths.get(targetDir + "\\" + listOfFiles[i].getName()));
						}
						else
						{
							Files.copy(listOfFiles[i].toPath(), Paths.get(targetDir + "\\" + listOfFiles[i].getName()));
						}
						Node node = doc.createElement("filename");
						node.setTextContent(filename);
						doc.getElementsByTagName(rootNode).item(0).appendChild(node);
					}
					// TODO I think we should remove this so that only files are
					// handled here. If we want to include directories we need
					// to use a modified version of copyDirectory
					else if (listOfFiles[i].isDirectory())
					{
						if (move)
						{
							Files.move(listOfFiles[i].toPath(), Paths.get(targetDir + "\\" + listOfFiles[i].getName()));
						}
						else
						{
							Files.copy(listOfFiles[i].toPath(), Paths.get(targetDir + "\\" + listOfFiles[i].getName()));
						}
						Node node = doc.createElement("directoryName");
						node.setTextContent(listOfFiles[i].getName());
						doc.getElementsByTagName(rootNode).item(0).appendChild(node);
					}
				}
			}

		}
		catch (Exception e)
		{
			String error = e.getMessage();
			error = error.replaceAll("&", "&amp;");
			error = error.replaceAll("<", "&lt;");
			error = error.replaceAll(">", "&gt;");
			Node node = doc.createElement("error");
			node.setTextContent(error);
			doc.getElementsByTagName("properties").item(0).appendChild(node);
		}

		if (move)
		{
			result = doc.getElementsByTagName("filesMoved").item(0);
		}
		else
		{
			result = doc.getElementsByTagName("filesCopied").item(0);
		}

		return result;
	}

	public boolean deleteFile(String source)
	{
		try
		{
			Path srcPath = Paths.get(source);
			Files.deleteIfExists(srcPath);
		}
		catch (IOException e)
		{
			logger.log.debug(e.getMessage());
			return false;
		}
		return true;
	}

	public Node loadXmlFile(String inputFile)
	{
		Node root = null;
		try
		{
			File xmlDocument = Paths.get(inputFile).toFile();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(xmlDocument);
			root = (Node) doc.getDocumentElement();
		}
		catch (Exception e)
		{
			logger.log.debug(e.getMessage());
		}

		return root;
	}

	public String zipFile(String zipFilename, String filename)
	{
		String returnString = "";
		zipFilename = zipFilename.replaceAll("\\s", "%20");
		//filename = filename.replaceAll("\\s", "%20");
		try (FileSystem zipFileSystem = createZipFileSystem(zipFilename, true))
		{
			final Path root = zipFileSystem.getPath("/");

			final Path src = Paths.get(filename);

			// add a file to the zip file system
			if (!Files.isDirectory(src))
			{
				File file = new File(filename);
				final Path dest = zipFileSystem.getPath(root.toString(), file.getName());
				final Path parent = dest.getParent();
				if (Files.notExists(parent))
				{
					Files.createDirectories(parent);
				}
				Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
			}

			returnString = "SUCCESS";
		}
		catch (Exception e)
		{
			returnString = e.getMessage();
			e.printStackTrace();
		}
		return returnString;
	}
	
	public String addFileToZip(String zipFilename, String filename, String encoding) 
	{
		zipFilename = zipFilename.replaceAll("\\s", "%20");
		String returnString = "";
		Map<String, String> env = new HashMap<>(); 
		env.put("create", "true");
		Path path = Paths.get(filename);
		URI uri = URI.create("jar:" + path.toUri());
		try (FileSystem fs = FileSystems.newFileSystem(uri, env))
		{
		    Path nf = fs.getPath("new.txt");
		    /*try (Writer writer = Files.newBufferedWriter(nf, "", StandardOpenOption.CREATE)) {
		        writer.write("hello");
		    }*/
		    
		    BufferedWriter bw = new BufferedWriter((new OutputStreamWriter(
					new FileOutputStream("week\\AC1_02.09.16_PROCESSOR.csv"), encoding)));
			bw.write("\"VENDOR\",\"MHZ\",\"MODEL\",\"CACHE_SIZE\",\"SAOS_ACTUAL_DATE\",\"SAOS_SCHEDULED_DATE\"");
		    
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return returnString;
	}

	public String zipDirectory(String zipFilename, String filename)
	{
		String returnString = "";
		try (FileSystem zipFileSystem = createZipFileSystem(zipFilename, true))
		{
			final Path root = zipFileSystem.getPath("/");

			final Path src = Paths.get(filename);

			// add a file to the zip file system
			if (Files.isDirectory(src))
			{
				File file = new File(filename);
				final Path dest = zipFileSystem.getPath(root.toString(), file.getName());
				final Path parent = dest.getParent();
				if (Files.notExists(parent))
				{
					Files.createDirectories(parent);
				}
				Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
			}

			returnString = "SUCCESS";
		}
		catch (Exception e)
		{
			returnString = e.getMessage();
		}
		return returnString;
	}

	public String renameFileInZip(String zipFile, String oldFilePath, String newFilePath)
	{
		// remove all spaces

		// syntax defined in java.net.JarURLConnection
		String uriString = zipFile;
		uriString = uriString.replaceAll("\\s", "%20");
		uriString = uriString.replaceAll("\\\\", "/");
		URI uri = URI.create("jar:file:///" + uriString);

		try (FileSystem zipfs = FileSystems.newFileSystem(uri, Collections.<String, Object> emptyMap()))
		{
			Path sourceURI = zipfs.getPath(oldFilePath);
			Path destinationURI = zipfs.getPath(newFilePath);

			Files.move(sourceURI, destinationURI);
			return "SUCCESS";
		}
		catch (Exception e)
		{
			return "ERROR: " + e.getMessage();
		}
	}

	public String deleteFileInZip(String zipFile, String oldFilePath)
	{
		// syntax defined in java.net.JarURLConnection
		String uriString = zipFile;
		uriString = uriString.replaceAll("\\s", "%20");
		uriString = uriString.replaceAll("\\\\", "/");
		URI uri = URI.create("jar:file:///" + uriString);

		try (FileSystem zipfs = FileSystems.newFileSystem(uri, Collections.<String, Object> emptyMap()))
		{
			Path sourceURI = zipfs.getPath(oldFilePath);

			Files.deleteIfExists(sourceURI);
			return "SUCCESS";
		}
		catch (Exception e)
		{
			return "ERROR: " + e.getMessage();
		}
	}

	public Node listFilesInZip(String zipFile)
	{
		ZipFile file = null;
		Node result = null;
		Document doc = null;

		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<properties></properties>");
			InputSource is = new InputSource(sr);
			doc = db.parse(is);

			file = new ZipFile(zipFile);
			Enumeration<? extends ZipEntry> entries = file.entries();
			while (entries.hasMoreElements())
			{
				ZipEntry entry = entries.nextElement();
				Node node = doc.createElement("filename");
				node.setTextContent(entry.getName());
				doc.getElementsByTagName("properties").item(0).appendChild(node);
			}
		}
		catch (Exception e)
		{
			String error = e.getMessage();
			error = error.replaceAll("&", "&amp;");
			error = error.replaceAll("<", "&lt;");
			error = error.replaceAll(">", "&gt;");
			Node node = doc.createElement("error");
			node.setTextContent(error);
			doc.getElementsByTagName("properties").item(0).appendChild(node);
		}
		finally
		{
			try
			{
				file.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		result = doc.getElementsByTagName("properties").item(0);
		return result;
	}

	public String verifyZip(String zipFile)
	{
		ZipFile file = null;
		String returnString = "CORRUPT";
		try
		{
			file = new ZipFile(zipFile);
			Enumeration<? extends ZipEntry> entries = file.entries();
			while (entries.hasMoreElements())
			{
				ZipEntry entry = entries.nextElement();
				entry.getCrc();
				entry.getCompressedSize();
				entry.getName();
			}
			returnString = "VALID";
		}
		catch (Exception e)
		{
			returnString = "CORRUPT: " + e.getMessage();
		}
		finally
		{
			try
			{
				if (file != null)
				{
					file.close();
				}
			}
			catch (Exception e)
			{
				returnString = "CORRUPT: " + e.getMessage();
			}
		}
		return returnString;
	}

	public Node getInnerZipProperties(String zipFile)
	{
		ZipFile file = null;
		Node result = null;
		Document doc = null;

		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<properties></properties>");
			InputSource is = new InputSource(sr);
			doc = db.parse(is);

			file = new ZipFile(zipFile);
			Enumeration<? extends ZipEntry> entries = file.entries();
			while (entries.hasMoreElements())
			{
				ZipEntry entry = entries.nextElement();

				entry.getCrc();
				entry.getCompressedSize();

				Node nameNode = doc.createElement("filename");
				nameNode.setTextContent(entry.getName());
				doc.getElementsByTagName("properties").item(0).appendChild(nameNode);

				Node crcNode = doc.createElement("filename");
				crcNode.setTextContent(entry.getName());
				nameNode.appendChild(crcNode);

				Node compressedSizeNode = doc.createElement("filename");
				compressedSizeNode.setTextContent(entry.getName());
				nameNode.appendChild(compressedSizeNode);
			}
		}
		catch (Exception e)
		{
			String error = e.getMessage();
			error = error.replaceAll("&", "&amp;");
			error = error.replaceAll("<", "&lt;");
			error = error.replaceAll(">", "&gt;");
			Node node = doc.createElement("error");
			node.setTextContent(error);
			doc.getElementsByTagName("properties").item(0).appendChild(node);
		}
		finally
		{
			try
			{
				file.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		result = doc.getElementsByTagName("properties").item(0);
		return result;
	}

	public String zipFile(String zipFilename, String... filenames)
	{
		String returnString = "";
		try (FileSystem zipFileSystem = createZipFileSystem(zipFilename, true))
		{
			final Path root = zipFileSystem.getPath("/");

			// iterate over the files we need to add
			for (String filename : filenames)
			{
				final Path src = Paths.get(filename);

				// add a file to the zip file system
				if (!Files.isDirectory(src))
				{
					File file = new File(filename);
					final Path dest = zipFileSystem.getPath(root.toString(), file.getName());
					final Path parent = dest.getParent();
					if (Files.notExists(parent))
					{
						Files.createDirectories(parent);
					}
					Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
				}
				else
				{
					// for directories, walk the file tree
					Files.walkFileTree(src, new SimpleFileVisitor<Path>()
					{
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
						{
							final Path dest = zipFileSystem.getPath(root.toString(), file.toString());
							Files.copy(file, dest, StandardCopyOption.REPLACE_EXISTING);
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
								throws IOException
						{
							final Path dirToCreate = zipFileSystem.getPath(root.toString(), dir.toString());
							if (Files.notExists(dirToCreate))
							{
								Files.createDirectories(dirToCreate);
							}
							return FileVisitResult.CONTINUE;
						}
					});
				}
			}
			returnString = "SUCCESS";
		}
		catch (Exception e)
		{
			returnString = e.getMessage();
		}
		return returnString;
	}
	
	public Node extractFilesFromZip(String zipFile, String outputFolder)
	{
		return extractFilesFromZip(zipFile, outputFolder, ".*");
	}

	public Node extractFilesFromZip(String zipFile, String outputFolder, String regex)
	{
		Node result = null;
		Document doc = null;
		byte[] buffer = new byte[1024];
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<files></files>");
			InputSource is = new InputSource(sr);
			doc = db.parse(is);

			// create output directory is not exists
			File folder = new File(outputFolder);
			if (!folder.exists())
			{
				folder.mkdir();
			}

			ZipFile file = null;
			try
			{
				file = new ZipFile(zipFile);
				Enumeration<? extends ZipEntry> entries = file.entries();
				while (entries.hasMoreElements())
				{
					final ZipEntry entry = entries.nextElement();
					String fileName = entry.getName();
					
					if (fileName.matches(regex))
					{
						Node node = doc.createElement("filename");
						node.setTextContent(fileName);
						//doc.getElementsByTagName("files").item(0).appendChild(node);
						System.out.println(fileName);
						// use entry input stream:
						InputStream ins = file.getInputStream(entry);
						File newFile = new File(outputFolder + File.separator + fileName);
						new File(newFile.getParent()).mkdirs();
						FileOutputStream fos = new FileOutputStream(newFile);
						int len;
						Element tempElement = (Element) node;
						try
						{
							while ((len = ins.read(buffer)) > 0)
							{
								fos.write(buffer, 0, len);
							}
							tempElement.setAttribute("extracted", "true");
						}
						catch (Exception e)
						{
							tempElement.setAttribute("extracted", "false");
						}
						doc.getElementsByTagName("files").item(0).appendChild(tempElement);
						try
						{
							fos.close();
						}
						catch (Exception e)
						{
							String error = e.getMessage();
							error = error.replaceAll("&", "&amp;");
							error = error.replaceAll("<", "&lt;");
							error = error.replaceAll(">", "&gt;");
							Node errorNodeode = doc.createElement("error");
							errorNodeode.setTextContent(error);
							doc.getElementsByTagName("files").item(0).appendChild(errorNodeode);
						}
					}

				}
			}
			finally
			{
				file.close();
			}

			/*
			 * // get the zip file content ZipInputStream zis = new
			 * ZipInputStream(new FileInputStream(zipFile)); // get the zipped
			 * file list entry ZipEntry ze = zis.getNextEntry();
			 * 
			 * while (ze != null) { String fileName = ze.getName();
			 * 
			 * Node node = doc.createElement("filename");
			 * node.setTextContent(fileName);
			 * doc.getElementsByTagName("files").item(0).appendChild(node);
			 * 
			 * File newFile = new File(outputFolder + File.separator +
			 * fileName);
			 * 
			 * new File(newFile.getParent()).mkdirs();
			 * 
			 * FileOutputStream fos = new FileOutputStream(newFile);
			 * 
			 * int len; try { while ((len = zis.read(buffer)) > 0) {
			 * fos.write(buffer, 0, len); } Element tempElement = (Element)
			 * node; tempElement.setAttribute("extracted", "true"); } catch
			 * (Exception e) { Element tempElement = (Element) node;
			 * tempElement.setAttribute("extracted", "false"); }
			 * 
			 * try { fos.close(); } catch (Exception e) { String error =
			 * e.getMessage(); error = error.replaceAll("&", "&amp;"); error =
			 * error.replaceAll("<", "&lt;"); error = error.replaceAll(">",
			 * "&gt;"); Node errorNodeode = doc.createElement("error");
			 * errorNodeode.setTextContent(error);
			 * doc.getElementsByTagName("files"
			 * ).item(0).appendChild(errorNodeode); } try { ze =
			 * zis.getNextEntry(); } catch (Exception e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } }
			 * 
			 * zis.closeEntry(); zis.close();
			 */

		}
		catch (Exception e)
		{
			String error = e.getMessage();
			error = error.replaceAll("&", "&amp;");
			error = error.replaceAll("<", "&lt;");
			error = error.replaceAll(">", "&gt;");
			Node node = doc.createElement("error");
			node.setTextContent(error);
			doc.getElementsByTagName("files").item(0).appendChild(node);
		}

		result = doc.getElementsByTagName("files").item(0);
		return result;
	}

	private static FileSystem createZipFileSystem(String zipFilename, boolean create) throws IOException
	{
		// convert the filename to a URI
		final Path path = Paths.get(zipFilename);
		final URI uri = URI.create("jar:file:" + path.toUri().getPath());

		final Map<String, String> env = new HashMap<>();
		if (create)
		{
			env.put("create", "true");
		}
		return FileSystems.newFileSystem(uri, env);
	}

	public String appendFile(String file, String str)
	{
		String returnString = "";
		BufferedWriter bw = null;

		try
		{
			bw = new BufferedWriter(new FileWriter(file, true));
			bw.write(str);
			bw.newLine();
			bw.flush();
			returnString = "SUCCESS";
		}
		catch (Exception e)
		{
			returnString = e.getMessage();
		}
		finally
		{
			if (bw != null)
				try
				{
					bw.close();
				}
				catch (Exception e)
				{
				}
		}
		return returnString;
	}
	
	public String appendToLine(String sourceFile, String stringToAppend, String regex, boolean include,
			String encoding)
	{
		String returnString = "";
		PrintWriter out = null;
		File file = new File(sourceFile);
		BufferedReader br = null;
		try 
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),
					Charset.forName(encoding)));
			// out = new PrintWriter(new BufferedWriter(new
			// FileWriter(destinationFile, true)));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file.getParent() + "\\tempFile", true), Charset.forName(encoding))));
			String line = "";
			while ((line = br.readLine()) != null)
			{
				if (line.matches(regex) == include)
				{
					line += stringToAppend;
				}
				out.println(line);
			}

			br.close();
			out.close();
			deleteFile(sourceFile);
			copyFile(file.getParent() + "\\tempFile", sourceFile);
			deleteFile(file.getParent() + "\\tempFile");
			returnString = "SUCCESS";
		}
		catch (Exception e)
		{
			returnString = "ERROR: " + e.getMessage();
		}
		finally
		{
			try
			{
				br.close();
				out.close();
			}
			catch (Exception e2)
			{
				// Ignore exception
			}
		}

		return returnString;
	}

	public Node parseTextFile(String sourceFile, String regex, String encoding)
	{
		Node root = null;
		Document doc = null;
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<Lines></Lines>");
			InputSource is = new InputSource(sr);

			doc = db.parse(is);
			String line = null;
			
			FileInputStream fis = new FileInputStream(sourceFile);
			InputStreamReader isr = new InputStreamReader(fis, encoding);
			BufferedReader br = new BufferedReader(isr);

			try 
			{

				while ((line = br.readLine()) != null)
				{
					if (line.matches(regex))
					{
						line = line.replaceAll("&", "&amp;");
						line = line.replaceAll("<", "&lt;");
						line = line.replaceAll(">", "&gt;");
						Node node = doc.createElement("line");
						node.setTextContent(line);
						doc.getElementsByTagName("Lines").item(0).appendChild(node);
					}
				}
			}
			catch (Exception e)
			{
				line = e.getMessage();
				line = line.replaceAll("&", "&amp;");
				line = line.replaceAll("<", "&lt;");
				line = line.replaceAll(">", "&gt;");
				Node node = doc.createElement("error");
				node.setTextContent(line);
				doc.getElementsByTagName("Lines").item(0).appendChild(node);
			}
			finally{
				br.close();
			}
		}
		catch (Exception e)
		{
			logger.log.debug(e.getMessage());
		}

		root = doc.getElementsByTagName("Lines").item(0);
		return root;
	}
	
	public Node parseTextFile(String sourceFile, String regex)
	{
		Node root = null;
		Document doc = null;
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<Lines></Lines>");
			InputSource is = new InputSource(sr);

			doc = db.parse(is);
			String line = null;

			try (BufferedReader br = new BufferedReader(new FileReader(sourceFile)))
			{

				while ((line = br.readLine()) != null)
				{
					if (line.matches(regex))
					{
						line = line.replaceAll("&", "&amp;");
						line = line.replaceAll("<", "&lt;");
						line = line.replaceAll(">", "&gt;");
						Node node = doc.createElement("line");
						node.setTextContent(line);
						doc.getElementsByTagName("Lines").item(0).appendChild(node);
					}
				}
			}
			catch (Exception e)
			{
				line = e.getMessage();
				line = line.replaceAll("&", "&amp;");
				line = line.replaceAll("<", "&lt;");
				line = line.replaceAll(">", "&gt;");
				Node node = doc.createElement("error");
				node.setTextContent(line);
				doc.getElementsByTagName("Lines").item(0).appendChild(node);
			}
		}
		catch (Exception e)
		{
			logger.log.debug(e.getMessage());
		}

		root = doc.getElementsByTagName("Lines").item(0);
		return root;
	}


	public String copySelectiveLines(String sourceFile, String destinationFile, String regex, boolean include)
	{
		return copySelectiveLines(sourceFile, destinationFile, regex, include, "UTF-8");
	}

	public String copySelectiveLines(String sourceFile, String destinationFile, String regex, boolean include,
			String encoding)
	{
		String returnString = "";
		PrintWriter out = null;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile),
				Charset.forName(encoding))))
		{
			// out = new PrintWriter(new BufferedWriter(new
			// FileWriter(destinationFile, true)));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(destinationFile, true), Charset.forName(encoding))));
			String line = "";
			while ((line = br.readLine()) != null)
			{
				if (line.matches(regex) == include)
				{
					out.println(line);
				}
			}

			returnString = "SUCCESS";
		}
		catch (Exception e)
		{
			returnString = "ERROR: " + e.getMessage();
		}
		finally
		{
			try
			{
				out.close();
			}
			catch (Exception e2)
			{
				// Ignore exception
			}
		}

		return returnString;
	}

	public String getMD5Hash(String file)
	{
		String returnString = "";
		FileInputStream fis = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			fis = new FileInputStream(file);

			byte[] bites = new byte[1024];

			int bite = 0;
			while ((bite = fis.read(bites)) != -1)
			{
				md.update(bites, 0, bite);
			}
			byte[] mdbytes = md.digest();

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < mdbytes.length; i++)
			{
				String hex = Integer.toHexString(0xff & mdbytes[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}

			returnString = hexString.toString();
			logger.log.info("MD5 hash = " + returnString);
		}
		catch (Exception e)
		{
			returnString = "ERROR: " + e.getMessage();
			logger.log.error(returnString);
		}
		finally
		{
			try
			{
				fis.close();
			}
			catch (IOException e)
			{

			}
		}
		return returnString;
	}

	private boolean isMatch(String regex, String targetStr)
	{

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(targetStr);

		boolean found = false;
		while (matcher.find())
		{
			found = true;

		}

		return found;
	}
}
