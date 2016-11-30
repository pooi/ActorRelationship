package tk.twpooi.actor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ActorRelationship {
	
	public static FloydAlgorithm floyd = null;
	public static Color bgColor = new Color(34, 34, 37);
	public static Color bgDarkColor = new Color(20, 20, 20);
	public static Color headerColor = new Color(205, 91, 85);

	public static void main(String[] args) {
		
		MainFrame frame = new MainFrame();
		
	}

}

class SubFrame extends JFrame{
	
	private final int IMAGE_W = 74;
	private final int IMAGE_H = 107;
	
	private int totalYear;
	private ArrayList<HashMap<String, String>> list;
	
	SubFrame(int total, ArrayList<HashMap<String, String>> list, Rectangle r){
		
		this.totalYear = total;
		this.list = list;
		
		double height = r.getHeight();
		if(height < height/4*list.size()){
			height = height/4*list.size();
		}
		
		this.setLayout(new BorderLayout());
		
		if(total == 5000){
			
			this.setBounds(10, 10, 1000, 500);
			
			JLabel label = new JLabel("There are no relationship.");
			label.setOpaque(true);
			label.setBackground(ActorRelationship.bgColor);
			label.setForeground(Color.white);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			
			this.add(label, BorderLayout.CENTER);
			
		}else{
			
			int w2 = 120;
			
			int width = w2*total + list.size()*IMAGE_W + 100;
			this.setBounds(10, 10, width, 500);
			
			JLabel titleLabel = new JLabel("총 " + totalYear + "년");
			titleLabel.setOpaque(true);
			titleLabel.setBackground(ActorRelationship.headerColor);
			titleLabel.setForeground(Color.white);
			titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
			titleLabel.setVerticalAlignment(SwingConstants.CENTER);
			titleLabel.setPreferredSize(new Dimension((int)this.getSize().getWidth(), 30));
			
			this.add(titleLabel, BorderLayout.NORTH);
			
			
			this.add(new ActorPanel(), BorderLayout.CENTER);
//			for(HashMap<String, String> h : list){
//				ActorItem item = new ActorItem(h);
//				this.add(item);
//			}
		}
		
		this.setVisible(true);
	}
	
	class ActorPanel extends JPanel{

		private ArrayList<String> actorList;
		private ArrayList<String> actorImgList;
		private ArrayList<HashMap<String, Object>> movieList;
		
		
		ActorPanel(){
			
			this.movieList = ActorRelationship.floyd.getMovieList();
			this.actorList = ActorRelationship.floyd.getActorList();
			this.actorImgList = ActorRelationship.floyd.getActorImgList();
			
			this.setOpaque(true);
			this.setBackground(ActorRelationship.bgColor);
			
		}
		
		@Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			
			int w2 = 120;
			
			int x = 50;
			int y = 100;
			
			for(int i=0; i<list.size(); i++){
				
				HashMap<String, String> h = list.get(i);
				String actor = h.get("actor");

				g.setColor(Color.white);
				
				int nextX = x;

				if(i+1 < list.size()){
					String movie = list.get(i+1).get("movie");
					int movieYear = getMovieYear(movie);
					nextX += IMAGE_W + w2*movieYear;
					
					
					g.setFont(new Font(this.getFont().getFontName(), Font.PLAIN, 12));
					Font font = getFont();
					FontMetrics metrics = getFontMetrics(font);
					

					int titleX = (nextX + x+IMAGE_W)/2 - metrics.stringWidth(movie)/2;
					g.drawString(movie, titleX, y+IMAGE_H/2);
					
					String yText = String.format("(%d) - %d년", 2017-movieYear, movieYear);
					titleX = (nextX + x+IMAGE_W)/2 - metrics.stringWidth(yText)/2;
					g.drawString(yText, titleX, y+10+IMAGE_H/2+20);
				}
				

				g.setFont(new Font(this.getFont().getFontName(), Font.PLAIN, 14));
				g.drawString(actor, x, y);
				g.drawLine(x + IMAGE_W, y+10+IMAGE_H/2, nextX, y+10+IMAGE_H/2);
				
				
				BufferedImage image = null;
				try{
					String path = actorImgList.get(actorList.indexOf(actor));
					URL url = new URL(path);
					image = ImageIO.read(url);
					g.drawImage(image, x, y+10, x+IMAGE_W, y+10+IMAGE_H, 0, 0, image.getWidth(), image.getHeight(), null);

				}catch(Exception e){
					g.setColor(Color.gray);
					g.fillRect(x, y+10, IMAGE_W, IMAGE_H);
				}
				
				x = nextX;
				
			}
			
		}
		
