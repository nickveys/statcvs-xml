package de.berlios.statcvs.xml.eclipse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.statcvs.input.Builder;
import net.sf.statcvs.input.CvsLogfileParser;
import net.sf.statcvs.input.EmptyRepositoryException;
import net.sf.statcvs.input.LogSyntaxException;
import net.sf.statcvs.input.RepositoryFileManager;
import net.sf.statcvs.model.CvsBranch;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.CvsRevision;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.part.ViewPart;

public class StatCvsXMLView extends ViewPart {

	private Canvas canvas;
	private Action openAction;
	private Action doubleClickAction;
	private CvsContent content;
	private Figure view;
	private ToolbarLayout viewLayout;
	private Action addFileAction;
	private Map figureByRevision = new HashMap();

	/**
	 * The constructor.
	 */
	public StatCvsXMLView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		canvas = new Canvas(parent, SWT.NONE);
		LightweightSystem lws = new LightweightSystem(canvas);
        view = new Figure();
        viewLayout = new ToolbarLayout(false);
		viewLayout.setSpacing(10);
        view.setLayoutManager(viewLayout);
        lws.setContents(view);

		
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				StatCvsXMLView.this.fillContextMenu(manager);
			}
		});
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(openAction);
		manager.add(addFileAction);
		manager.add(new Separator());
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(openAction);
		manager.add(addFileAction);
		manager.add(new Separator());
	}

	private void makeActions() {
		openAction = new Action() {
			public void run() {
				FileDialog dialog = new FileDialog(getSite().getShell(), SWT.OPEN);
				String initialFilename = StatCvsXMLPlugin.getDefault().getPluginPreferences().getString("filename");
				if (initialFilename != null) {
					File file = new File(initialFilename);
					dialog.setFilterPath(file.getParent());
					dialog.setFileName(file.getName());
				}
				final String filename = dialog.open();
				if (filename != null) {
					IRunnableWithProgress runner = new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
							try {
								openCvsLog(filename);
							} catch (Exception e) {
								throw new InvocationTargetException(e);
							}
						}
					};
					try {
						new ProgressMonitorDialog(getSite().getShell()).run(true, true, runner);
						StatCvsXMLPlugin.getDefault().getPluginPreferences().setValue("filename", filename);
						addFileAction.setEnabled(true);
						openAddFileDialog();
					} catch (InvocationTargetException e) {
						ErrorDialog.openError(getSite().getShell(), "Error", "Error Opening Cvs Log",
								new Status(IStatus.WARNING, "id", 0, e.getTargetException().getLocalizedMessage(), null));
					} catch (InterruptedException e) {
					}
				}
			}
		};
		openAction.setText("Open");
		openAction.setToolTipText("Open a CVS log file.");
		openAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER));

		addFileAction = new Action() {
			public void run() {
				openAddFileDialog();
			}
		};
		addFileAction.setText("Add File...");
		addFileAction.setToolTipText("Opens a dialog for adding a file to the view.");
		addFileAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));
		addFileAction.setEnabled(false);

	}

	private void hookDoubleClickAction() {
//		canvas.addDoubleClickListener(new IDoubleClickListener() {
//			public void doubleClick(DoubleClickEvent event) {
//				doubleClickAction.run();
//			}
//		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		canvas.setFocus();
	}

	/**
	 * @param filename
	 * @throws FileNotFoundException
	 * @throws LogSyntaxException
	 * @throws IOException
	 * @throws EmptyRepositoryException 
	 * @throws EmptyRepositoryException
	 */
	public void openCvsLog(String filename) throws LogSyntaxException, IOException, EmptyRepositoryException {
		File file = new File(filename);
		Reader logReader = new FileReader(file);
		RepositoryFileManager fileManager = new RepositoryFileManager(file.getParent());
		Builder builder = new Builder(fileManager, null, null, null);
		new CvsLogfileParser(logReader, builder).parse();
		content = builder.createCvsContent();
	}
	
	public class FileFigure extends Figure {
		
		Figure branchContainer;
		
		public FileFigure(CvsFile file) {
			ToolbarLayout layout = new ToolbarLayout(true);
			layout.setSpacing(10);
			setLayoutManager(layout);   
			//setBorder(new LineBorder(ColorConstants.black, 1));
			setOpaque(true);
			
			add(new Label(file.getFilename()));
			
			branchContainer = new Figure();
			layout = new ToolbarLayout(false);
			layout.setSpacing(15);
			branchContainer.setLayoutManager(layout);
			add(branchContainer);
		}
	}
	
	public class RevisionFigure extends Figure {
		
		public Color classColor = new Color(null, 255, 255, 206);
		
		public RevisionFigure(CvsRevision revision) {
			ToolbarLayout layout = new ToolbarLayout();
			setLayoutManager(layout);   
			setBorder(new LineBorder(ColorConstants.black, 1));
			setBackgroundColor(classColor);
			setOpaque(true);
			
			add(new Label(revision.getRevisionNumber()));  
		}

	}

	public void openAddFileDialog() {
		ILabelProvider provider = new LabelProvider() {
			public String getText(Object element) {
				return ((CvsFile)element).getFilenameWithPath();
			}
		};

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getViewSite().getShell(), provider);
		dialog.setElements(content.getFiles().toArray());
		dialog.open();
		CvsFile file = (CvsFile)dialog.getFirstResult();
		if (file != null) {
			addFile(file);
		}
	}

	public void addFile(CvsFile file) {
		FileFigure fileFigure = new FileFigure(file);
		view.add(fileFigure);
		
		for (Iterator it = content.getBranches().iterator(); it.hasNext();) {
			CvsBranch branch = (CvsBranch) it.next();

			if (file.existsOnBranch(branch.getName())) {
				Figure branchFigure = new Figure();
				ToolbarLayout layout = new ToolbarLayout(true);
				layout.setSpacing(10);
				branchFigure.setLayoutManager(layout);   
				fileFigure.branchContainer.add(branchFigure);
				
				for (Iterator it2 = file.getRevisions(branch.getName()).iterator(); it2.hasNext();) {
					CvsRevision revision = (CvsRevision)it2.next();
					addRevision(branchFigure, revision);
				}
			}
		}
	}
	
	public void addRevision(Figure parent, CvsRevision revision) {
		RevisionFigure figure = new RevisionFigure(revision);
		figureByRevision .put(revision, figure);
		parent.add(figure);

		if (revision.getPreviousRevision() != null) {
			Figure targetFigure = (Figure)figureByRevision.get(revision.getPreviousRevision());
			PolylineConnection c = new PolylineConnection();
			ChopboxAnchor sourceAnchor = new ChopboxAnchor(figure);
			ChopboxAnchor targetAnchor = new ChopboxAnchor(targetFigure);
			c.setSourceAnchor(sourceAnchor);
			c.setTargetAnchor(targetAnchor);
			
			PolygonDecoration decoration = new PolygonDecoration();
			decoration.setTemplate(PolygonDecoration.TRIANGLE_TIP);
			c.setTargetDecoration(decoration);
			
			view.add(c);
		}
	}
	
}