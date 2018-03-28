package jtetravex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Domovoy
 * Date: 16.02.2007
 * Time: 17:22:03
 */

public class MainFrame extends JFrame {
	private static MainFrame mainFrame;
	private int side;
	private JPanel glassPane;
	private TetraPanel tetraPanel;
	private JLabel timeLabel;
	private Timer timer;
	private Nums[][] rightModel;
	private ImageIcon aboutIcon = new ImageIcon("images/aboutTetra.gif");
	private boolean isWin;
	private boolean isCheater;
	private boolean isSolved;
	private JMenu moveMenu;
	public long delta = 0;
	private int testCount = 0;
	private Map topMap;

	private MainFrame() {
		super();
		setTitle("JTetraVex");
		setSize(640, 480);
		createMenuBar();
		setIconImage(Toolkit.getDefaultToolkit().createImage("images/JTetraVex.png"));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		newGame();
	}

	private void createGlassPane() {
		glassPane = new JPanel() {
			public void paint(Graphics g) {
				super.paint(g);
			}
		};
		glassPane.setLayout(null);
		glassPane.setOpaque(false);
	}

	public void setTopTetra(Tetra tetra) {
//		glassPane.removeAll();
//		tetra.setBounds(rect);
		glassPane.add(tetra);

	}

	public void clearGlassPane() {
		glassPane.removeAll();
		repaint();
	}

	private void createStartField() {
		Nums model[][] = createTetraNums();
		rightModel = new Nums[side][side];
		for (int x = 0; x < side; x++) {
			System.arraycopy(model[x], 0, rightModel[x], 0, side);
		}
		Nums nums[][] = mixNums(model);
		for (int x = 0; x < side; x++) {
			for (int y = 0; y < side; y++) {
				Tetra tetra = new Tetra(nums[x][y]);
				tetraPanel.add(tetra, "1" + x + y);
			}
		}
	}

	private Nums[][] mixNums(Nums[][] nums) {
		int x, y, x1, y1;
		Nums buf;
		Random r = new Random();
		for (int i = 0; i < side * side; i++) {
			buf = nums[x = r.nextInt(side)][y = r.nextInt(side)];
			nums[x][y] = nums[x1 = r.nextInt(side)][y1 = r.nextInt(side)];
			nums[x1][y1] = buf;
		}
		return nums;
	}

	private Nums[][] createTetraNums() {
		Random rnd = new Random();
		Nums nums[][] = new Nums[side][side];
		int left, up, right, down;
		for (int x = 0; x < side; x++) {
			for (int y = 0; y < side; y++) {
				if (x - 1 >= 0 && nums[x - 1][y] != null) {
					left = nums[x - 1][y].right;
				} else {
					left = rnd.nextInt(10);
				}

				if (y - 1 >= 0 && nums[x][y - 1] != null) {
					up = nums[x][y - 1].down;
				} else {
					up = rnd.nextInt(10);
				}

				if (x + 1 < side && nums[x + 1][y] != null) {
					right = nums[x + 1][y].left;
				} else {
					right = rnd.nextInt(10);
				}

				if (y + 1 < side && nums[x][y + 1] != null) {
					down = nums[x][y + 1].up;
				} else {
					down = rnd.nextInt(10);
				}

				nums[x][y] = new Nums(left, up, right, down);
			}
		}
		return nums;
	}

	public static MainFrame getMainFrame() {
		if (mainFrame == null) {
			mainFrame = new MainFrame();
		}
		return mainFrame;
	}

	public void win() {
		if (!isWin()) {
			isWin = true;
/*			if (isTop()) {todo
				addRecord();
			}
*/
			timer.setRun(false);
			moveMenu.setEnabled(false);
			if (isSolved) {
				return;
			}
			boolean again = JOptionPane.showConfirmDialog(this,
			                                              "Вы решили головоломку! Хотите сыграть еще?",
			                                              "", JOptionPane.YES_NO_OPTION) ==
			                                                                             JOptionPane.YES_OPTION;
			if (again) newGame();
		}
	}

	private boolean isTop() {
		return false;
	}