		public int getMovieYear(String title){
			
			int year=0;
			
			if(title.equals("header")){
				return 0;
			}
			
			for(HashMap<String, Object> h : movieList){
				if(h.get("title").equals(title)){
					year = (int)h.get("year");
					
					break;
				}
			}
			
			return 2017-year;
			
		}
		
	}
	
	class ActorItem extends JPanel{
		
		private HashMap<String, String> item;
		private ArrayList<String> actorList;
		private ArrayList<String> actorImgList;
		private ArrayList<HashMap<String, Object>> movieList;
		
		private Color bgColor = ActorRelationship.bgColor;
		private Color bgDarkColor = ActorRelationship.bgDarkColor;
		private Color headerColor = ActorRelationship.headerColor;
		
		ActorItem(HashMap<String, String> item){
			
			this.item = item;
			this.movieList = ActorRelationship.floyd.getMovieList();
			this.actorList = ActorRelationship.floyd.getActorList();
			this.actorImgList = ActorRelationship.floyd.getActorImgList();
			
			GridBagLayout gbl = new GridBagLayout();
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			this.setLayout(gbl);
			this.setOpaque(true);
			this.setBackground(bgColor);
			
			String actor = item.get("actor");
			String movie = item.get("movie");

			JLabel movieLabel = new JLabel(getMovieInfo(movie));
			movieLabel.setHorizontalAlignment(SwingConstants.CENTER);
			movieLabel.setOpaque(true);
			movieLabel.setBackground(bgDarkColor);
			movieLabel.setForeground(Color.white);
			addGrid(gbl, gbc, movieLabel, 0, 0, 3, 1, 3, 1);
			if(movie.equals("header")){
				movieLabel.setText("총 " + totalYear + "년");
				movieLabel.setBackground(headerColor);
			}

			if(actorImgList != null){
				BufferedImage image = null;
				try{
					String path = actorImgList.get(actorList.indexOf(actor));
					URL url = new URL(path);
					image = ImageIO.read(url);

					JLabel actorImgLabel = new JLabel(new ImageIcon(image));
					actorImgLabel.setOpaque(true);
					actorImgLabel.setBackground(bgColor);
					addGrid(gbl, gbc, actorImgLabel, 0, 1, 1, 2, 1, 2);

				}catch(Exception e){
					//e.printStackTrace();
					System.out.println(e.getMessage());
				}
			}

			JLabel actorLabel = new JLabel(actor);
			actorLabel.setOpaque(true);
			actorLabel.setBackground(bgColor);
			actorLabel.setForeground(Color.white);
			actorLabel.setHorizontalAlignment(SwingConstants.CENTER);
			addGrid(gbl, gbc, actorLabel, 1, 1, 2, 2, 2, 2);

			this.setVisible(true);
			
		}
		
		private void addGrid(GridBagLayout gbl, GridBagConstraints gbc, Component c,  
				int gridx, int gridy, int gridwidth, int gridheight, int weightx, int weighty) {
			gbc.gridx = gridx;
			gbc.gridy = gridy;
			gbc.gridwidth = gridwidth;
			gbc.gridheight = gridheight;
			gbc.weightx = weightx;
			gbc.weighty = weighty;
			gbl.setConstraints(c, gbc);
			add(c);
		}
		
		private String getMovieInfo(String movieName){
			
			String s = "";
			
			for(HashMap<String, Object> h : movieList){
				if(h.get("title").equals(movieName)){
					int year = (int)h.get("year");
					
					s = movieName + "(" + year + ")" + " - " + (2017-year) + "년";
					return s;
				}
			}
			
			return s;
			
		}
		
		
	}
	
}

class ListFrame extends JFrame{
	
	private ArrayList<ArrayList<HashMap<String, String>>> list;
	
	private JList jList;
	
	ListFrame(ArrayList<ArrayList<HashMap<String, String>>> list){
		
		this.list = list;
		
		this.setSize(360, 640);
		this.setLayout(new BorderLayout());
		
		JLabel info = new JLabel("총 " + list.size() + "명");
		info.setPreferredSize(new Dimension((int)getSize().getWidth(), 30));
		info.setOpaque(true);
		info.setBackground(ActorRelationship.headerColor);
		info.setForeground(Color.white);
		info.setHorizontalAlignment(SwingConstants.CENTER);
		info.setVerticalAlignment(SwingConstants.CENTER);
		this.add(info, BorderLayout.NORTH);
		
		jList = new JList(getListInfo());
		jList.setOpaque(true);
		jList.setBackground(ActorRelationship.bgColor);
		jList.setForeground(Color.white);
		jList.addMouseListener(new MouseAdapter(){
			
			public void mouseClicked(MouseEvent evt) {
		        JList tempList = (JList)evt.getSource();
		        if (evt.getClickCount() == 2) {
		            int index = tempList.locationToIndex(evt.getPoint());
		            
		            ArrayList<HashMap<String, String>> a = list.get(index);
					
					String start = a.get(0).get("actor");
					String finish = a.get(a.size()-1).get("actor");
		            SubFrame f = new SubFrame(ActorRelationship.floyd.getRelationship(start, finish), a, getBounds());
		        }
		    }
			
		});
		JScrollPane scrollPane = new JScrollPane(jList);
		
		this.add(scrollPane, BorderLayout.CENTER);
		
		
		this.setVisible(true);
		
	}
	
	private Rectangle getRec(){
		return this.getBounds();
	}
	
	private String[] getListInfo(){
		
		String[] returnList = new String[list.size()];
		
		for(int i=0; i<list.size(); i++){
			
			ArrayList<HashMap<String, String>> a = list.get(i);
			
			String start = a.get(0).get("actor");
			String finish = a.get(a.size()-1).get("actor");
			String s = String.format("%3d : %s -> %s (%d년, %d명 거침)",
					i+1,
					start,
					finish,
					ActorRelationship.floyd.getRelationship(start, finish),
					Math.max(0, a.size()-2)
			);
			
			returnList[i] = s;
		}
		
		return returnList;
		
	}
	
	
}

