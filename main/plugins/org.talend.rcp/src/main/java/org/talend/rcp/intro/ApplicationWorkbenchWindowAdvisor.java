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
package org.talend.rcp.intro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.activities.IWorkbenchActivitySupport;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.ide.EditorAreaDropAdapter;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.utils.CheatSheetPerspectiveAdapter;
import org.talend.commons.utils.VersionUtils;
import org.talend.commons.utils.workbench.extensions.ExtensionImplementationProvider;
import org.talend.commons.utils.workbench.extensions.ExtensionPointLimiterImpl;
import org.talend.commons.utils.workbench.extensions.IExtensionPointLimiter;
import org.talend.core.CorePlugin;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.ITDQRepositoryService;
import org.talend.core.PluginChecker;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.general.Project;
import org.talend.core.model.repository.RepositoryManager;
import org.talend.core.model.utils.RepositoryManagerHelper;
import org.talend.core.model.utils.TalendPropertiesUtil;
import org.talend.core.prefs.IDEInternalPreferences;
import org.talend.core.prefs.ITalendCorePrefConstants;
import org.talend.core.prefs.hidden.HidePreferencePageProvider;
import org.talend.core.prefs.hidden.HidePreferencePagesManager;
import org.talend.core.prefs.hidden.IHidePreferencePageValidator;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.service.ISQLBuilderService;
import org.talend.core.token.TokenCollectorFactory;
import org.talend.core.ui.CoreUIPlugin;
import org.talend.core.ui.branding.IBrandingConfiguration;
import org.talend.core.ui.branding.IBrandingService;
import org.talend.designer.business.diagram.custom.IDiagramModelService;
import org.talend.designer.core.ui.editor.palette.TalendPaletteHelper;
import org.talend.designer.core.ui.views.properties.IComponentSettingsView;
import org.talend.rcp.Activator;
import org.talend.rcp.i18n.Messages;
import org.talend.rcp.intro.starting.StartingEditorInput;
import org.talend.rcp.intro.starting.StartingHelper;
import org.talend.rcp.util.ApplicationDeletionUtil;
import org.talend.repository.ui.login.connections.ConnectionUserPerReader;
import org.talend.repository.ui.views.IRepositoryView;

/**
 * DOC ccarbone class global comment. Detailled comment <br/>
 * 
 * $Id$
 * 
 */
