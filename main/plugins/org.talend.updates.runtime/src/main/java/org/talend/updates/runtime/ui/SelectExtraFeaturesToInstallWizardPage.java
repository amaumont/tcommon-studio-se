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
package org.talend.updates.runtime.ui;

import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.talend.updates.runtime.i18n.Messages;
import org.talend.updates.runtime.model.ExtraFeature;

/**
 * created by sgandon on 25 févr. 2013 Detailled comment
 * 
 */
public class SelectExtraFeaturesToInstallWizardPage extends WizardPage {

    private Table table;

    private final UpdateWizardModel updateWizardModel;

    private CheckboxTableViewer checkboxTableViewer;

    private StyledText featureDescriptionText;

    public DataBindingContext dbc;

    /**
     * Create the wizard.
     * 
     * @param updateWizardModel
     * @wbp.parser.constructor
     */
    public SelectExtraFeaturesToInstallWizardPage(UpdateWizardModel updateWizardModel) {
        super("wizardPage"); //$NON-NLS-1$
        this.updateWizardModel = updateWizardModel;
        setTitle(Messages.getString("SelectExtraFeaturesToInstallWizardPage.wizard.page.title")); //$NON-NLS-1$
        setDescription(Messages.getString("SelectExtraFeaturesToInstallWizardPage.wizard.page.description")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
     */
    @Override
    public boolean canFlipToNextPage() {
        return super.canFlipToNextPage() && !updateWizardModel.selectedExtraFeatures.isEmpty()
                && updateWizardModel.canConfigureUpdateSiteLocation();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
     */
    @Override
    public boolean isPageComplete() {
        return updateWizardModel.hasDoNotShowThisAgainChanged ? true : super.isPageComplete();
    }

    /**
     * Create contents of the wizard.
     * 
     * @param parent
     */
    @SuppressWarnings("unchecked")
    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);

        setControl(container);
        container.setLayout(new GridLayout(2, false));

        Label lblNewLabel = new Label(container, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblNewLabel.setText(Messages.getString("SelectExtraFeaturesToInstallWizardPage.feature.list.label")); //$NON-NLS-1$

        Composite featureComposite = new Composite(container, SWT.NONE);
        featureComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        TableColumnLayout friendsColumnLayout = new TableColumnLayout();
        featureComposite.setLayout(friendsColumnLayout);

        checkboxTableViewer = CheckboxTableViewer.newCheckList(featureComposite, SWT.BORDER | SWT.FULL_SELECTION);
        checkboxTableViewer.setSorter(new ViewerSorter() {// regroupt by class type and then by name

                    /*
                     * (non-Javadoc)
                     * 
                     * @see org.eclipse.jface.viewers.ViewerComparator#category(java.lang.Object)
                     */
                    @Override
                    public int category(Object element) {
                        return element.getClass().hashCode();
                    }
                });
        table = checkboxTableViewer.getTable();
        table.setSize(400, 155);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        TableColumn featureNameColumn = new TableColumn(table, SWT.NONE);
        featureNameColumn.setText(Messages.getString("SelectExtraFeaturesToInstallWizardPage.feature.column.name.name")); //$NON-NLS-1$
        friendsColumnLayout.setColumnData(featureNameColumn, new ColumnWeightData(3, true));

        TableColumn tblclmnVersion = new TableColumn(table, SWT.NONE);
        friendsColumnLayout.setColumnData(tblclmnVersion, new ColumnWeightData(1, true));
        tblclmnVersion.setText(Messages.getString("SelectExtraFeaturesToInstallWizardPage.features.column.version.name")); //$NON-NLS-1$

        Label lblDescription = new Label(container, SWT.NONE);
        lblDescription.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblDescription.setText(Messages.getString("SelectExtraFeaturesToInstallWizardPage.description.label")); //$NON-NLS-1$

        featureDescriptionText = new StyledText(container, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
        GridData gd_featureDescriptionText = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gd_featureDescriptionText.heightHint = 61;
        featureDescriptionText.setLayoutData(gd_featureDescriptionText);
        initDataBindings();
        updateSelectedState(updateWizardModel.availableExtraFeatures);
        updatePageStatus();
    }

    protected DataBindingContext initDataBindings() {
        dbc = new DataBindingContext();
        // bind the table elements with the updateWizardModel availableExtraFeatures and the columns with the
        // extrafeatures values
        ViewerSupport.bind(checkboxTableViewer, updateWizardModel.availableExtraFeatures,
                new IValueProperty[] { PojoProperties.value(ExtraFeature.class, "name"), //$NON-NLS-1$
                        PojoProperties.value(ExtraFeature.class, "version") }); //$NON-NLS-1$
        // bind selecting of the check boxes to the selected extra features set in the model
        dbc.bindSet(ViewersObservables.observeCheckedElements(checkboxTableViewer, ExtraFeature.class),
                updateWizardModel.selectedExtraFeatures);
        // bind the table selection desctiption to the text field
        IObservableValue selectedFeature = ViewersObservables.observeSingleSelection(checkboxTableViewer);
        dbc.bindValue(SWTObservables.observeText(featureDescriptionText),
                PojoObservables.observeDetailValue(selectedFeature, "description", String.class)); //$NON-NLS-1$
        // add a validator for feature selection because SetObservable does not provide any validator.
        dbc.addValidationStatusProvider(updateWizardModel.new FeatureSelectionValidator());
        WizardPageSupport.create(this, dbc);
        // add a listener to update the description and enabled state when avaialble features are added and also
        // add them to the selected list if the must be installed
        updateWizardModel.availableExtraFeatures.addSetChangeListener(new ISetChangeListener() {

            @Override
            public void handleSetChange(SetChangeEvent arg0) {
                updatePageStatus();
                updateSelectedState(arg0.diff.getAdditions());
            }
        });
        return dbc;

    }

    /**
     * when an element is added to the feature list this get called so that the selected state of the item gets
     * automatically set according to it's mustBeInstalled value
     * 
     * @param arg0
     */
    protected void updateSelectedState(Set<ExtraFeature> features) {
        for (ExtraFeature ef : features) {
            if (ef.mustBeInstalled()) {
                updateWizardModel.selectedExtraFeatures.add(ef);
            }// else do not select caus must not be installed
        }
    }

    /**
     * DOC sgandon Comment method "updateEnabledStatus".
     */
    protected void updatePageStatus() {
        // tell the user when no feature is to be installed.
        if (updateWizardModel.availableExtraFeatures.isEmpty()) {
            featureDescriptionText.setText(Messages.getString("SelectExtraFeaturesToInstallWizardPage.no.feauture.to.install")); //$NON-NLS-1$
            // table.setEnabled(false);
        } else {
            featureDescriptionText.setText(""); //$NON-NLS-1$
        }
    }
}