class MainFrame extends JFrame{
	
	private FloydPanel floydPanel;
	private SearchPanel yearPanel;
	private SearchPanel actorPanel;
	
	MainFrame(){
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(360, 640);
		
		BorderLayout bl = new BorderLayout();
		this.setLayout(bl);
		
		yearPanel = new SearchPanel(this);
		yearPanel.setField1Text("2016");
		yearPanel.setField2Text("2014");
		yearPanel.setSearchBtnAction(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				String a = yearPanel.getField1Text();
				String b = yearPanel.getField2Text();
				
				try{
					Integer.parseInt(a);
					Integer.parseInt(b);

					clicked(a, b);
				}catch(Exception exception){
					JOptionPane.showMessageDialog(null, "잘못된 입력입니다.", "경고", JOptionPane.WARNING_MESSAGE);
				}
				
			}
		});
		
		JButton xlsxBtn = new JButton("Xlsx에서 가져오기");
		xlsxBtn.setOpaque(true);
		xlsxBtn.setBackground(ActorRelationship.bgDarkColor);
		xlsxBtn.setForeground(Color.white);
		xlsxBtn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.darkGray),
				BorderFactory.createEmptyBorder(5,  5,  5,  5)));
		xlsxBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clicked();
			}
		});
		yearPanel.addAdditionComponents(xlsxBtn);
		
		
		this.add(yearPanel, BorderLayout.NORTH);
		
		
		floydPanel = new FloydPanel(this);
		this.add(floydPanel, BorderLayout.CENTER);
		
		
		actorPanel = new SearchPanel(this);
		actorPanel.setSearchBtnEnabled(false);
		//actorPanel.showAdditionButtonPanel();
		actorPanel.setEnabled(false);
		actorPanel.setSearchBtnAction(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String a = actorPanel.getField1Text();
				String b = actorPanel.getField2Text();
				
				if((a.equals("") || a == null) && (b.equals("") || b == null)){
					JOptionPane.showMessageDialog(null, "배우를 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
				}else if((a.equals("") || a == null)){
					JOptionPane.showMessageDialog(null, "첫번째 배우를 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
				}else if((b.equals("") || b == null)){
					JOptionPane.showMessageDialog(null, "두번째 배우를 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
				}else{
					
					boolean check;
					
					try{
						int c = Integer.parseInt(b);
						check = false;
					}catch(Exception e2){
						check = true;
					}
					
					
					if(check){
						ArrayList<HashMap<String, String>> list = ActorRelationship.floyd.getPath(a, b);
						
						if(list.size() != 0){
							SubFrame f = new SubFrame(ActorRelationship.floyd.getRelationship(a, b), list, getBound());
						}
					}else{
						ListFrame f = new ListFrame( ActorRelationship.floyd.getPathFromYear(a, Integer.parseInt(b)) );
					}
					
				}
			}
		});
		
		JButton addActorBtn1 = new JButton("basicGraph 출력");
		JButton addActorBtn2 = new JButton("dGraph 출력");
		JButton addActorBtn3 = new JButton("pGraph 출력");
		JButton addActorBtn4 = new JButton("mTitle 출력");
		
		addActorBtn1.setOpaque(true);
		addActorBtn1.setBackground(ActorRelationship.bgDarkColor);
		addActorBtn1.setForeground(Color.white);
		addActorBtn1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.darkGray),
				BorderFactory.createEmptyBorder(5,  5,  5,  0)));
		
		addActorBtn2.setOpaque(true);
		addActorBtn2.setBackground(ActorRelationship.bgDarkColor);
		addActorBtn2.setForeground(Color.white);
		addActorBtn2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.darkGray),
				BorderFactory.createEmptyBorder(5,  5,  5,  0)));
		
		addActorBtn3.setOpaque(true);
		addActorBtn3.setBackground(ActorRelationship.bgDarkColor);
		addActorBtn3.setForeground(Color.white);
		addActorBtn3.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, Color.darkGray),
				BorderFactory.createEmptyBorder(5,  5,  5,  0)));
		
		addActorBtn4.setOpaque(true);
		addActorBtn4.setBackground(ActorRelationship.bgDarkColor);
		addActorBtn4.setForeground(Color.white);
		addActorBtn4.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.darkGray),
				BorderFactory.createEmptyBorder(5,  5,  5,  0)));
		
		addActorBtn1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new Thread(){
					public void run(){
						yearPanel.setEnabled(false);
						actorPanel.setAdditionPanelEnabled(false);
						
						ActorRelationship.floyd.printBasicGraph();
						
						actorPanel.setAdditionPanelEnabled(true);
						yearPanel.setEnabled(true);
					}
				}.start();
			}
		});
		addActorBtn2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new Thread(){
					public void run(){
						yearPanel.setEnabled(false);
						actorPanel.setAdditionPanelEnabled(false);
						
						ActorRelationship.floyd.printdGraph();
						
						actorPanel.setAdditionPanelEnabled(true);
						yearPanel.setEnabled(true);
					}
				}.start();
			}
		});
		addActorBtn3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new Thread(){
					public void run(){
						yearPanel.setEnabled(false);
						actorPanel.setAdditionPanelEnabled(false);
						
						ActorRelationship.floyd.printpGraph();
						
						actorPanel.setAdditionPanelEnabled(true);
						yearPanel.setEnabled(true);
					}
				}.start();
			}
		});
		addActorBtn4.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new Thread(){
					public void run(){
						yearPanel.setEnabled(false);
						actorPanel.setAdditionPanelEnabled(false);
						
						ActorRelationship.floyd.printmTitle();
						
						actorPanel.setAdditionPanelEnabled(true);
						yearPanel.setEnabled(true);
					}
				}.start();
			}
		});
		actorPanel.addAdditionComponents(addActorBtn1, addActorBtn2, addActorBtn3, addActorBtn4);
		actorPanel.setAdditionPanelEnabled(false);
		
		this.add(actorPanel, BorderLayout.SOUTH);
		
		
		this.setVisible(true);
		
	}
	
	public Rectangle getBound(){
		return this.getBounds();
	}
	
	public void clicked(){
		
		ActorRelationship.floyd = new FloydAlgorithm(this);
		ActorRelationship.floyd.start();
		
	}
	
	public void clicked(String a, String b){
		
		ActorRelationship.floyd = new FloydAlgorithm(a, b, this);
		ActorRelationship.floyd.start();
		
	}
	
	public FloydPanel getFloydPanel(){
		return floydPanel;
	}
	public SearchPanel getYearPanel(){
		return yearPanel;
	}
	public SearchPanel getActorPanel(){
		return actorPanel;
	}
	
}

