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
package org.talend.core.model.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.commons.i18n.internal.Messages;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.utils.sql.metadata.constants.MetaDataConstants;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * created by xqliu on 2014-10-29 Detailled comment
 * 
 */
public class NetezzaConnectionFiller extends DBConnectionFillerImpl {

    private static Logger log = Logger.getLogger(NetezzaConnectionFiller.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.core.model.metadata.DBConnectionFillerImpl#fillCatalogs(org.talend.core.model.metadata.builder.connection
     * .DatabaseConnection, java.sql.DatabaseMetaData, org.talend.core.model.metadata.IMetadataConnection,
     * java.util.List)
     */
    @Override
    public List<Catalog> fillCatalogs(DatabaseConnection dbConn, DatabaseMetaData dbJDBCMetadata,
            IMetadataConnection metaConnection, List<String> catalogFilter) {
        // catalogFilter is always empty for Netezza connection ???
        List<Catalog> catalogList = new ArrayList<Catalog>();
        String catalogName = dbConn.getSID();

        if (StringUtils.isEmpty(catalogName)) {
            catalogName = getCatalogNameFromUrl(metaConnection.getUrl());
        }

        if (StringUtils.isEmpty(catalogName)) {
            log.error(Messages.getString("NetezzaConnectionFiller.emptyCalalogName")); //$NON-NLS-1$
            return catalogList;
        }

        Catalog catalog = CatalogHelper.createCatalog(catalogName);
        catalogList.add(catalog);

        List<String> filterList = new ArrayList<String>();
        List<Schema> schemaList = new ArrayList<Schema>();
        try {
            schemaList = fillSchemaToCatalog(dbConn, dbJDBCMetadata, catalog, filterList);
            if (!schemaList.isEmpty()) {
                CatalogHelper.addSchemas(schemaList, catalog);
            }
        } catch (Throwable e) {
            log.info(e);
        }
        ConnectionHelper.addCatalogs(catalogList, dbConn);

        return catalogList;
    }

    private String getCatalogNameFromUrl(String url) {
        int lastIndexOf1 = StringUtils.lastIndexOf(url, "/"); //$NON-NLS-1$
        if (StringUtils.isBlank(url) || lastIndexOf1 < 0 || lastIndexOf1 > url.length() - 1) {
            return StringUtils.EMPTY;
        }
        int lastIndexOf2 = StringUtils.lastIndexOf(url, "?"); //$NON-NLS-1$
        if (lastIndexOf2 < 0) {
            return StringUtils.substring(url, lastIndexOf1 + 1);
        } else {
            if (lastIndexOf2 < lastIndexOf1) {
                return StringUtils.EMPTY;
            }
            return StringUtils.substring(url, lastIndexOf1 + 1, lastIndexOf2);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.metadata.DBConnectionFillerImpl#getSchemaName(java.sql.ResultSet,
     * java.sql.DatabaseMetaData, orgomg.cwm.resource.relational.Catalog)
     */
    @Override
    protected String getSchemaName(ResultSet schemaRs, DatabaseMetaData dbJDBCMetadata, Catalog catalog) {
        String schemaName = null;
        String catalogName = null;
        try {
            schemaName = schemaRs.getString(MetaDataConstants.TABLE_SCHEM.name());
            catalogName = schemaRs.getString(MetaDataConstants.TABLE_CATALOG.name());

            if (catalogName != null && !StringUtils.equalsIgnoreCase(catalogName, catalog.getName())) {
                return null;
            }
        } catch (Exception e) {
            log.warn(e);
        }
        return schemaName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.core.model.metadata.DBConnectionFillerImpl#fillSchemas(org.talend.core.model.metadata.builder.connection
     * .DatabaseConnection, java.sql.DatabaseMetaData, org.talend.core.model.metadata.IMetadataConnection,
     * java.util.List)
     */
    @Override
    public List<Package> fillSchemas(DatabaseConnection dbConn, DatabaseMetaData dbJDBCMetadata,
            IMetadataConnection metaConnection, List<String> schemaFilter) {
        return new ArrayList<Package>();
    }
}
