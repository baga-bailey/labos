package uk.co.mafew.hephaestus;

import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;

import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import javax.ejb.Stateless;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import uk.co.mafew.logging.Logger;
import uk.co.mafew.narcissus.ReflectedObject;

@Stateless
public class DirectoryWatcher extends Thread {

	private WatchService watcher;
	private Map<WatchKey, Path> keys;
	// private Map<WatchKey, Element> bindings;
	private Map<WatchKey, ArrayList<Element>> bindings;
	private boolean trace;
	private long WRITE_CHECK_PAUSE = 5000;
	private Logger logger;

	public long getWRITE_CHECK_PAUSE() {
		return WRITE_CHECK_PAUSE;
	}

	public void setWRITE_CHECK_PAUSE(long writeCheckPause) {
		WRITE_CHECK_PAUSE = writeCheckPause;
	}

	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	public static void main(String[] args) throws IOException {

		// register directory and process its events
		// Path dir = Paths.get("C://");
		// new DirectoryWatcher(dir, false).processEvents();
		DirectoryWatcher dirWatcher = new DirectoryWatcher();
		dirWatcher.register("C://");
		dirWatcher.processEvents();
	}

	public void run() {
		processEvents();
	}

	public DirectoryWatcher() throws IOException {
		logger = new Logger(this.getClass().getName());
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey, Path>();
		this.bindings = new HashMap<WatchKey, ArrayList<Element>>();

		// enable trace after initial registration
		this.trace = true;
	}

	/**
	 * Register the given directory with the WatchService
	 */
	public WatchKey register(String path) throws IOException {
		Path dir = Paths.get(path);

		Kind<Path> kind = ENTRY_CREATE;
		WatchKey key = dir.register(watcher, kind);
		if (trace) {
			Path prev = keys.get(key);
			if (prev == null) {
				logger.log.info(dir.toString() + " has been registered: " + kind.name());
			} else {
				if (!dir.equals(prev)) {
					logger.log.info(dir.toString() + " has been updated: " + kind.name());
				} else {
					logger.log.info(dir.toString() + " is already registered: " + kind.name());
				}
			}
		}
		keys.put(key, dir);
		return key;
		// TODO Add another Map to bind the key to the doc containing the event
		// to be called
		// or do we need to create class that holds everything?
	}

	public void bind(WatchKey key, Element targetClassElem) {
		ArrayList<Element> list = bindings.get(key);

		if (list == null) {
			list = new ArrayList<Element>();
			bindings.put(key, list);
		}
		list.add(targetClassElem);
	}