class SearchPanel extends JPanel implements ComponentListener{
	
	private MainFrame parent;
	
	private GridBagLayout gbl;
	private GridBagConstraints gbc;
	
	private JTextField field1;
	private JTextField field2;
	private JButton searchBtn;
	
	private JPanel additionPanel;
//	private JButton additionBtn1;
//	private JButton additionBtn2;
	
	SearchPanel(MainFrame parent){
		
		this.parent = parent;
		
		gbl = new GridBagLayout();
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		this.setLayout(gbl);
		this.setOpaque(true);
		this.setBackground(ActorRelationship.bgColor);
		this.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.darkGray));
		
		field1 = new JTextField();
		field2 = new JTextField();
		searchBtn = new JButton("Search");
		
		addGrid(gbl, gbc, field1, 0, 0, 2, 1, 5, 1);
		addGrid(gbl, gbc, field2, 0, 1, 2, 1, 5, 1);
		addGrid(gbl, gbc, searchBtn, 3, 0, 1, 2, 1, 1);
		

		field1.setOpaque(true);
		field1.setBackground(ActorRelationship.bgColor);
		field1.setForeground(Color.white);
		field1.setCaretColor(Color.white);
		field1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.darkGray),
				BorderFactory.createEmptyBorder(5,  5,  5,  0)));

		field2.setOpaque(true);
		field2.setBackground(ActorRelationship.bgColor);
		field2.setForeground(Color.white);
		field2.setCaretColor(Color.white);
		field2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.darkGray),
				BorderFactory.createEmptyBorder(5,  5,  5,  0)));
		
		searchBtn.setOpaque(true);
		searchBtn.setBackground(ActorRelationship.headerColor);
		searchBtn.setForeground(Color.white);
		searchBtn.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.white));
		
		setLabelSize();
		
		this.setVisible(true);
		
	}
	

	private void addGrid(GridBagLayout gbl, GridBagConstraints gbc, Component c,  
			int gridx, int gridy, int gridwidth, int gridheight, int weightx, int weighty) {
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbl.setConstraints(c, gbc);
		add(c);
	}
	
	public void setEnabled(boolean b){
		field1.setEnabled(b);
		field2.setEnabled(b);
		searchBtn.setEnabled(b);
		if(additionPanel != null){
			setAdditionPanelEnabled(b);
		}
	}
	
	public void addAdditionComponents(Component c){
		
		additionPanel = new JPanel();
		
		additionPanel.setOpaque(true);
		additionPanel.setBackground(ActorRelationship.bgDarkColor);
		
		additionPanel.setLayout(new GridLayout(1, 1));
		additionPanel.add(c);
		additionPanel.setVisible(true);
		
		addGrid(gbl,gbc, additionPanel, 0, 2, 4, 1, 1, 1);
		
	}
	
	public void addAdditionComponents(Component c1, Component c2){
		
		additionPanel = new JPanel();
		
		additionPanel.setOpaque(true);
		additionPanel.setBackground(ActorRelationship.bgDarkColor);
		
		additionPanel.setLayout(new GridLayout(1, 2));
		additionPanel.add(c1);
		additionPanel.add(c2);
		additionPanel.setVisible(true);
		
		addGrid(gbl,gbc, additionPanel, 0, 2, 4, 1, 1, 1);
		
	}
	
	public void addAdditionComponents(Component c1, Component c2, Component c3, Component c4){
		
		additionPanel = new JPanel();
		
		additionPanel.setOpaque(true);
		additionPanel.setBackground(ActorRelationship.bgDarkColor);
		
		additionPanel.setLayout(new GridLayout(2, 2));
		additionPanel.add(c1);
		additionPanel.add(c2);
		additionPanel.add(c3);
		additionPanel.add(c4);
		additionPanel.setVisible(true);
		
		addGrid(gbl,gbc, additionPanel, 0, 2, 4, 1, 1, 1);
		
	}

	public void setSearchBtnText(String t){
		searchBtn.setText(t);
	}
	public void setField1Text(String t){
		field1.setText(t);
	}
	public void setField2Text(String t){
		field2.setText(t);
	}
	public void setSearchBtnEnabled(boolean b){
		searchBtn.setEnabled(b);
	}
	public void setSearchBtnAction(ActionListener a){
		searchBtn.addActionListener(a);
	}
	private void setLabelSize(){
		this.setPreferredSize(new Dimension(parent.getWidth(), (int)(parent.getHeight()*0.2)));
	}
	public void setAdditionPanelEnabled(boolean b){
		additionPanel.setEnabled(b);
		for(Component c : additionPanel.getComponents()){
			c.setEnabled(b);
		}
	}
	
	public String getField1Text(){
		return field1.getText();
	}
	public String getField2Text(){
		return field2.getText();
	}
	
	
	@Override
	public void componentResized(ComponentEvent e) {
		setLabelSize();
	}

	@Override
	public void componentMoved(ComponentEvent e) {}
	@Override
	public void componentShown(ComponentEvent e) {}
	@Override
	public void componentHidden(ComponentEvent e) {}
	
}

