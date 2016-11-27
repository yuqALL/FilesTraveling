package com.example.yuq32.filestraveling.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by yuq32 on 2016/6/3.
 */
public class saveXML {

// 将配置中的数据保存在本地XML文件中

    // 使用Dom方式来保存数据
    String fileNameString = null;
    String filePathString = null;
    String fileTypeString = null;
    String IpString = null;
    String portString = null;
    String wifiNameString = null;
    String wifiPSWString = null;
    String deviceNameString=null;
    Map<String, String> infoMap;

    public saveXML(Map<String, String> infoMap){
        this.infoMap=infoMap;
        initString();
        saveParam2Xml();
    }

    public void initString(){
        fileNameString=infoMap.get("fileName");
        filePathString=infoMap.get("filePath");
        fileTypeString=infoMap.get("fileType");
        IpString=infoMap.get("Ip");
        portString=infoMap.get("port");
        wifiNameString=infoMap.get("wifiName");
        wifiPSWString=infoMap.get("wifiPSW");
        deviceNameString=infoMap.get("deviceName");
    }
    public boolean saveParam2Xml() {

        // 文档生成器工厂
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // 实例化文档生成器
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            File f = new File("/mnt/sdcard/filestraveling/loadfileslist.xml");
            if (!f.exists()) {
                //创建一个文件
                f.createNewFile();

                // 生成一个文档
                Document document = builder.newDocument();

                // 创建根节点
                Element files = document.createElement("Files");
                Element file = document.createElement("File");
                // 创建XML文件所需的各种对象并序列化(元素)
                Element fileName = document.createElement("fileName");// 创建元素节点
                Element filePath = document.createElement("filePath");
                Element fileType = document.createElement("fileType");
                Element Ip = document.createElement("Ip");
                Element port = document.createElement("port");
                Element wifiName = document.createElement("wifiName");
                Element wifiPSW = document.createElement("wifiPSW");
                Element deviceName = document.createElement("deviceName");

                Text fileName_value = document.createTextNode(fileNameString);// 创建text文本
                Text filePath_value = document.createTextNode(filePathString);
                Text fileType_value = document.createTextNode(fileTypeString);
                Text Ip_value = document.createTextNode(IpString);
                Text port_value = document.createTextNode(portString);
                Text wifiName_value = document.createTextNode(wifiNameString);
                Text wifiPSW_value = document.createTextNode(wifiPSWString);
                Text deviceName_value = document.createTextNode(deviceNameString);

                fileName.appendChild(fileName_value);
                filePath.appendChild(filePath_value);
                fileType.appendChild(fileType_value);
                Ip.appendChild(Ip_value);
                port.appendChild(port_value);
                wifiName.appendChild(wifiName_value);
                wifiPSW.appendChild(wifiPSW_value);
                deviceName.appendChild(deviceName_value);

                file.appendChild(fileName);
                file.appendChild(filePath);
                file.appendChild(fileType);
                file.appendChild(port);
                file.appendChild(wifiName);
                file.appendChild(wifiPSW);
                file.appendChild(deviceName);

                files.appendChild(file);

                document.appendChild(files);// 添加到文档中

                // 调用方法，将文档写入xml文件中
                return saveXML.write2Xml(document, f);
            } else {

                // 解析文档
                Document document = builder.parse(f);
                Element files = document.getDocumentElement();// 得到根节点，把后面创建的子节点加入这个跟节点中

                // 创建XML文件所需的各种对象并序列化(元素)
                Element file = document.createElement("File");
                // 创建XML文件所需的各种对象并序列化(元素)
                Element fileName = document.createElement("fileName");// 创建元素节点
                Element filePath = document.createElement("filePath");
                Element fileType = document.createElement("fileType");
                Element Ip = document.createElement("Ip");
                Element port = document.createElement("port");
                Element wifiName = document.createElement("wifiName");
                Element wifiPSW = document.createElement("wifiPSW");
                Element deviceName = document.createElement("deviceName");

                Text fileName_value = document.createTextNode(fileNameString);// 创建text文本
                Text filePath_value = document.createTextNode(filePathString);
                Text fileType_value = document.createTextNode(fileTypeString);
                Text Ip_value = document.createTextNode(IpString);
                Text port_value = document.createTextNode(portString);
                Text wifiName_value = document.createTextNode(wifiNameString);
                Text wifiPSW_value = document.createTextNode(wifiPSWString);
                Text deviceName_value = document.createTextNode(deviceNameString);

                fileName.appendChild(fileName_value);
                filePath.appendChild(filePath_value);
                fileType.appendChild(fileType_value);
                Ip.appendChild(Ip_value);
                port.appendChild(port_value);
                wifiName.appendChild(wifiName_value);
                wifiPSW.appendChild(wifiPSW_value);
                deviceName.appendChild(deviceName_value);

                file.appendChild(fileName);
                file.appendChild(filePath);
                file.appendChild(fileType);
                file.appendChild(port);
                file.appendChild(wifiName);
                file.appendChild(wifiPSW);
                file.appendChild(deviceName);

                files.appendChild(file);// 添加到根节点中

                // 调用方法，将文档写入xml文件中
                return saveXML.write2Xml(document, f);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();

            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;

    }

    public static boolean write2Xml(Document document, File file) {
        // 创建转化工厂
        TransformerFactory factory = TransformerFactory.newInstance();
        // 创建转换实例
        try {
            Transformer transformer = factory.newTransformer();

            // 将建立好的DOM放入DOM源中
            DOMSource domSource = new DOMSource(document);

            // 创建输出流
            StreamResult result = new StreamResult(file);

            // 开始转换
            transformer.transform(domSource, result);

            return true;

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();

            return false;
        } catch (TransformerException e) {
            e.printStackTrace();

            return false;
        }
    }

    public List<Map<String, String>> parseXML(InputStream is) {

        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> info=new HashMap<>();
        // 创建DOM工厂对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            // DocumentBuilder对象
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 获取文档对象
            Document document = builder.parse(is);

            // 获取文档对象的root
            Element root = document.getDocumentElement();

            // 获取files根节点中所有的file节点对象

            NodeList fileNodes = root.getElementsByTagName("file");

            // 遍历所有的files节点

            for (int i = 0; i < fileNodes.getLength(); i++) {
                // 根据item(index)获取该索引对应的节点对象
                Element fileNode = (Element) fileNodes.item(i); // 具体的person节点

                // 获取该节点下面的所有字节点
                NodeList fileChildNodes = fileNode.getChildNodes();

                // 遍历file的字节点
                for (int index = 0; index < fileChildNodes.getLength(); index++) {
                    // 获取子节点
                    Node node = fileChildNodes.item(index);

                    // 判断node节点是否是元素节点
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        //把节点转换成元素节点
                        Element element = (Element) node;


                        if ("fileName".equals(element.getNodeName())) {
                            info.put("fileName",element.getNodeValue());
                        } else if ("filePath".equals(element.getNodeName())) {
                            info.put("filePath",element.getNodeValue());
                        }else if ("fileType".equals(element.getNodeName())) {
                            info.put("fileType",element.getNodeValue());
                        } else if ("Ip".equals(element.getNodeName())) {
                            info.put("Ip",element.getNodeValue());
                        } else if ("port".equals(element.getNodeName())) {
                            info.put("port",element.getNodeValue());
                        } else if ("wifiName".equals(element.getNodeName())) {
                            info.put("wifiName",element.getNodeValue());
                        } else if ("wifiPSW".equals(element.getNodeName())) {
                            info.put("wifiPSW",element.getNodeValue());
                        }else if ("deviceName".equals(element.getNodeName())) {
                            info.put("deviceName",element.getNodeValue());
                        }

                    }

                }

                // 把person对象加入到集合中
                list.add(info);

            }
            //关闭输入流
            is.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }
}
