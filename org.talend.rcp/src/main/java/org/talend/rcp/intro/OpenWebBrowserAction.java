// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.rcp.intro;

import java.util.Properties;

import org.eclipse.swt.program.Program;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.ui.branding.IBrandingService;

/**
 * DOC hcyi class global comment. Detailled comment
 */
public class OpenWebBrowserAction implements IIntroAction {

    private static final String USERGUIDE_URL = "http://www.talend.com/download/data-integration?qt-product_download_tabs=2#qt-product_download_tabs"; //$NON-NLS-1$

    private static final String REFERENCEGUIDE_URL = "http://www.talend.com/download/data-integration?qt-product_download_tabs=2#qt-product_download_tabs"; //$NON-NLS-1$

    private static final String TUTORIALS_URL = "http://www.talendforge.org/tutorials"; //$NON-NLS-1$

    private static final String FORUMS_URL = "http://www.talendforge.org/forum"; //$NON-NLS-1$

    private static final String EXCHANGE_URL = "http://www.talendforge.org/exchange"; //$NON-NLS-1$

    private static final String TOS_DI = "http://www.talend.com/download/data-integration?qt-product_download_tabs=2#qt-product_download_tabs"; //$NON-NLS-1$

    private static final String TOS_DQ = "http://www.talend.com/download/data-quality?qt-product_download_tabs=2#qt-product_download_tabs"; //$NON-NLS-1$

    private static final String TOS_ESB = "http://www.talend.com/download/esb?qt-product_download_tabs=2#qt-product_download_tabs"; //$NON-NLS-1$

    private static final String TOS_MDM = "http://www.talend.com/download/mdm?qt-product_download_tabs=2#qt-product_download_tabs"; //$NON-NLS-1$

    private static final String TOS_BD = "http://www.talend.com/download/big-data?qt-product_download_tabs=2#qt-product_download_tabs"; //$NON-NLS-1$

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.intro.config.IIntroAction#run(org.eclipse.ui.intro.IIntroSite, java.util.Properties)
     */
    public void run(IIntroSite site, Properties params) {
        if (params == null) {
            return;
        }
        Object type = params.get("type");
        if (type != null) {
            if ("showUserGuide".equals(type.toString())) {
                if (GlobalServiceRegister.getDefault().isServiceRegistered(IBrandingService.class)) {
                    IBrandingService brandingService = (IBrandingService) GlobalServiceRegister.getDefault().getService(
                            IBrandingService.class);
                    String userManuals = brandingService.getUserManuals();
                    if ("DI".equals(userManuals)) {
                        Program.launch(TOS_DI);
                    } else if ("DQ".equals(userManuals)) {
                        Program.launch(TOS_DQ);
                    } else if ("ESB".equals(userManuals)) {
                        Program.launch(TOS_ESB);
                    } else if ("MDM".equals(userManuals)) {
                        Program.launch(TOS_MDM);
                    } else if ("BD".equals(userManuals)) {
                        Program.launch(TOS_BD);
                    }
                }
            } else if ("showReferenceGuide".equals(type.toString())) {
                Program.launch(REFERENCEGUIDE_URL);
            } else if ("showTutorials".equals(type.toString())) {
                Program.launch(TUTORIALS_URL);
            } else if ("showForums".equals(type.toString())) {
                Program.launch(FORUMS_URL);
            } else if ("showExchange".equals(type.toString())) {
                Program.launch(EXCHANGE_URL);
            }
        }
    }
}