class FloydPanel extends JPanel{
	
	private MainFrame parent;
	private JLabel stateLabel;
	
	FloydPanel(MainFrame parent){
		
		this.parent = parent;
		
		BorderLayout bl = new BorderLayout();
		this.setLayout(bl);
		this.setOpaque(true);
		this.setBackground(ActorRelationship.bgColor);
		
		stateLabel = new JLabel();
		stateLabel.setHorizontalAlignment(SwingConstants.CENTER);
		stateLabel.setVerticalAlignment(SwingConstants.CENTER);
		this.add(stateLabel, BorderLayout.CENTER);
		
	}
	
	public JLabel getStateLabel(){
		return stateLabel;
	}

	public int getParentWidth(){
		return this.getWidth();
	}
	
	
	
}


class FloydAlgorithm extends Thread{
	
	private boolean isXlsx = false;
	private String a, b;
	private MainFrame parent;
	private JLabel label;
	private boolean isDataReady = false;
	
	private ArrayList<String> actorList;
	private ArrayList<String> actorImgList;
	private ArrayList<HashMap<String, Object>> movieList;
	
	private int[][] basicGraph;
	private int[][] dGraph;
	private int[][] pGraph;
	private String[][] movieTitle;
	
	private ArrayList<HashMap<String, String>> pathList;
	
	private ArrayList<HashMap<String, String>> progressState;
	
	public boolean getIsReady(){
		return isDataReady;
	}
	
	FloydAlgorithm(MainFrame parent){
		
		isXlsx = true;
		this.parent = parent;
		this.label = parent.getFloydPanel().getStateLabel();
		this.progressState = new ArrayList<>();
		this.actorImgList = null;
		
	}
	
	FloydAlgorithm(String a, String b, MainFrame parent){
		this.a = a;
		this.b = b;
		this.parent = parent;
		this.label = parent.getFloydPanel().getStateLabel();
		this.progressState = new ArrayList<>();
		this.actorImgList = new ArrayList<>();
	}
	
	private void addState(String text, boolean isNewLine){
		
		if(isNewLine){
			HashMap<String, String> temp = new HashMap<>();
			temp.put("first", text);
			temp.put("second", "");
			progressState.add(temp);
		}else{
			HashMap<String, String> temp = progressState.get(progressState.size()-1);
			temp.put("second", text);
			progressState.set(progressState.size()-1, temp);
		}
		
		String progressText = "<html><font size=4 color=white>";
		for(HashMap<String, String> h : progressState){
			
			String first = h.get("first");
			String second = h.get("second");
			
			progressText = progressText + first + " " + second + "<br>";
			
		}
		progressText += "</font></html>";
		label.setText(progressText);
	}
	
	public void run(){

		isDataReady = false;
		parent.getYearPanel().setEnabled(isDataReady);
		parent.getActorPanel().setEnabled(isDataReady);

		
		addState("Making movie list...", true);
		if(isXlsx){
			movieList = getMovieListFromXlsx();
		}else{
			movieList = getMovieList(a, b);
		}
		if(movieList.size() <= 0){
			parent.getYearPanel().setEnabled(true);
			addState("영화 목록이 없습니다.", true);
			return;
		}
		addState("Done (" + movieList.size() + "개)", false);

		addState("Making actor list...", true);
		actorList = getActorList(movieList);
		addState("Done (" + actorList.size() + "명)", false);
		
		init();
		
		makeBasicGraph();
		
		makeDGraph();

		addState("Complete!", true);
		addState("Please enter the actor name.", true);

		isDataReady = true;
		parent.getYearPanel().setEnabled(isDataReady);
		parent.getActorPanel().setEnabled(isDataReady);
		
	}

	
	private void init(){
		basicGraph = new int[actorList.size()][actorList.size()];
		dGraph = new int[actorList.size()][actorList.size()];
		pGraph = new int[actorList.size()][actorList.size()];
		movieTitle = new String[actorList.size()][actorList.size()];
		
		addState("Initialize Basic Graph...", true);
		for(int i=0; i<actorList.size(); i++){
			addState("(" + (int)((double)i/actorList.size()*100) + "%)", false);
			for(int j=0; j<actorList.size(); j++){
				basicGraph[i][j] = 5000;
			}
		}
		addState("Done", false);
	}
	
