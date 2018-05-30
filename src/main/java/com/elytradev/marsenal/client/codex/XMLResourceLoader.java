/*
 * MIT License
 *
 * Copyright (c) 2018 Isaac Ellingson (Falkreon) and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.elytradev.marsenal.client.codex;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.elytradev.marsenal.MagicArsenal;
import com.elytradev.marsenal.gui.ContainerCodex;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class XMLResourceLoader implements IResourceManagerReloadListener {
	
	public static XMLResourceLoader INSTANCE;
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void preInit() {
		IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
		if (resourceManager instanceof IReloadableResourceManager) {
			IReloadableResourceManager mgr = (IReloadableResourceManager)resourceManager;
			mgr.registerReloadListener(this);
			
			
		}
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		String locale = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
		
		try {
			IResource indexResource = resourceManager.getResource(new ResourceLocation("magicarsenal:codex/"+locale.toLowerCase(Locale.ROOT)+"/index.xml"));
			bootstrapCodex(locale, indexResource);
		} catch (IOException ex) {
			locale = "en_US";
			
			try {
				IResource indexResource = resourceManager.getResource(new ResourceLocation("magicarsenal:codex/"+locale.toLowerCase(Locale.ROOT)+"/index.xml"));
				bootstrapCodex(locale, indexResource);
			} catch (IOException ex2) {
				MagicArsenal.LOG.error("There was a problem reading the data for the Codex. Additionally, the default/fallback en_US Codex could not be read.", ex);
			}
		}
		
		
	}
	
	public void bootstrapCodex(String locale, IResource indexResource) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setCoalescing(true);
			factory.setXIncludeAware(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStream stream = indexResource.getInputStream();
			Document document = builder.parse(stream, "/dev/null/");
			
			NodeList list = document.getChildNodes();
			if (list.getLength()>=1) {
				Node codexNode = list.item(0);
				if (!codexNode.getNodeName().equals("codex")) throw new IOException("The Codex index.xml's root element MUST be <codex></codex>");
				NodeList pages = codexNode.getChildNodes();
				if (pages.getLength()<=0) throw new IOException("The Codex is empty!");
				for(int i=0; i<pages.getLength(); i++) {
					Node pageNode = pages.item(i);
					if (pageNode.getNodeName().equals("page")) {
						ContainerCodex.CodexPage page = parsePage(pageNode);
						if (page!=null) {
							ContainerCodex.CODEX_PAGES.add(page);
						}
					} else {
						//MagicArsenal.LOG.warn("Unknown node type '{}', expected 'page'. Ignoring this node.", pageNode.getNodeName());
					}
				}
			}
		} catch (IOException | ParserConfigurationException | SAXException e) {
			MagicArsenal.LOG.error("Couldn't get data for the codex.", e);
		}
	}
	
	public ContainerCodex.CodexPage parsePage(Node pageNode) {
		NodeList list = pageNode.getChildNodes();
		if (list.getLength()==0) return null;
		
		ContainerCodex.CodexPage result = new ContainerCodex.CodexPage();
		for(int i=0; i<list.getLength(); i++) {
			Node node = list.item(i);
			switch(node.getNodeName()) {
			case "left":
				result.setLeftPage(cleanup(node.getTextContent()));
				break;
			case "right":
				result.setRightPage(cleanup(node.getTextContent()));
				break;
			case "header":
				Node refNode = node.getAttributes().getNamedItem("ref");
				if (refNode!=null && !refNode.getTextContent().isEmpty()) {
					result.setHeader(refNode.getTextContent());
				}
				break;
			case "item":
				NamedNodeMap attributes = node.getAttributes();
				Node idNode = attributes.getNamedItem("id");
				if (idNode!=null) {
					String id = idNode.getTextContent();
					int data = 0;
					Node metaNode = attributes.getNamedItem("data");
					if (metaNode==null) metaNode = attributes.getNamedItem("damage");
					if (metaNode!=null) {
						try {
							data = Integer.valueOf(metaNode.getTextContent());
						} catch (NumberFormatException ex) {
							//Just give us the zero-meta instead.
						}
					}
					result.setSpotlight(id, data);
				}
				break;
			case "background":
				Node featureNode = node.getAttributes().getNamedItem("ref");
				if (featureNode!=null && !featureNode.getTextContent().isEmpty()) {
					result.setFeature(featureNode.getTextContent());
				}
				break;
			}
			
		}
		
		return result;
	}
	
	public String cleanup(String text) {
		String[] strings = text.split("\\n");
		StringBuilder result = new StringBuilder();
		for(int i=0; i<strings.length; i++) {
			result.append(strings[i].trim());
			if (i<strings.length-1) result.append(" ");
		}
		return result.toString();
	}
	
	public static XMLResourceLoader getInstance() {
		if (INSTANCE==null) {
			INSTANCE = new XMLResourceLoader();
		}
		
		return INSTANCE;
	}
}
