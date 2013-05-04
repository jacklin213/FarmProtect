package me.jacklin213.farmprotect.ulits;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import me.jacklin213.farmprotect.FarmProtect;

public class UpdateChecker {

	public static FarmProtect plugin;

	private String version;
	private String link;
	private URL filesFeed;
	public float pluginVersion;
	public float latestVersion;

	public UpdateChecker(FarmProtect instance, String url) {
		plugin = instance;
		try {
			this.filesFeed = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public boolean updateNeeded() {
		try {
			InputStream input = this.filesFeed.openConnection()
					.getInputStream();
			Document document = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(input);

			Node latestFile = document.getElementsByTagName("item").item(0);
			NodeList children = latestFile.getChildNodes();

			this.version = children.item(1).getTextContent()
					.replaceAll("[a-zA-Z ]", "");
			this.link = children.item(3).getTextContent();

			this.pluginVersion = Float.parseFloat(plugin.getDescription()
					.getVersion());
			this.latestVersion = Float.parseFloat(this.version);
			if ((plugin.getDescription().getVersion().equals(this.version))
					|| (this.pluginVersion > this.latestVersion)) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getVersion() {
		return this.version;
	}

	public String getLink() {
		return this.link;
	}

}
