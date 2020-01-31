import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.File;
import java.io.IOException;

public class App extends JFrame {

    ArrayList<Good> list = new ArrayList<>();

    App(String title) {
        super((title));
        Container container = getContentPane();

        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        menu.add(fileMenu);
        JMenuItem open = new JMenuItem("Open...");
        fileMenu.add(open);
        JMenuItem save = new JMenuItem("Save to XML-file...");
        fileMenu.add(save);
        JMenuItem show = new JMenuItem("Show");
        fileMenu.add(show);
        this.setJMenuBar(menu);

        JTextArea out = new JTextArea();
        out.setEditable(false);
        out.setColumns(40);

        JDialog dialog = new JDialog(this, "smth", false);
        dialog.setSize(400, 500);
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JButton addData = new JButton("Add data");
        JButton showList = new JButton("Show list");
        container.add(addData, BorderLayout.NORTH);
        container.add(showList, BorderLayout.WEST);
        container.add(out, BorderLayout.EAST);

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                out.setText("");
                try {
                    if (list.isEmpty()) {
                        throw new NullPointerException("List is empty");
                    }
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Save...");
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int result = fileChooser.showSaveDialog(App.this);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        String path = fileChooser.getSelectedFile().getAbsolutePath();
                        String filename = JOptionPane.showInputDialog("Enter filename");
                        path += "\\";
                        path += filename;
                        createXML(path);

                        JOptionPane.showMessageDialog(App.this,
                                "File '" + fileChooser.getSelectedFile() +
                                        "\\" + filename + " saved");
                    }
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(container, ex.getMessage());
                }
            }
        });

        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                out.setText("");
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Open...");
                fileChooser.setAcceptAllFileFilterUsed(false);
                int result = fileChooser.showOpenDialog(null);
                if(result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        parsing(list, file);
                        dialog.setVisible(true);
                        JTextArea text = new JTextArea();
                        text.setEditable(false);
                        dialog.add(text);
                        for(Good el : list) {
                            text.append(el + "\n");
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(container, ex.getMessage());
                    }
                }
            }
        });

        show.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (list.isEmpty()) {
                        throw new NullPointerException("List is empty");
                    }
                    dialog.setVisible(true);
                    JTextArea text = new JTextArea();
                    text.setEditable(false);
                    dialog.add(text);
                    for (Good el : list) {
                        text.append(el + "\n");
                    }
                } catch(NullPointerException ex) {
                    JOptionPane.showMessageDialog(container, ex.getMessage());
                }
            }
        });

        addData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                out.setText("");
                String infoString = JOptionPane.showInputDialog("Enter name, country and volume");
                infoString.trim();
                try {
                    String[] info = infoString.split(" ");
                    Good good = new Good();
                    good.setName(info[0]);
                    good.setCountry(info[1]);
                    int volume = Integer.parseInt(info[2]);
                    if(volume <= 0) {
                        throw new IllegalArgumentException();
                    } else {
                        good.setVolume(volume);
                    }
                    list.add(good);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(container, "Illegal value");
                }

            }
        });

        showList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                out.setText("");
                try {
                    if(list.isEmpty()) {
                        throw new NullPointerException("List is empty");
                    } else {
                        ArrayList<Good> tempList = (ArrayList<Good>)list.clone();
                        Collections.sort(tempList, new Comparator<Good>() {
                            @Override
                            public int compare(Good o1, Good o2) {
                                Integer v1 = o1.getVolume();
                                return -(v1.compareTo(o2.getVolume()));
                            }
                        });
                        for(Good el : tempList) {
                            out.append(el.getCountry() + "\n");
                        }
                    }
                } catch(NullPointerException ex) {
                    JOptionPane.showMessageDialog(container, ex.getMessage());
                }
            }
        });

        setPreferredSize(new Dimension(500, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }


    public void parsing(ArrayList<Good> list, File file) throws ParserConfigurationException,
            IOException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);

        list.clear();

        NodeList nodeList = document.getElementsByTagName("good");
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()){
                Element element = (Element) node;
                Element name = (Element) element.getElementsByTagName("name").item(0);
                Element country = (Element) element.getElementsByTagName("country").item(0);
                Element volume = (Element) element.getElementsByTagName("volume").item(0);
                Good good = new Good(name.getTextContent(), country.getTextContent(), Integer.parseInt(volume.getTextContent()));
                list.add(good);
            }
        }
    }

    public void createXML(String path) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element rootElement = document.createElement("goods");
        document.appendChild(rootElement);
        for(Good el : list) {
            Element good = document.createElement("good");

            Element nodeName = document.createElement("name");
            nodeName.appendChild(document.createTextNode(el.getName()));
            good.appendChild(nodeName);

            Element nodeCountry = document.createElement("country");
            nodeCountry.appendChild(document.createTextNode(el.getCountry()));
            good.appendChild(nodeCountry);

            Element nodeVolume = document.createElement("volume");
            nodeVolume.appendChild(document.createTextNode(String.valueOf(el.getVolume())));
            good.appendChild(nodeVolume);

            rootElement.appendChild(good);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);

        StreamResult file = new StreamResult(new File(path + ".xml"));

        transformer.transform(source, file);
    }
}