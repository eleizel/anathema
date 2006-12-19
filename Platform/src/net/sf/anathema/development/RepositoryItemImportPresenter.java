package net.sf.anathema.development;

import java.awt.Component;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.disy.commons.core.message.Message;
import net.disy.commons.swing.action.SmartAction;
import net.disy.commons.swing.dialog.message.MessageDialogFactory;
import net.sf.anathema.framework.item.IItemType;
import net.sf.anathema.framework.repository.RepositoryException;
import net.sf.anathema.framework.repository.access.IRepositoryWriteAccess;
import net.sf.anathema.framework.repository.tree.RepositoryTreeModel;
import net.sf.anathema.framework.repository.tree.RepositoryTreeView;
import net.sf.anathema.lib.collection.MultiEntryMap;
import net.sf.anathema.lib.gui.IPresenter;
import net.sf.anathema.lib.gui.file.FileChoosingUtilities;
import net.sf.anathema.lib.logging.Logger;
import net.sf.anathema.lib.resources.IResources;

public class RepositoryItemImportPresenter implements IPresenter {

  private final IResources resources;
  private final RepositoryTreeModel model;
  private final RepositoryTreeView view;
  private final RepositoryZipPathCreator creator;

  public RepositoryItemImportPresenter(
      IResources resources,
      RepositoryTreeModel repositoryTreeModel,
      RepositoryTreeView treeView) {
    this.resources = resources;
    this.model = repositoryTreeModel;
    this.view = treeView;
    this.creator = new RepositoryZipPathCreator(model.getRepositoryPath());
  }

  public void initPresentation() {
    final SmartAction action = new SmartAction("Import") {
      @Override
      protected void execute(Component parentComponent) {
        try {
          File loadFile = FileChoosingUtilities.chooseFile("Import", parentComponent, new ZipFileFilter());
          ZipFile importZipFile = new ZipFile(loadFile);
          Enumeration< ? extends ZipEntry> entries = importZipFile.entries();
          MultiEntryMap<String, ZipEntry> entriesByComment = new MultiEntryMap<String, ZipEntry>();
          for (; entries.hasMoreElements();) {
            ZipEntry entry = entries.nextElement();
            entriesByComment.add(entry.getComment(), entry);
          }
          for (String comment : entriesByComment.keySet()) {
            String[] splitComment = comment.split("#"); //$NON-NLS-1$
            if (!splitComment[0].equals(resources.getString("Anathema.Version.Numeric"))) { //$NON-NLS-1$
              continue;
            }
            IItemType type = model.getItemTypeForId(splitComment[1]);
            String id = splitComment[2];
            IRepositoryWriteAccess access = model.getWriteAccess(type, id);
            String mainFilePath = creator.createZipPath(model.getMainFilePath(type, id));
            for (ZipEntry entry : entriesByComment.get(comment)) {
              if (entry.getName().equals(mainFilePath)) {
                InputStream inputStream = importZipFile.getInputStream(entry);
                OutputStream outputStream = access.createMainOutputStream();
                writeFileToRepository(inputStream, outputStream);
              }
              // TODO: MultiFileItems
              // TODO: Unique IDs
              // String uniqueId = model.createUniqueId(type, id);
            }
          }
          importZipFile.close();
        }
        catch (IOException e) {
          MessageDialogFactory.showMessageDialog(parentComponent, new Message(
              resources.getString("AnathemaCore.Tools.RepositoryView.ImportError"),
              e));
          Logger.getLogger(getClass()).error(e);
        }
        catch (RepositoryException e) {
          MessageDialogFactory.showMessageDialog(parentComponent, new Message(
              resources.getString("AnathemaCore.Tools.RepositoryView.ImportError"),
              e));
          Logger.getLogger(getClass()).error(e);
        }
      }

    };
    view.addActionButton(action);
  }

  private void writeFileToRepository(InputStream importStream, OutputStream repositoryStream) throws IOException {
    byte buffer[] = new byte[512];
    int lengthRead = 0;
    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(repositoryStream, 512);
    while ((lengthRead = importStream.read(buffer)) != -1) {
      bufferedOutputStream.write(buffer, 0, lengthRead);
    }
    bufferedOutputStream.close();
    importStream.close();
  }
}