	private void newGame() {
		topMap = readTopList();
		isWin = false;
		setCheater(false);
		isSolved = false;
		Config.lock = false;

		getMoveMenu().setEnabled(true);

		if (timer != null) {
			timer.setRun(false);
		}
		side = Config.side;
		int minX = (int) (80 * (Config.side + 1.5));
		int minY = 80 * Config.side;
		setMinimumSize(new Dimension(minX, minY));

		Container c = new Container();
		c.setLayout(new BorderLayout());
		setContentPane(c);

		tetraPanel = new TetraPanel();

		JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		timeLabel = new JLabel("00:00:00");
		statusPanel.add(timeLabel);

		JButton testButton = new JButton("Тест");
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				test();
			}
		};
		testButton.addActionListener(al);
		statusPanel.add(testButton);

		createStartField();

		createGlassPane();
		clearGlassPane();
		setGlassPane(glassPane);

		c.add(tetraPanel, BorderLayout.CENTER);
		c.add(statusPanel, BorderLayout.SOUTH);
		timer = new Timer();

		setVisible(true);
		glassPane.setVisible(true);
	}

	private Map readTopList() {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream("top"));
			return ((HashMap[]) in.readObject())[side - 2];
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.err.println("File 'top' not found.");
		} catch (IOException e) {
			e.printStackTrace();
		}

		Map defaultTop = new TreeMap();
		Random r = new Random();
		int time = 30 / Config.top;
		for (int i = 0; i < Config.top; i++) {
			String date = DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(r.nextLong()));
			defaultTop.put((30 + (time * i)) * 60, new String[]{"Domovoy", date});
		}
		return defaultTop;
	}

	public TetraPanel getTetraPanel() {
		return tetraPanel;
	}

	public void setTime(String time) {
		timeLabel.setText(time);
		validate();
	}

	private void createMenuBar() {
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str = e.getActionCommand();

				if ("Новая игра".equals(str)) {
					newGame();

				} else if ("Лучшие результаты".equals(str)) {
					showResults();

				} else if ("Выход".equals(str)) {
					System.exit(0);

				} else if ("Решить".equals(str)) {
					isSolved = true;
					tetraPanel.solve();

				} else if ("Подсказка".equals(str)) {
					tetraPanel.cheat();

				} else if ("Помощь".equals(str)) {
					showNotImpl();

				} else if ("О программе".equals(str)) {
					showAbout();

				} else if (str.length() == 1) {
					Config.side = Integer.parseInt(str);
					newGame();

				} else if ("Цифры".equals(str)) {
					Config.view = Config.NUMS;
					repaint();

				} else if ("Круги".equals(str)) {
					Config.view = Config.COLORS_CIRCLE;
					repaint();

				} else if ("Квадраты".equals(str)) {
					Config.view = Config.COLORS_RECTANGLE;
					repaint();

				}

			}
		};
		JMenuBar bar = new JMenuBar();
		JMenu game = new JMenu("Игра");
		bar.add(game);
		createMenuItem("Новая игра", game, listener);
		createMenuItem("Лучшие результаты", game, listener);
		createMenuItem("Выход", game, listener);

		moveMenu = new JMenu("Ход");
		createMenuItem("Подсказка", getMoveMenu(), listener);
		createMenuItem("Решить", getMoveMenu(), listener);
		bar.add(getMoveMenu());

		JMenu view = new JMenu("Вид");
		ButtonGroup group = new ButtonGroup();
		JRadioButtonMenuItem item = new JRadioButtonMenuItem("Цифры");
		if (Config.view == Config.NUMS) {
			item.setSelected(true);
		}
		item.setActionCommand("Цифры");
		item.addActionListener(listener);
		group.add(item);
		view.add(item);
		JMenu colors = new JMenu("Цвета");
		view.add(colors);
		item = new JRadioButtonMenuItem("Квадраты");
		if (Config.view == Config.COLORS_RECTANGLE) {
			item.setSelected(true);
		}
		item.setActionCommand("Квадраты");
		item.addActionListener(listener);
		group.add(item);
		colors.add(item);
		item = new JRadioButtonMenuItem("Круги");
		if (Config.view == Config.COLORS_CIRCLE) {
			item.setSelected(true);
		}
		item.setActionCommand("Круги");
		item.addActionListener(listener);
		group.add(item);
		colors.add(item);
		bar.add(view);

		JMenu size = new JMenu("Размер");
		bar.add(size);
		createRadioButtons(size, listener);
		JMenu help = new JMenu("Справка");
		bar.add(help);
		createMenuItem("Помощь", help, listener);
		createMenuItem("О программе", help, listener);


		setJMenuBar(bar);
	}

	private void createMenuItem(String name, JComponent parent, ActionListener listener) {
		JMenuItem item = new JMenuItem(name);
		item.addActionListener(listener);
		parent.add(item);
	}

	private void showNotImpl() {
		JOptionPane.showMessageDialog(this, "Not implemented yet!", "", JOptionPane.INFORMATION_MESSAGE);
	}

	private void showAbout() {
		JPanel about = new JPanel();
		about.setBorder(BorderFactory.createEmptyBorder());
		about.add(new JLabel("<html>JTetraVex<br>0.1 alpha<br>Copyright (c) Domovoy </html>"));
		JOptionPane.showMessageDialog(this, about, "О программе", JOptionPane.OK_OPTION, aboutIcon);
	}

	private void showResults() {
		if (isCheater) {
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		topMap.put(new Integer(2000), new String[]{"Аноним","12.12.12"}); //todo: save results, isTop(), entryName()
		Set times = topMap.keySet();
		if(times.size()>Config.top){
		}
		String tableModel[][] = new String[Config.top][4];
		String columnNames[] = {"№", "Время","Имя","Дата"};
		Iterator<Integer> i = times.iterator();
		int ind = 0;
		while(i.hasNext()) {
			Integer current = i.next();
			String time = sdf.format(new Date(current * 1000));
			String value[] = (String[])topMap.get(current);
			if(!i.hasNext() && topMap.size()>Config.top){
				topMap.remove(current);
			}else{
			tableModel[ind] = new String[]{"<html><b>"+String.valueOf(ind+1)+"</b></html>", time, value[0], value[1]};}
			ind++;
		}
		
		JTable table = new JTable(tableModel, columnNames);
		table.setEnabled(false);
		table.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		JOptionPane.showMessageDialog(this, table, "Top " + Config.top, JOptionPane.PLAIN_MESSAGE);
	}

	private void createRadioButtons(JComponent parent, ActionListener listener) {
		ButtonGroup group = new ButtonGroup();
		for (int i = 2; i < 10; i++) {
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(i + " x " + i);
			if (i == Config.side) {
				item.setSelected(true);
			}
			item.setActionCommand(String.valueOf(i));
			item.addActionListener(listener);
			group.add(item);
			parent.add(item);
		}
	}

	public Nums[][] getRightModel() {
		return rightModel;
	}

	public boolean isWin() {
		return isWin;
	}

	public void setCheater(boolean cheater) {
		isCheater = cheater;
	}

	public JMenu getMoveMenu() {
		return moveMenu;
	}

	public void test() {
		newGame();
		if (testCount != 0) {
			System.out.println("Среднее время за " + (testCount) + " тестов = " + delta / (testCount));
		}
		testCount++;
		isSolved = true;
		tetraPanel.solve();
	}

}