	private void makeBasicGraph(){
		
		addState("Making Basic Graph...", true);
		int k=0;
		for(HashMap<String ,Object> temp : movieList){

			addState("(" + (int)((double)k/movieList.size()*100) + "%)", false);
			k+=1;
			
			String title = (String)temp.get("title");
			int year = (int)temp.get("year");
			ArrayList<String> actor = (ArrayList<String>)temp.get("actor");

			for(int i=0; i<actor.size(); i++){
				for(int j=0; j<actor.size(); j++){

					
					String actI = actor.get(i);
					String actJ = actor.get(j);

					int indexI = actorList.indexOf(actI);
					int indexJ = actorList.indexOf(actJ);

					if(actor.get(j).equals(actor.get(i))){
						basicGraph[indexJ][indexI] = 0;
						basicGraph[indexI][indexJ] = 0;
					}else{

						if(basicGraph[indexJ][indexI] > 2017 - year){
							basicGraph[indexJ][indexI] = 2017-year;
							basicGraph[indexI][indexJ] = 2017-year;
							movieTitle[indexJ][indexI] = title;
							movieTitle[indexI][indexJ] = title;
						}

					}

				}
			}
		}
		
		addState("Done", false);
		
	}
	
	private void makeDGraph(){
		
		addState("Making D Graph...", true);
		dGraph = basicGraph.clone();
		
		for(int i=0; i<actorList.size(); i++){
			for(int j=0; j<actorList.size(); j++){
				pGraph[i][j] = -1;
			}
		}
		
		for(int i=0; i<basicGraph.length; i++){
			dGraph[i] = basicGraph[i].clone();
		}
		
		for(int k=0; k<actorList.size(); k++){
			addState("(" + (int)((double)k/actorList.size()*100) + "%)", false);
			for(int i=0; i<actorList.size(); i++){
				for(int j=0; j<actorList.size(); j++){
					
					if(dGraph[i][k] + dGraph[k][j] < dGraph[i][j]){
						pGraph[i][j] = k;
						dGraph[i][j] = dGraph[i][k] + dGraph[k][j];
					}
					
				}
			}
		}
		
		addState("Done", false);
		
	}
	
	private boolean path(int q, int r){
		
		if(pGraph[q][r] != -1){
			if(!path(q, pGraph[q][r])){
				HashMap<String,String> temp = new HashMap<>();
				temp.put("actor", actorList.get(pGraph[q][r]));
				temp.put("movie", movieTitle[q][pGraph[q][r]]);
				pathList.add(temp);
			}
			
			if(!path(pGraph[q][r], r)){
				HashMap<String,String> temp = new HashMap<>();
				temp.put("actor", actorList.get(r));
				temp.put("movie", movieTitle[pGraph[q][r]][r]);
				pathList.add(temp);
			}
			return true;
		}
		
		return false;
		
	}
	
	public int getRelationship(String start, String finish){
		
		int relationship = 0;
		
		int indexA = actorList.indexOf(start);
		int indexB = actorList.indexOf(finish);
		
		if(indexA < 0 && indexB <0){
			JOptionPane.showMessageDialog(null, start + "(과)와 " + finish + "(이)라는 이름을 가진 배우가 없습니다.", "경고", JOptionPane.WARNING_MESSAGE);
		}else if(indexA <0){
			JOptionPane.showMessageDialog(null, start + "(이)라는 이름을 가진 배우가 없습니다.", "경고", JOptionPane.WARNING_MESSAGE);
		}else if(indexB < 0){
			JOptionPane.showMessageDialog(null, finish + "(이)라는 이름을 가진 배우가 없습니다.", "경고", JOptionPane.WARNING_MESSAGE);
		}else{
			
			relationship = dGraph[indexA][indexB];
		
		}
		
		return relationship;
		
	}
	
	public ArrayList<HashMap<String,String>> getPath(String start, String finish){
		
		
		pathList = new ArrayList<>();
		
		int indexA = actorList.indexOf(start);
		int indexB = actorList.indexOf(finish);
		
		if(indexA < 0 && indexB <0){
			JOptionPane.showMessageDialog(null, start + "(과)와 " + finish + "(이)라는 이름을 가진 배우가 없습니다.", "경고", JOptionPane.WARNING_MESSAGE);
		}else if(indexA <0){
			JOptionPane.showMessageDialog(null, start + "(이)라는 이름을 가진 배우가 없습니다.", "경고", JOptionPane.WARNING_MESSAGE);
		}else if(indexB < 0){
			JOptionPane.showMessageDialog(null, finish + "(이)라는 이름을 가진 배우가 없습니다.", "경고", JOptionPane.WARNING_MESSAGE);
		}else{
			
			HashMap<String,String> temp = new HashMap<>();
			temp.put("actor", start);
			temp.put("movie", "header");
			pathList.add(temp);
			
			path(indexA, indexB);
			
			if(pathList.size() == 1){
				HashMap<String,String> temp2 = new HashMap<>();
				temp2.put("actor", finish);
				temp2.put("movie", movieTitle[indexA][indexB]);
				pathList.add(temp2);
			}
		
		}
		
		
		return pathList;
		
	}
	