@SuppressWarnings("restriction")
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    private List<IAction> actions = new ArrayList<IAction>();

    ApplicationActionBarAdvisor adviser = null;

    private IEditorPart startingBrowser;

    public static final IExtensionPointLimiter GLOBAL_ACTIONS = new ExtensionPointLimiterImpl("org.talend.core.global_actions", //$NON-NLS-1$
            "GlobalAction"); //$NON-NLS-1$

    /**
     * constant from org.eclipse.ui.internal.IWorkbenchConstants;
     */
    public class IWorkbenchConstants {

        public static final String TAG_WINDOW = "window"; //$NON-NLS-1$

        public static final String TAG_PAGE = "page"; //$NON-NLS-1$

        public static final String TAG_PERSPECTIVES = "perspectives"; //$NON-NLS-1$

        public static final String TAG_HIDE_MENU = "hide_menu_item_id"; //$NON-NLS-1$

        public static final String TAG_PERSPECTIVE = "perspective"; //$NON-NLS-1$

        public static final String TAG_ID = "id"; //$NON-NLS-1$

    }

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    @Override
    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        adviser = new ApplicationActionBarAdvisor(configurer);
        return adviser;
    }

    @Override
    public void preWindowOpen() {
        // feature 19053
        PerspectiveReviewUtil.setPerspectiveTabsBarSize();
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(1000, 750));
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(true);
        configurer.setShowProgressIndicator(true);
        configurer.setShowPerspectiveBar(true);
        configurer.configureEditorAreaDropListener(new EditorAreaDropAdapter(configurer.getWindow()));
        RepositoryContext repositoryContext = (RepositoryContext) CorePlugin.getContext().getProperty(
                Context.REPOSITORY_CONTEXT_KEY);
        Project project = repositoryContext.getProject();

        IBrandingService service = (IBrandingService) GlobalServiceRegister.getDefault().getService(IBrandingService.class);
        IBrandingConfiguration brandingConfiguration = service.getBrandingConfiguration();
        String appName = service.getFullProductName();
        // TDI-18644
        ProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        boolean localProvider = false;
        try {
            localProvider = factory.isLocalConnectionProvider();
        } catch (PersistenceException e) {
            localProvider = true;
        }
        String buildIdField = " (" + VersionUtils.getVersion() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        if (TalendPropertiesUtil.isHideBuildNumber()) {
            buildIdField = ""; //$NON-NLS-1$
        }
        if (localProvider) {
            configurer
                    .setTitle(appName
                            + buildIdField
                            + " | " + project.getLabel() + " (" //$NON-NLS-1$ //$NON-NLS-2$ 
                            + Messages.getString("ApplicationWorkbenchWindowAdvisor.repositoryConnection") + ": " + ConnectionUserPerReader.getInstance().readLastConncetion() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        } else {
            configurer
                    .setTitle(appName
                            + buildIdField
                            + " | " + repositoryContext.getUser() + " | " + project.getLabel() + " (" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
                            + Messages.getString("ApplicationWorkbenchWindowAdvisor.repositoryConnection") + ": " + ConnectionUserPerReader.getInstance().readLastConncetion() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        ActionBarBuildHelper helper = (ActionBarBuildHelper) brandingConfiguration.getHelper();
        if (helper == null) {
            helper = new ActionBarBuildHelper();
            brandingConfiguration.setHelper(helper);
        }
        helper.preWindowOpen(configurer);

        if (GlobalServiceRegister.getDefault().isServiceRegistered(ITDQRepositoryService.class)) {
            ITDQRepositoryService tdqRepositoryService = (ITDQRepositoryService) GlobalServiceRegister.getDefault().getService(
                    ITDQRepositoryService.class);
            if (tdqRepositoryService != null) {
                tdqRepositoryService.initProxyRepository();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#postWindowOpen()
     */
    @Override
    public void postWindowOpen() {
        // intro

        IPreferenceStore preferenceStore = CorePlugin.getDefault().getPreferenceStore();
        boolean alwaysWelcome = preferenceStore.getBoolean(ITalendCorePrefConstants.ALWAYS_WELCOME);
        if (alwaysWelcome) {
            getWindowConfigurer().getWindow().getWorkbench().getIntroManager()
                    .showIntro(getWindowConfigurer().getWindow(), !alwaysWelcome);
        }

        createActions();
        registerActions();
        adviser.getHelper().postWindowOpen();
        IBrandingService service = (IBrandingService) GlobalServiceRegister.getDefault().getService(IBrandingService.class);
        getWindowConfigurer()
                .setTitle(getWindowConfigurer().getTitle() + service.getBrandingConfiguration().getAdditionalTitle());

        /**
         * PTODO need remove this, if there is not only merging ref-project option in the repository page.(feature 6725)
         * 
         * @see org.talend.designer.core.ui.preferences.RepositoryPreferencePage
         */
        if (!PluginChecker.isRefProjectLoaded()) {
            String[] prefsId = { "org.talend.designer.core.ui.preferences.RepositoryPreferencePage" }; //$NON-NLS-1$
            ApplicationDeletionUtil.removeAndResetPreferencePages(this.getWindowConfigurer().getWindow(), Arrays.asList(prefsId),
                    true);
        } else {
            String[] prefsId = { "org.talend.mdm.repository.ui.preferences.RepositoryPreferencePage" }; //$NON-NLS-1$
            ApplicationDeletionUtil.removeAndResetPreferencePages(this.getWindowConfigurer().getWindow(), Arrays.asList(prefsId),
                    false);
        }
        List<HidePreferencePageProvider> providers = HidePreferencePagesManager.getInstance().getProviders();
        List<String> needRemovedPrefs = new ArrayList<String>();
        for (HidePreferencePageProvider provider : providers) {
            String prefPageId = provider.getPrefPageId();
            IHidePreferencePageValidator validator = provider.getValidator();
            if (prefPageId != null && (validator == null || validator.validate())) {
                needRemovedPrefs.add(prefPageId);
            }
        }
        ApplicationDeletionUtil.removeAndResetPreferencePages(this.getWindowConfigurer().getWindow(), needRemovedPrefs, false);
        // MOD mzhao feature 9207. 2009-09-21 ,Add part listener.
        if (GlobalServiceRegister.getDefault().isServiceRegistered(ITDQRepositoryService.class)) {
            ITDQRepositoryService tdqRepositoryService = (ITDQRepositoryService) GlobalServiceRegister.getDefault().getService(
                    ITDQRepositoryService.class);
            if (tdqRepositoryService != null) {
                tdqRepositoryService.addPartListener();
                tdqRepositoryService.addSoftwareSystemUpdateListener();
            }
        }

        showStarting();
        // feature 18752
        regisitPerspectiveListener();
        // feature 19053
        PerspectiveReviewUtil.regisitPerspectiveBarSelectListener();

        if (PluginChecker.isBPMloaded()) {
            IPath path = WorkbenchPlugin.getDefault().getDataLocation();
            if (path == null) {
                return;
            }
            final File stateFile = path.append("workbench.xml").toFile(); //$NON-NLS-1$
            if (stateFile.exists()) {
                IWorkbenchWindow workBenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                IWorkbenchPage activePage = workBenchWindow.getActivePage();
                FileInputStream input;
                try {
                    input = new FileInputStream(stateFile);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input, "utf-8")); //$NON-NLS-1$
                    IMemento memento = XMLMemento.createReadRoot(reader);
                    IMemento[] hideMenuArray = memento.getChild(IWorkbenchConstants.TAG_WINDOW)
                            .getChild(IWorkbenchConstants.TAG_PAGE).getChild(IWorkbenchConstants.TAG_PERSPECTIVES)
                            .getChild(IWorkbenchConstants.TAG_PERSPECTIVE).getChildren(IWorkbenchConstants.TAG_HIDE_MENU);
                    if (hideMenuArray.length == 0) {
                        activePage.resetPerspective();
                    } else {
                        // if no bonita menue is filtered ,need to recaculate
                        String bonitaMenues = "org.bonitasoft.studio";
                        boolean isBPMFilterWork = false;
                        for (int i = 0; hideMenuArray != null && i < hideMenuArray.length; i++) {
                            IMemento hideMenu = hideMenuArray[i];
                            String string = hideMenu.getString(IWorkbenchConstants.TAG_ID);
                            if (string != null && string.startsWith(bonitaMenues)) {
                                isBPMFilterWork = true;
                                break;
                            }
                        }
                        if (!isBPMFilterWork) {
                            activePage.resetPerspective();
                        }

                    }
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
        // tmp for token
        final IPreferenceStore store = CoreUIPlugin.getDefault().getPreferenceStore();
        if (!store.getBoolean(ITalendCorePrefConstants.DATA_COLLECTOR)) {
            TokenCollectorFactory.getFactory().send(true);
            store.setValue(ITalendCorePrefConstants.DATA_COLLECTOR, true);
            if (store instanceof ScopedPreferenceStore) {
                try {
                    ((ScopedPreferenceStore) store).save();
                } catch (IOException e) {
                    ExceptionHandler.process(e);
                }
            }
        }

        IWorkbenchActivitySupport activitySupport = getWindowConfigurer().getWindow().getWorkbench().getActivitySupport();
        String hideUpdateSiteId = "org.talend.rcp.hideUpdatesite"; //$NON-NLS-1$
        Set<String> enabledActivities = new HashSet<String>();
        enabledActivities.addAll(activitySupport.getActivityManager().getEnabledActivityIds());
        if (!PluginChecker.isSVNProviderPluginLoaded()) {
            if (activitySupport.getActivityManager().getActivity(hideUpdateSiteId).isDefined()) {
                enabledActivities.remove(hideUpdateSiteId);
            }

        } else {
            if (activitySupport.getActivityManager().getActivity(hideUpdateSiteId).isDefined()) {
                enabledActivities.add(hideUpdateSiteId);
            }
        }

        activitySupport.setEnabledActivityIds(enabledActivities);

        IWorkbenchWindowConfigurer workbenchWindowConfigurer = getWindowConfigurer();
        IActionBarConfigurer actionBarConfigurer = workbenchWindowConfigurer.getActionBarConfigurer();
        IMenuManager menuManager = actionBarConfigurer.getMenuManager();

        IContributionItem[] menuItems = menuManager.getItems();
        for (IContributionItem menuItem : menuItems) {
            // Hack to remove the Run menu - it seems you cannot do this using the
            // "org.eclipse.ui.activities" extension
            if ("org.eclipse.ui.run".equals(menuItem.getId())) {
                menuManager.remove(menuItem);
            }
        }

        menuManager.update(true);

    }

    private void showStarting() {
        try {
            IBrandingService service = (IBrandingService) GlobalServiceRegister.getDefault().getService(IBrandingService.class);
            // the first time to call getHtmlContent, if throws any exception ,don't show StartingBrower
            StartingHelper.getHelper().getHtmlContent();
            IWorkbenchPage activePage = getWindowConfigurer().getWindow().getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage();
            if (activePage != null) {
                if (activePage.getPerspective().getId().equals("org.talend.rcp.perspective")) { //$NON-NLS-1$
                    startingBrowser = activePage.openEditor(new StartingEditorInput(service), service.getStartingBrowserId());
                }
            }
        } catch (Exception e) {
            // do nothing
        }
    }

    private void regisitPerspectiveListener() {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(new PerspectiveAdapter() {

            @Override
            public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
                // MOD xqliu 2010-10-14 bug 15756
                String pId = perspective.getId();

                if (IBrandingConfiguration.PERSPECTIVE_DI_ID.equals(pId)
                        || IBrandingConfiguration.PERSPECTIVE_CAMEL_ID.equals(pId)) {
                    IRepositoryView view = RepositoryManager.getRepositoryView();
                    if (view != null) {
                        /* 0016610 need to refresh not only databaseconnection but only trash bin */
                        view.refresh();
                        TalendPaletteHelper.checkAndInitToolBar();
                    }
                } else if (IBrandingConfiguration.PERSPECTIVE_DQ_ID.equals(pId)) {
                    if (GlobalServiceRegister.getDefault().isServiceRegistered(ITDQRepositoryService.class)) {
                        ITDQRepositoryService tdqRepositoryService = (ITDQRepositoryService) GlobalServiceRegister.getDefault()
                                .getService(ITDQRepositoryService.class);
                        if (tdqRepositoryService != null) {
                            tdqRepositoryService.refresh();
                        }
                    }
                }
                // ~ 15756
            }

            @Override
            public void perspectiveDeactivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
                String pId = perspective.getId();
                if (IBrandingConfiguration.PERSPECTIVE_DI_ID.equals(pId)) {
                    IComponentSettingsView viewer = (IComponentSettingsView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                            .getActivePage().findView(IComponentSettingsView.ID);

                    if (viewer != null) {
                        viewer.cleanDisplay();
                    }
                }
            }
        });

        // MOD yyi 2011-05-17 19088: add perspective change listener for cheatsheet view of tdq
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(new CheatSheetPerspectiveAdapter());
    }

    /**
     * DOC smallet Comment method "createActions".
     */
    private void createActions() {

        List<IAction> list = ExtensionImplementationProvider.getInstance(GLOBAL_ACTIONS);
        actions.addAll(list);
    }

    /**
     * DOC smallet Comment method "registerActions".
     */
    private void registerActions() {
        IContextService contextService = (IContextService) Activator.getDefault().getWorkbench()
                .getAdapter(IContextService.class);
        contextService.activateContext("talend.global"); //$NON-NLS-1$

        IWorkbench workbench = PlatformUI.getWorkbench();
        IHandlerService handlerService = (IHandlerService) workbench.getService(IHandlerService.class);

        IHandler handler;
        for (IAction action : actions) {
            handler = new ActionHandler(action);
            handlerService.activateHandler(action.getActionDefinitionId(), handler);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#postWindowClose()
     */
    @Override
    public void postWindowClose() {
        if (GlobalServiceRegister.getDefault().isServiceRegistered(ISQLBuilderService.class)) {
            ISQLBuilderService service = (ISQLBuilderService) GlobalServiceRegister.getDefault().getService(
                    ISQLBuilderService.class);
            Shell[] shelles = Display.getDefault().getShells();
            for (Shell shell : shelles) {
                if (!shell.isDisposed() && shell.getData() != null) {
                    service.closeIfSQLBuilderDialog(shell.getData());
                }
            }
        }
        super.postWindowClose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#preWindowShellClose()
     */

    @Override
    public boolean preWindowShellClose() {

        if (PlatformUI.getWorkbench().getWorkbenchWindowCount() > 1) {
            return true;
        }
        IPreferenceStore store = IDEWorkbenchPlugin.getDefault().getPreferenceStore();
        boolean promptOnExit = store.getBoolean(IDEInternalPreferences.EXIT_PROMPT_ON_CLOSE_LAST_WINDOW);

        if (promptOnExit) {
            // String message = IDEWorkbenchMessages.PromptOnExitDialog_message0;
            String message = Messages.getString("ApplicationWorkbenchWindowAdvisor.PromptOnExitDialog_message0"); //$NON-NLS-1$

            // MessageDialogWithToggle dlg =
            // MessageDialogWithToggle.openOkCancelConfirm(getWindowConfigurer().getWindow()
            // .getShell(), IDEWorkbenchMessages.PromptOnExitDialog_shellTitle, message,
            // IDEWorkbenchMessages.PromptOnExitDialog_choice, false, null, null);
            MessageDialogWithToggle dlg = MessageDialogWithToggle.openOkCancelConfirm(getWindowConfigurer().getWindow()
                    .getShell(), Messages.getString("ApplicationWorkbenchWindowAdvisor.PromptOnExitDialog_shellTitle"), message, //$NON-NLS-1$
                    Messages.getString("ApplicationWorkbenchWindowAdvisor.PromptOnExitDialog_choice"), false, null, null); //$NON-NLS-1$
            if (dlg.getReturnCode() != IDialogConstants.OK_ID) {
                return false;
            }
            if (dlg.getToggleState()) {
                store.setValue(IDEInternalPreferences.EXIT_PROMPT_ON_CLOSE_LAST_WINDOW, false);
                // MOD qiongli 2010-11-1,bug 16723: Code Cleansing
                // IDEWorkbenchPlugin.getDefault().savePluginPreferences();
                try {// FIXME, need check it for tdq
                    new InstanceScope().getNode(Activator.PLUGIN_ID).flush();
                } catch (BackingStoreException e) {
                    e.printStackTrace();
                }
            }
        }

        // for bug 7071
        IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (GlobalServiceRegister.getDefault().isServiceRegistered(IDiagramModelService.class)) {
            if (workbenchWindow.getActivePage() != null
                    && CorePlugin.getDefault().getDiagramModelService()
                            .isBusinessDiagramEditor(workbenchWindow.getActivePage().getActiveEditor())) {
                IViewReference findViewReference = RepositoryManagerHelper.findRepositoryViewRef();
                if (findViewReference != null) {
                    findViewReference.getView(false).setFocus();
                }

            }
        }
        // tmp for data collector
        TokenCollectorFactory.getFactory().process();

        return true;
    }
}