	/**
	 * Creates a WatchService and registers the given directory
	 */
	DirectoryWatcher(String path, boolean recursive) throws IOException {
		Path dir = Paths.get(path);
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey, Path>();
		if (recursive) {
			logger.log.info(dir.toString() + " is being scanned");
			registerAll(dir);
			logger.log.info("Done.");
		} else {
			register(path);
		}

		// enable trace after initial registration
		this.trace = true;
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void registerAll(final Path start) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				register("");
				return FileVisitResult.CONTINUE;
			}
		});
	}

	/**
	 * Process all events for keys queued to the watcher
	 */
	void processEvents() {
		for (;;) {
			// wait for key to be signalled
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}

			Path dir = keys.get(key);
			if (dir == null) {
				logger.log.error("WatchKey not recognized!!");
				// continue;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				Kind<?> kind = event.kind();

				if (kind == OVERFLOW) {
					continue;
				}

				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path child = dir.resolve(name);

				// print out event
				logger.log.info(event.kind().name() + ": " + child);

				// Only process if its a file that's been created
				if (Files.isRegularFile(child, NOFOLLOW_LINKS)) {
					ArrayList<Element> list = bindings.get(key);

					for (Element watcher : list) {

						// Get the regex
						String regex = watcher.getElementsByTagName("regex").item(0).getTextContent();
						//Get the type of watcher this is
						String watchType = watcher.getElementsByTagName("watchType").item(0).getTextContent();

						if (isMatch(regex, name.toString()) && (watchType.toLowerCase().equals("file") || (watchType.equals("*")))) {
							long startLength = 0;
							long currentLength = 1;
							while (startLength < currentLength) {
								logger.log.debug("startLength of file = " + startLength);
								logger.log.debug("currentLength of file = " + currentLength);
								logger.log.debug("Will go to sleep as " + currentLength + " exceeds " + startLength);

								try {
									String localReadString = watcher.getElementsByTagName("readInterval").item(0)
											.getTextContent();
									long localReadInterval = WRITE_CHECK_PAUSE;
									try {
										localReadInterval = Long.parseLong(localReadString);
									} catch (Exception e) {

									}
									logger.log.info("Pausing for " + String.valueOf(localReadInterval)
											+ " to allow file write to complete");
									sleep(localReadInterval);
									logger.log.debug("sleep ended");
									startLength = currentLength;
									currentLength = child.toFile().length();

								} catch (InterruptedException e) {
									logger.log.error(e.getMessage());
								}
							}
							try {
								Element targetClass = (Element) watcher.getElementsByTagName("TargetClass").item(0);

								// Create a clone to work from, so that each
								// invocation
								// uses a unique document
								TransformerFactory tfactory = TransformerFactory.newInstance();
								Transformer tx = tfactory.newTransformer();
								DOMSource source = new DOMSource(targetClass);
								DOMResult result = new DOMResult();
								tx.transform(source, result);
								Element tempTargetClass = ((Document) result.getNode()).getDocumentElement();

								// Create the element to hold the source file
								// name
								Element sourceFileElement = tempTargetClass.getOwnerDocument()
										.createElement("sourceFile");
								Node sourceFileText = tempTargetClass.getOwnerDocument()
										.createTextNode(ev.context().getFileName().toString());
								sourceFileElement.appendChild(sourceFileText);

								// Create the element to hold the source
								// directory
								// name
								Element sourceDirElement = tempTargetClass.getOwnerDocument()
										.createElement("sourceDir");
								Node sourceDirText = tempTargetClass.getOwnerDocument()
										.createTextNode(child.getParent().toString());
								sourceDirElement.appendChild(sourceDirText);

								XPathFactory factory = XPathFactory.newInstance();
								XPath xPath = factory.newXPath();
								// Get the place in FileWatcher.xml to append
								// the
								// new
								// elements to
								Node headerNode = (Node) xPath.evaluate("//constructor/params/param/value/header[1]",
										tempTargetClass, XPathConstants.NODE);
								headerNode.appendChild(sourceFileElement);
								headerNode.appendChild(sourceDirElement);

								callReflectedObject cro = new callReflectedObject(tempTargetClass);
								cro.run();
							} catch (Exception e) {
								logger.log.error(e.getMessage());
							}
						}
					}
				} else if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
					processNewDir(name, child, key, ev);

				}
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty()) {
					// break;
				}
			}

		}

	}

	private boolean isMatch(String regex, String targetStr) {

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(targetStr);

		boolean found = false;
		while (matcher.find()) {
			found = true;

		}
		if (found) {
			logger.log.debug("Match found for regular expression: " + regex);
		} else {
			logger.log.debug("No match found for regular expression: " + regex);
		}
		return found;
	}

	/*
	 * Test code for directories There'll be a lot of duplication here This
	 * should be removed once we know it's working
	 */

	private void processNewDir(Path name, Path child, WatchKey key, WatchEvent<Path> ev) {
		ArrayList<Element> list = bindings.get(key);

		for (Element watcher : list) {

			// Get the regex
			String regex = watcher.getElementsByTagName("regex").item(0).getTextContent();
			//Get the type of watcher this is
			String watchType = watcher.getElementsByTagName("watchType").item(0).getTextContent();

			if (isMatch(regex, name.toString()) && (watchType.toLowerCase().equals("directory") || (watchType.equals("*")))) {
				long startLength = 0;
				long currentLength = 1;
				String localReadString = watcher.getElementsByTagName("readInterval").item(0).getTextContent();
				long localReadInterval = WRITE_CHECK_PAUSE;
				try {
					localReadInterval = Long.parseLong(localReadString);
				} catch (Exception e) {

				}
				while (startLength < currentLength) {
					logger.log.debug("startLength of file = " + startLength);
					logger.log.debug("currentLength of file = " + currentLength);
					logger.log.debug("Will go to sleep as " + currentLength + " exceeds " + startLength);
					final AtomicLong dirSize = new AtomicLong(0);

					try {

						logger.log.info("Pausing for " + String.valueOf(localReadInterval)
								+ " to allow file write to complete");
						sleep(localReadInterval);
						logger.log.debug("sleep ended");
						startLength = currentLength;

						/*
						 * Get the current size here
						 */
						try {
							Files.walkFileTree(child, new SimpleFileVisitor<Path>() {
								@Override
								public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
										throws IOException {
									dirSize.addAndGet(attrs.size());
									return FileVisitResult.CONTINUE;
								}

							});
						} catch (IOException e) {
							logger.log.error(e.getMessage());
						}

						currentLength = dirSize.get();

					} catch (InterruptedException e) {
						logger.log.error(e.getMessage());
					}
				}
				logger.log.debug("startLength of file = " + startLength);
				logger.log.debug("currentLength of file = " + currentLength);
				logger.log.debug("Will start process as " + currentLength + " does not exceed " + startLength);
				try {
					Element targetClass = (Element) watcher.getElementsByTagName("TargetClass").item(0);

					// Create a clone to work from, so that each
					// invocation
					// uses a unique document
					TransformerFactory tfactory = TransformerFactory.newInstance();
					Transformer tx = tfactory.newTransformer();
					DOMSource source = new DOMSource(targetClass);
					DOMResult result = new DOMResult();
					tx.transform(source, result);
					Element tempTargetClass = ((Document) result.getNode()).getDocumentElement();

					// Create the element to hold the source file
					// name
					Element sourceFileElement = tempTargetClass.getOwnerDocument().createElement("sourceFile");
					Node sourceFileText = tempTargetClass.getOwnerDocument()
							.createTextNode(ev.context().getFileName().toString());
					sourceFileElement.appendChild(sourceFileText);

					// Create the element to hold the source
					// directory
					// name
					Element sourceDirElement = tempTargetClass.getOwnerDocument().createElement("sourceDir");
					Node sourceDirText = tempTargetClass.getOwnerDocument()
							.createTextNode(child.getParent().toString());
					sourceDirElement.appendChild(sourceDirText);

					XPathFactory factory = XPathFactory.newInstance();
					XPath xPath = factory.newXPath();
					// Get the place in FileWatcher.xml to append
					// the
					// new
					// elements to
					Node headerNode = (Node) xPath.evaluate("//constructor/params/param/value/header[1]",
							tempTargetClass, XPathConstants.NODE);
					headerNode.appendChild(sourceFileElement);
					headerNode.appendChild(sourceDirElement);

					callReflectedObject cro = new callReflectedObject(tempTargetClass);
					cro.run();
				} catch (Exception e) {
					logger.log.error(e.getMessage());
				}
			}
		}
	}

	class callReflectedObject extends TimerTask {
		private ReflectedObject ro;

		public callReflectedObject(Element targetClass) {
			try {
				ro = new ReflectedObject(targetClass, "");
			} catch (NoSuchMethodException ne) {
				logger.log.error("THE METHOD WAS NOT FOUND");
			} catch (Exception e) {
				logger.log.error(e.getMessage());
			}
		}

		public callReflectedObject(ReflectedObject ro) {
			this.ro = ro;
		}

		public void run() {
			try {
				ro.invoke();
			} catch (Exception e) {
				logger.log.error("Error executing method");
				logger.log.error(e.getMessage());
			}
		}
	}

}