	public ArrayList<ArrayList<HashMap<String, String>>> getPathFromYear(String start, int year){
		
		ArrayList<ArrayList<HashMap<String, String>>> list = new ArrayList<>();
		
		int indexS = actorList.indexOf(start);
		
		for(int i=0; i<actorList.size(); i++){
			
			if(0 < dGraph[i][indexS] && dGraph[i][indexS] <= year){
				list.add(getPath(start, actorList.get(i)));
			}
			
		}
		
		return list;
		
	}
	
	public ArrayList<String> getActorList(){
		return actorList;
	}
	
	public ArrayList<String> getActorImgList(){
		return actorImgList;
	}
	
	public ArrayList<HashMap<String, Object>> getMovieList(){
		return movieList;
	}
	
	private ArrayList<String> getActorList(ArrayList<HashMap<String, Object>> mList){

		ArrayList<String> querys = new ArrayList<String>();
		if(!isXlsx){
			actorImgList.clear();
		}
		
		for(int i=0; i<mList.size(); i++){
			
			HashMap<String, Object> h  = mList.get(i);
			
			ArrayList<String> list = (ArrayList<String>)h.get("actor");
			
			ArrayList<String> list2;
			if(!isXlsx){
				list2 = (ArrayList<String>)h.get("actorImg");
			}else{
				list2 = new ArrayList<>();
			}
			
			for(int j=0; j<list.size(); j++){
				String s = list.get(j);
				if(!querys.contains(s)){
					querys.add(s);
					if(!isXlsx){
						actorImgList.add(list2.get(j));
					}
				}
			}
			
		}

		return querys;

	}

	private ArrayList<HashMap<String, Object>> getMovieList(String year1, String year2){

		ArrayList<HashMap<String, Object>> querys = new ArrayList<>();
		
		int minYear, maxYear;
		try{
			if(Integer.parseInt(year1) > Integer.parseInt(year2)){
				minYear = Integer.parseInt(year2);
				maxYear = Integer.parseInt(year1);
			}else{
				maxYear = Integer.parseInt(year2);
				minYear = Integer.parseInt(year1);
			}
		}catch(Exception e){
			return null;
		}

		try{
			
			ArrayList<HashMap<String, Object>> movieList;
			
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("movieDB.data"));
			
			movieList = (ArrayList<HashMap<String, Object>>)ois.readObject();
			ois.close();
			
			for(HashMap<String, Object> h : movieList){
				
				int year = (int)h.get("year");
				
				if(minYear <= year && year <= maxYear){
					
					querys.add(h);
					
				}
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
			addState("영화 정보를 가져올 수 없습니다.", true);
		}

		return querys;

	}
	
