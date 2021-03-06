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
package org.talend.helpers.ui.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.talend.core.model.components.IComponent;
import org.talend.core.model.components.IComponentsFactory;
import org.talend.helpers.i18n.Messages;
import org.talend.repository.model.ComponentsFactoryProvider;

/**
 * DOC smallet class global comment. Detailled comment <br/>
 * 
 * $Id$
 * 
 */
public class LogLoadedComponentsAction extends Action implements IWorkbenchWindowActionDelegate {

    private static Logger log = Logger.getLogger(LogLoadedComponentsAction.class);

    public LogLoadedComponentsAction() {
        super();
        this.setActionDefinitionId("logLoadedComponents"); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        IComponentsFactory componentsFactory = ComponentsFactoryProvider.getInstance();
        for (IComponent component : componentsFactory.getComponents()) {
            log.info(Messages.getString("LogLoadedComponentsAction.componentLog", component.getName())); //$NON-NLS-1$
        }
    }

    public void dispose() {
    }

    public void init(IWorkbenchWindow window) {
    }

    public void run(IAction action) {
        run();
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }
}
