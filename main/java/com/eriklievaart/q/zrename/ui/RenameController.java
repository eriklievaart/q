package com.eriklievaart.q.zrename.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import com.eriklievaart.q.api.QView;
import com.eriklievaart.q.api.render.ColorFactory;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.pattern.PatternTool;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class RenameController {
	private static final String VIEW_ID = "q.rename";
	private static final String INITIAL_REGEX = ".*";

	private static final Color INPUT = new Color(100, 100, 100);
	private static final Color WARNING = Color.ORANGE;
	private static final Color ACTIVE = Color.WHITE;
	private static final Color INACTIVE = Color.GRAY;

	private LogTemplate log = new LogTemplate(getClass());

	private VirtualFile directory;

	private Supplier<QMainUi> ui;
	private Supplier<Engine> engine;

	public JPanel mainPanel = new JPanel(new BorderLayout());
	public JPanel listPanel = new JPanel(new GridLayout(0, 2, 5, 0));
	public JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 0, 0));
	public JPanel criteriaPanel = new JPanel(new GridLayout(0, 2, 5, 0));
	public JLabel regexLabel = new JLabel("regex to match files:");
	public JTextField regexField = new JTextField(INITIAL_REGEX);
	public JLabel renameLabel = new JLabel("renaming expression:");
	public JTextField renameField = new JTextField("$0");
	public JList<RenameListElement> fromList = new JList<>();
	public JList<RenameListElement> toList = new JList<>();
	public JButton acceptButton = new JButton("Accept");
	public JButton refreshButton = new JButton("Refresh");

	public RenameController(Supplier<QMainUi> ui, Supplier<Engine> engine) {
		Check.notNull(ui, engine);
		this.ui = ui;
		this.engine = engine;
		initComponents();
	}

	private void initComponents() {
		initCriteria();
		initLists();
		initButtons();
		initMainPanel();
	}

	private void initButtons() {
		buttonPanel.add(acceptButton);
		buttonPanel.add(refreshButton);
	}

	private void initLists() {
		listPanel.add(fromList);
		listPanel.add(toList);
	}

	private void initCriteria() {
		regexLabel.setHorizontalAlignment(JLabel.RIGHT);
		renameLabel.setHorizontalAlignment(JLabel.RIGHT);

		criteriaPanel.add(regexLabel);
		criteriaPanel.add(regexField);
		criteriaPanel.add(renameLabel);
		criteriaPanel.add(renameField);
	}

	private void initMainPanel() {
		mainPanel.add(criteriaPanel, BorderLayout.NORTH);
		mainPanel.add(new JScrollPane(listPanel), BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
	}

	private ListCellRenderer<RenameListElement> createRenameRenderer() {
		ColorFactory factory = c -> ((RenameListElement) c).getForeground();
		return ui.get().createListCellRenderer(factory);
	}

	public void regexUpdated() {
		boolean compiles = PatternTool.isCompilable(regexField.getText());
		regexField.setBackground(compiles ? INPUT : WARNING);

		if (compiles) {
			log.trace("regex compiles; updating lists");
			updateListMatches();
			repaintLists();
		}
	}

	public void updateListFiles() {
		List<? extends VirtualFile> children = directory.getChildrenAdvanced().getAlphabeticallyDirectoriesFirst();
		fromList.setListData(convertToRenameListElements(children));
		toList.setListData(convertToRenameListElements(children));
		updateListMatches();
		createListRenderers();
	}

	private void updateListMatches() {
		ListModel<RenameListElement> fromModel = fromList.getModel();
		ListModel<RenameListElement> toModel = toList.getModel();

		for (int i = 0; i < fromModel.getSize(); i++) {
			RenameListElement fromElement = fromModel.getElementAt(i);
			RenameListElement toElement = toModel.getElementAt(i);

			if (fromElement.getText().matches(regexField.getText())) {
				fromElement.setForeground(ACTIVE);
				toElement.setForeground(ACTIVE);
				toElement.setText(getReplacementText(fromElement));
			} else {
				fromElement.setForeground(INACTIVE);
				toElement.setForeground(INACTIVE);
				toElement.setText(fromElement.getText());
			}
		}
	}

	private void createListRenderers() {
		fromList.setCellRenderer(createRenameRenderer());
		fromList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		toList.setCellRenderer(createRenameRenderer());
		toList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	private String getReplacementText(RenameListElement fromElement) {
		try {
			return fromElement.getText().replaceFirst(regexField.getText(), renameField.getText());
		} catch (IllegalArgumentException | IndexOutOfBoundsException e) {
			log.trace("invalid replace: " + e.getMessage(), e);
			return "*error:* " + e.getMessage();
		}
	}

	private void repaintLists() {
		fromList.validate();
		fromList.repaint();
		toList.validate();
		toList.repaint();
	}

	public void showUi() {
		QMainUi main = ui.get();
		if (!PatternTool.isCompilable(regexField.getText())) {
			regexField.setText(INITIAL_REGEX);
			regexField.setBackground(INPUT);
		}
		if (main != null) {
			showUi(ui.get().getQContext().getActive().getDirectory());
		}
	}

	public void showUi(VirtualFile dir) {
		directory = dir;
		QMainUi main = ui.get();
		if (main == null) {
			return;
		}
		if (dir.getChildren().isEmpty()) {
			JOptionPane.showMessageDialog(null, "No files to rename");
			return;
		}
		updateListFiles();

		QView view = new QView(VIEW_ID, mainPanel);
		view.setLabel("rename");
		main.showView(view);
	}

	private RenameListElement[] convertToRenameListElements(List<? extends VirtualFile> children) {
		RenameListElement[] elements = new RenameListElement[children.size()];
		for (int i = 0; i < children.size(); i++) {
			elements[i] = new RenameListElement(children.get(i));
		}
		return elements;
	}

	public void doRename() {
		try {
			Pattern.compile(regexField.getText());
		} catch (PatternSyntaxException pse) {
			JOptionPane.showMessageDialog(null, "invalid regex: " + pse.getDescription());
			return;
		}
		Engine e = engine.get();
		if (e == null) {
			JOptionPane.showMessageDialog(null, "Q engine unavailable");
			return;
		}
		doRename(e);
	}

	private void doRename(Engine e) {
		ListModel<RenameListElement> model = fromList.getModel();

		for (int i = 0; i < model.getSize(); i++) {
			RenameListElement element = model.getElementAt(i);

			if (element.getText().matches(regexField.getText())) {
				String url = element.getVirtualFile().getUrl().getUrlEscaped();
				String parent = element.getVirtualFile().getParentFile().get().getUrl().getUrlEscaped();
				String newName = element.getText().replaceFirst(regexField.getText(), renameField.getText());
				e.invoke(Str.sub("move -s % % %", url, parent, UrlTool.escape(newName)));
			}
		}
	}
}