	private ArrayList<HashMap<String, Object>> getMovieListFromXlsx(){

		ArrayList<HashMap<String, Object>> querys = new ArrayList<>();
		
		try{
			FileInputStream fis = new FileInputStream("koreaMovieList.xlsx");
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			int rowindex = 0;
			int columnindex = 0;

			XSSFSheet sheet = workbook.getSheetAt(0);

			int rows = sheet.getPhysicalNumberOfRows();
			for(rowindex=1; rowindex<rows; rowindex++){

				XSSFRow row = sheet.getRow(rowindex);
				if(row != null){

					HashMap<String, Object> temp = new HashMap<>();
					ArrayList<String>  actorList = new ArrayList<>();

					int cells = row.getPhysicalNumberOfCells();

					XSSFCell cellTitle = row.getCell(1);

					String year = "";
					if(cellTitle == null){
						continue;
					}else{
						switch(cellTitle.getCellType()){
						case XSSFCell.CELL_TYPE_FORMULA:
							year = cellTitle.getCellFormula();
							break;
						case XSSFCell.CELL_TYPE_NUMERIC:
							year = cellTitle.getNumericCellValue()+"";
							break;
						case XSSFCell.CELL_TYPE_STRING:
							year = cellTitle.getStringCellValue()+"";
							break;
						case XSSFCell.CELL_TYPE_BLANK:
							year = cellTitle.getBooleanCellValue()+"";
							break;
						case XSSFCell.CELL_TYPE_ERROR:
							year = cellTitle.getErrorCellValue()+"";
							break;
						}
					}



					temp.put("year", Integer.parseInt(year));


					cellTitle = row.getCell(0);

					String title = "";
					if(cellTitle == null){
						continue;
					}else{
						switch(cellTitle.getCellType()){
						case XSSFCell.CELL_TYPE_FORMULA:
							title = cellTitle.getCellFormula();
							break;
						case XSSFCell.CELL_TYPE_NUMERIC:
							title = cellTitle.getNumericCellValue()+"";
							break;
						case XSSFCell.CELL_TYPE_STRING:
							title = cellTitle.getStringCellValue()+"";
							break;
						case XSSFCell.CELL_TYPE_BLANK:
							title = cellTitle.getBooleanCellValue()+"";
							break;
						case XSSFCell.CELL_TYPE_ERROR:
							title = cellTitle.getErrorCellValue()+"";
							break;
						}
					}

					temp.put("title", title);




					cellTitle = row.getCell(2);

					String content = "";
					if(cellTitle == null){
						continue;
					}else{
						switch(cellTitle.getCellType()){
						case XSSFCell.CELL_TYPE_FORMULA:
							content = cellTitle.getCellFormula();
							break;
						case XSSFCell.CELL_TYPE_NUMERIC:
							content = cellTitle.getNumericCellValue()+"";
							break;
						case XSSFCell.CELL_TYPE_STRING:
							content = cellTitle.getStringCellValue()+"";
							break;
						case XSSFCell.CELL_TYPE_BLANK:
							content = cellTitle.getBooleanCellValue()+"";
							break;
						case XSSFCell.CELL_TYPE_ERROR:
							content = cellTitle.getErrorCellValue()+"";
							break;
						}
					}


					for(String s : content.split(",")){
						actorList.add(s);
					}

					temp.put("actor", actorList);
					
					querys.add(temp);
					
				}

			}

		}catch(Exception e){
			e.printStackTrace();
			addState("영화 엑셀파일을 가져올 수 없습니다.", true);
		}

		return querys;

	}
	
	
	public void printBasicGraph(){
		
		try {
			addState("Print Basic Graph to .txt ...", true);
			BufferedWriter out = new BufferedWriter(new FileWriter("basicGraph.txt"));
			
			for(int i=0; i<=actorList.size(); i++){
				addState("(" + (int)((double)(i+1)/actorList.size()*100) +"%)", false);
				String s = "";
				
				if(i==0){
					s += ",";
					for(String t : actorList){
						s += t + ",";
					}
				}else{
					s += actorList.get(i-1) + ",";
					for(int j=0; j<actorList.size(); j++){
						
						s += basicGraph[i-1][j] + ",";
						
					}
				}
				
				out.write(s); out.newLine();
			}

			out.close();
			addState("Done", false);
		} catch (IOException e) {
			e.printStackTrace();
			addState("Error", false);
		}
		
	}

	public void printdGraph(){
		
		try {
			addState("Print D Graph to .txt ...", true);
			BufferedWriter out = new BufferedWriter(new FileWriter("dGraph.txt"));
			
			for(int i=0; i<=actorList.size(); i++){
				addState("(" + (int)((double)(i+1)/actorList.size()*100) +"%)", false);
				String s = "";
				
				if(i==0){
					s += ",";
					for(String t : actorList){
						s += t + ",";
					}
				}else{
					s += actorList.get(i-1) + ",";
					for(int j=0; j<actorList.size(); j++){
						
						s += dGraph[i-1][j] + ",";
						
					}
				}
				
				out.write(s); out.newLine();
			}

			out.close();
			addState("Done", false);
		} catch (IOException e) {
			e.printStackTrace();
			addState("Error", false);
		}
		
	}

	public void printpGraph(){
		
		try {
			addState("Print P Graph to .txt ...", true);
			BufferedWriter out = new BufferedWriter(new FileWriter("pGraph.txt"));
			
			for(int i=0; i<=actorList.size(); i++){
				addState("(" + (int)((double)(i+1)/actorList.size()*100) +"%)", false);
				String s = "";
				
				if(i==0){
					s += ",";
					for(String t : actorList){
						s += t + ",";
					}
				}else{
					s += actorList.get(i-1) + ",";
					for(int j=0; j<actorList.size(); j++){
						
						String str;
						if(pGraph[i-1][j] < 0){
							str = " ";
						}else{
							str = actorList.get(pGraph[i-1][j]);
						}
						s += str + ",";
						
					}
				}
				
				out.write(s); out.newLine();
			}

			out.close();
			addState("Done", false);
		} catch (IOException e) {
			e.printStackTrace();
			addState("Error", false);
		}
		
	}
	
	public void printmTitle(){
		
		try {
			addState("Print Movie Title to .txt ...", true);
			BufferedWriter out = new BufferedWriter(new FileWriter("mTitle.txt"));
			
			for(int i=0; i<=actorList.size(); i++){
				addState("(" + (int)((double)(i+1)/actorList.size()*100) +"%)", false);
				String s = "";
				
				if(i==0){
					s += ",";
					for(String t : actorList){
						s += t + ",";
					}
				}else{
					s += actorList.get(i-1) + ",";
					for(int j=0; j<actorList.size(); j++){
						
						String str = movieTitle[i-1][j];
						if(str != null){
							str = str.replaceAll(",", "");
						}else{
							str = " ";
						}
						
						s += str + ",";
						
					}
				}
				
				out.write(s); out.newLine();
			}

			out.close();
			addState("Done", false);
		} catch (IOException e) {
			e.printStackTrace();
			addState("Error", false);
		}
		
	}
	
}