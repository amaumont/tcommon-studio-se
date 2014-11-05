// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.librariesmanager.ui.actions;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.image.ECoreImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.commons.utils.generation.JavaUtils;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.librariesmanager.ui.LibManagerUiPlugin;
import org.talend.librariesmanager.ui.i18n.Messages;

/**
 * Imports the external jar files into talend.
 * 
 * $Id: ImportExternalJarAction.java Mar 15, 20075:58:30 PM bqian $
 * 
 */
public class ImportExternalJarAction extends Action {

    /**
     * DOC acer ImportExternalJarAction constructor comment.
     */
    public ImportExternalJarAction() {
        super();
        this.setText(Messages.getString("ImportExternalJarAction.title")); //$NON-NLS-1$
        this.setDescription(Messages.getString("ImportExternalJarAction.title")); //$NON-NLS-1$
        this.setImageDescriptor(ImageProvider.getImageDesc(ECoreImage.IMPORT_JAR));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {

        handleImportJarDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
    }

    /**
     * DOC sgandon Comment method "handleImportJarDialog".
     * 
     * @param shell, to display the dialog box
     * @return, list of imported file names, may be empty
     */
    public String[] handleImportJarDialog(Shell shell) {
        FileDialog fileDialog = new FileDialog(shell, SWT.OPEN | SWT.MULTI);
        fileDialog.setFilterExtensions(FilesUtils.getAcceptJARFilesSuffix());
        fileDialog.open();
        final String path = fileDialog.getFilterPath();
        final String[] fileNames = fileDialog.getFileNames();

        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {

            @Override
            public void run() {
                for (String fileName : fileNames) {

                    File file = new File(path + File.separatorChar + fileName);
                    try {
                        LibManagerUiPlugin.getDefault().getLibrariesService().deployLibrary(file.toURL());
                        // emptyLibs();
                    } catch (Exception e) {
                        ExceptionHandler.process(e);
                    }
                }
            }
        });
        return fileNames;
    }

    private void emptyLibs() {
        File libsDir = org.eclipse.core.runtime.Platform.getLocation().append(JavaUtils.JAVA_PROJECT_NAME).append(File.separator)
                .append(JavaUtils.JAVA_LIB_DIRECTORY).toFile();
        if (libsDir.exists() && libsDir.isDirectory()) {
            FilesUtils.emptyFolder(libsDir);
        }
    }
}
